package org.vitorfurini.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vitorfurini.domain.DataLineage;
import org.vitorfurini.domain.SalesRecord;
import org.vitorfurini.messaging.KafkaProducer;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesProcessingService {

    private final KafkaProducer kafkaProducer;

    public void processSalesRecord(SalesRecord rawRecord) {
        log.info("Processing sales record: {}", rawRecord.getSaleId());

        try {
            // Data cleaning and validation
            SalesRecord cleanedRecord = cleanData(rawRecord);

            // Track cleaning lineage
            DataLineage cleaningLineage = DataLineage.builder()
                    .lineageId(UUID.randomUUID().toString())
                    .recordId(rawRecord.getSaleId())
                    .source(rawRecord.getSource())
                    .stage("CLEANED")
                    .transformation("Data cleaned: normalized strings, validated amounts")
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .build();
            kafkaProducer.sendLineage(cleaningLineage);

            // Send to processed topic
            kafkaProducer.sendProcessedSales(cleanedRecord);

            // Track processing lineage
            DataLineage processingLineage = DataLineage.builder()
                    .lineageId(UUID.randomUUID().toString())
                    .recordId(rawRecord.getSaleId())
                    .source(rawRecord.getSource())
                    .stage("PROCESSED")
                    .transformation("Record processed and ready for analytics")
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .build();
            kafkaProducer.sendLineage(processingLineage);

        } catch (Exception e) {
            log.error("Error processing sales record", e);

            // Track error in lineage
            DataLineage errorLineage = DataLineage.builder()
                    .lineageId(UUID.randomUUID().toString())
                    .recordId(rawRecord.getSaleId())
                    .source(rawRecord.getSource())
                    .stage("PROCESSING_FAILED")
                    .transformation("Processing failed")
                    .timestamp(LocalDateTime.now())
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .build();
            kafkaProducer.sendLineage(errorLineage);
        }
    }

    private SalesRecord cleanData(SalesRecord rawRecord) {
        return SalesRecord.builder()
                .saleId(rawRecord.getSaleId())
                .productType(normalizeString(rawRecord.getProductType()))
                .productName(normalizeString(rawRecord.getProductName()))
                .amount(rawRecord.getAmount())
                .salespersonId(normalizeString(rawRecord.getSalespersonId()))
                .salespersonName(normalizeString(rawRecord.getSalespersonName()))
                .country(normalizeString(rawRecord.getCountry()))
                .city(normalizeString(rawRecord.getCity()))
                .warehouse(normalizeString(rawRecord.getWarehouse()))
                .retailer(normalizeString(rawRecord.getRetailer()))
                .saleDate(rawRecord.getSaleDate())
                .source(rawRecord.getSource())
                .ingestedAt(rawRecord.getIngestedAt())
                .build();
    }

    private String normalizeString(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase();
    }
}

