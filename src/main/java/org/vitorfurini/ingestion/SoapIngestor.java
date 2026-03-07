package org.vitorfurini.ingestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.vitorfurini.domain.DataLineage;
import org.vitorfurini.domain.SalesRecord;
import org.vitorfurini.messaging.KafkaProducer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoapIngestor {

    private final KafkaProducer kafkaProducer;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${soap.service.url:http://localhost:8081/soap/sales}")
    private String soapServiceUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public void ingestFromSoapService() {
        log.info("Starting SOAP service data ingestion");

        try {
            // Build SOAP request
            String soapRequest = buildSoapRequest();

            // Call SOAP service
            String soapResponse = restTemplate.postForObject(
                    soapServiceUrl,
                    soapRequest,
                    String.class
            );

            if (soapResponse != null) {
                parseSoapResponse(soapResponse);
            }

        } catch (Exception e) {
            log.error("Error ingesting data from SOAP service", e);
        }
    }

    private String buildSoapRequest() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:sal="http://vitorfurini.org/sales">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <sal:GetSalesDataRequest>
                         <sal:fromDate>%s</sal:fromDate>
                         <sal:toDate>%s</sal:toDate>
                      </sal:GetSalesDataRequest>
                   </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(
                LocalDateTime.now().minusDays(30).format(DATE_FORMATTER),
                LocalDateTime.now().format(DATE_FORMATTER)
        );
    }

    private void parseSoapResponse(String soapResponse) {
        // Simplified parsing - in production, use JAXB or similar
        log.info("Parsing SOAP response");

        // For demonstration, create sample records
        // In production, properly parse XML response
        createSampleSoapRecord();
    }

    private void createSampleSoapRecord() {
        SalesRecord salesRecord = SalesRecord.builder()
                .saleId("SOAP-" + UUID.randomUUID().toString())
                .productType("CELL_PHONE")
                .productName("Sample from SOAP")
                .amount(new BigDecimal("999.99"))
                .salespersonId("SP-SOAP-001")
                .salespersonName("SOAP Salesperson")
                .country("USA")
                .city("New York")
                .warehouse("WH-SOAP-01")
                .retailer("SOAP Retailer")
                .saleDate(LocalDateTime.now())
                .source("SOAP")
                .ingestedAt(LocalDateTime.now())
                .build();

        kafkaProducer.sendRawSales(salesRecord);

        // Track lineage
        DataLineage lineage = DataLineage.builder()
                .lineageId(UUID.randomUUID().toString())
                .recordId(salesRecord.getSaleId())
                .source("SOAP")
                .stage("RAW")
                .transformation("Ingested from SOAP web service")
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .build();
        kafkaProducer.sendLineage(lineage);

        log.info("Created sample SOAP record");
    }
}

