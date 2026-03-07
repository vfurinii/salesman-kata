package org.vitorfurini.ingestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.vitorfurini.domain.DataLineage;
import org.vitorfurini.domain.SalesRecord;
import org.vitorfurini.messaging.KafkaProducer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresIngestor {

    private final JdbcTemplate jdbcTemplate;
    private final KafkaProducer kafkaProducer;

    public void ingestSalesData() {
        log.info("Starting PostgreSQL data ingestion");

        try {
            // Query sales data from PostgreSQL
            String sql = """
                SELECT 
                    sale_id,
                    product_type,
                    product_name,
                    amount,
                    salesperson_id,
                    salesperson_name,
                    country,
                    city,
                    warehouse,
                    retailer,
                    sale_date
                FROM source_sales
                WHERE sale_date >= CURRENT_DATE - INTERVAL '30 days'
            """;

            List<SalesRecord> records = jdbcTemplate.query(sql, (rs, rowNum) ->
                SalesRecord.builder()
                    .saleId(rs.getString("sale_id"))
                    .productType(rs.getString("product_type"))
                    .productName(rs.getString("product_name"))
                    .amount(rs.getBigDecimal("amount"))
                    .salespersonId(rs.getString("salesperson_id"))
                    .salespersonName(rs.getString("salesperson_name"))
                    .country(rs.getString("country"))
                    .city(rs.getString("city"))
                    .warehouse(rs.getString("warehouse"))
                    .retailer(rs.getString("retailer"))
                    .saleDate(rs.getTimestamp("sale_date").toLocalDateTime())
                    .source("POSTGRES")
                    .ingestedAt(LocalDateTime.now())
                    .build()
            );

            log.info("Ingested {} records from PostgreSQL", records.size());

            // Send each record to Kafka
            records.forEach(record -> {
                kafkaProducer.sendRawSales(record);

                // Track lineage
                DataLineage lineage = DataLineage.builder()
                    .lineageId(UUID.randomUUID().toString())
                    .recordId(record.getSaleId())
                    .source("POSTGRES")
                    .stage("RAW")
                    .transformation("Ingested from PostgreSQL source_sales table")
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .build();
                kafkaProducer.sendLineage(lineage);
            });

        } catch (Exception e) {
            log.error("Error ingesting data from PostgreSQL", e);
        }
    }
}

