package org.vitorfurini.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String RAW_SALES_TOPIC = "raw-sales";
    public static final String PROCESSED_SALES_TOPIC = "processed-sales";
    public static final String LINEAGE_TOPIC = "data-lineage";

    @Bean
    public NewTopic rawSalesTopic() {
        return TopicBuilder.name(RAW_SALES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic processedSalesTopic() {
        return TopicBuilder.name(PROCESSED_SALES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic lineageTopic() {
        return TopicBuilder.name(LINEAGE_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

