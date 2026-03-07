package org.vitorfurini.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.vitorfurini.ingestion.CsvIngestor;
import org.vitorfurini.ingestion.PostgresIngestor;
import org.vitorfurini.ingestion.SoapIngestor;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataIngestionOrchestrator {

    private final PostgresIngestor postgresIngestor;
    private final CsvIngestor csvIngestor;
    private final SoapIngestor soapIngestor;

    // Run every Monday at 6:00 AM
    @Scheduled(cron = "0 0 6 * * MON")
    public void runScheduledIngestion() {
        log.info("Starting scheduled data ingestion");
        runFullIngestion();
    }

    public void runFullIngestion() {
        log.info("Running full data ingestion from all sources");

        // Ingest from PostgreSQL
        try {
            postgresIngestor.ingestSalesData();
        } catch (Exception e) {
            log.error("PostgreSQL ingestion failed", e);
        }

        // Ingest from CSV files
        try {
            csvIngestor.ingestCsvFiles();
        } catch (Exception e) {
            log.error("CSV ingestion failed", e);
        }

        // Ingest from SOAP service
        try {
            soapIngestor.ingestFromSoapService();
        } catch (Exception e) {
            log.error("SOAP ingestion failed", e);
        }

        log.info("Completed full data ingestion");
    }
}

