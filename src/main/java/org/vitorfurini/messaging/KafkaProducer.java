package org.vitorfurini.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.vitorfurini.config.KafkaTopicConfig;
import org.vitorfurini.domain.DataLineage;
import org.vitorfurini.domain.SalesRecord;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendRawSales(SalesRecord salesRecord) {
        try {
            String message = objectMapper.writeValueAsString(salesRecord);
            kafkaTemplate.send(KafkaTopicConfig.RAW_SALES_TOPIC, salesRecord.getSaleId(), message);
            log.info("Sent raw sales record to Kafka: {}", salesRecord.getSaleId());
        } catch (Exception e) {
            log.error("Error sending raw sales record to Kafka", e);
        }
    }

    public void sendProcessedSales(SalesRecord salesRecord) {
        try {
            String message = objectMapper.writeValueAsString(salesRecord);
            kafkaTemplate.send(KafkaTopicConfig.PROCESSED_SALES_TOPIC, salesRecord.getSaleId(), message);
            log.info("Sent processed sales record to Kafka: {}", salesRecord.getSaleId());
        } catch (Exception e) {
            log.error("Error sending processed sales record to Kafka", e);
        }
    }

    public void sendLineage(DataLineage lineage) {
        try {
            String message = objectMapper.writeValueAsString(lineage);
            kafkaTemplate.send(KafkaTopicConfig.LINEAGE_TOPIC, lineage.getLineageId(), message);
            log.info("Sent lineage record to Kafka: {}", lineage.getLineageId());
        } catch (Exception e) {
            log.error("Error sending lineage record to Kafka", e);
        }
    }
}

