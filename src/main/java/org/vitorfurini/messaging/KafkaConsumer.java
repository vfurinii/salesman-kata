package org.vitorfurini.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.vitorfurini.config.KafkaTopicConfig;
import org.vitorfurini.domain.DataLineage;
import org.vitorfurini.domain.SalesRecord;
import org.vitorfurini.service.DataLineageService;
import org.vitorfurini.service.SalesProcessingService;
import org.vitorfurini.service.StorageService;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final StorageService storageService;
    private final SalesProcessingService processingService;
    private final DataLineageService lineageService;

    @KafkaListener(topics = KafkaTopicConfig.RAW_SALES_TOPIC, groupId = "raw-sales-storage-group")
    public void consumeRawSales(String message) {
        try {
            log.info("Received raw sales message");
            SalesRecord salesRecord = objectMapper.readValue(message, SalesRecord.class);
            storageService.saveRawSales(salesRecord);

            // Trigger processing
            processingService.processSalesRecord(salesRecord);
        } catch (Exception e) {
            log.error("Error consuming raw sales message", e);
        }
    }

    @KafkaListener(topics = KafkaTopicConfig.PROCESSED_SALES_TOPIC, groupId = "processed-sales-storage-group")
    public void consumeProcessedSales(String message) {
        try {
            log.info("Received processed sales message");
            SalesRecord salesRecord = objectMapper.readValue(message, SalesRecord.class);
            storageService.saveProcessedSales(salesRecord);
        } catch (Exception e) {
            log.error("Error consuming processed sales message", e);
        }
    }

    @KafkaListener(topics = KafkaTopicConfig.LINEAGE_TOPIC, groupId = "lineage-storage-group")
    public void consumeLineage(String message) {
        try {
            log.info("Received lineage message");
            DataLineage lineage = objectMapper.readValue(message, DataLineage.class);
            lineageService.saveLineage(lineage);
        } catch (Exception e) {
            log.error("Error consuming lineage message", e);
        }
    }
}

