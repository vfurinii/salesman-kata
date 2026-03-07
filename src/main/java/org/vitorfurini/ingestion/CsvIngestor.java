package org.vitorfurini.ingestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vitorfurini.domain.DataLineage;
import org.vitorfurini.domain.SalesRecord;
import org.vitorfurini.messaging.KafkaProducer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvIngestor {

    private final KafkaProducer kafkaProducer;

    @Value("${csv.input.directory:./data/csv}")
    private String csvDirectory;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void ingestCsvFiles() {
        log.info("Starting CSV data ingestion from directory: {}", csvDirectory);

        File directory = new File(csvDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            log.warn("CSV directory does not exist: {}", csvDirectory);
            return;
        }

        File[] csvFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (csvFiles == null || csvFiles.length == 0) {
            log.warn("No CSV files found in directory: {}", csvDirectory);
            return;
        }

        for (File csvFile : csvFiles) {
            processCsvFile(csvFile);
        }
    }

    private void processCsvFile(File csvFile) {
        log.info("Processing CSV file: {}", csvFile.getName());

        try (Reader reader = new FileReader(csvFile);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {

            int count = 0;
            for (CSVRecord csvRecord : csvParser) {
                try {
                    SalesRecord salesRecord = SalesRecord.builder()
                            .saleId(csvRecord.get("sale_id"))
                            .productType(csvRecord.get("product_type"))
                            .productName(csvRecord.get("product_name"))
                            .amount(new BigDecimal(csvRecord.get("amount")))
                            .salespersonId(csvRecord.get("salesperson_id"))
                            .salespersonName(csvRecord.get("salesperson_name"))
                            .country(csvRecord.get("country"))
                            .city(csvRecord.get("city"))
                            .warehouse(csvRecord.get("warehouse"))
                            .retailer(csvRecord.get("retailer"))
                            .saleDate(LocalDateTime.parse(csvRecord.get("sale_date"), DATE_FORMATTER))
                            .source("CSV")
                            .ingestedAt(LocalDateTime.now())
                            .build();

                    kafkaProducer.sendRawSales(salesRecord);

                    // Track lineage
                    DataLineage lineage = DataLineage.builder()
                            .lineageId(UUID.randomUUID().toString())
                            .recordId(salesRecord.getSaleId())
                            .source("CSV")
                            .stage("RAW")
                            .transformation("Ingested from CSV file: " + csvFile.getName())
                            .timestamp(LocalDateTime.now())
                            .status("SUCCESS")
                            .build();
                    kafkaProducer.sendLineage(lineage);

                    count++;
                } catch (Exception e) {
                    log.error("Error processing CSV record", e);
                }
            }

            log.info("Ingested {} records from CSV file: {}", count, csvFile.getName());

        } catch (IOException e) {
            log.error("Error reading CSV file: {}", csvFile.getName(), e);
        }
    }
}

