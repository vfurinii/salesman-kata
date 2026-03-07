package org.vitorfurini.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vitorfurini.domain.SalesRecord;
import org.vitorfurini.entity.ProcessedSalesEntity;
import org.vitorfurini.entity.RawSalesEntity;
import org.vitorfurini.repository.ProcessedSalesRepository;
import org.vitorfurini.repository.RawSalesRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final RawSalesRepository rawSalesRepository;
    private final ProcessedSalesRepository processedSalesRepository;

    public void saveRawSales(SalesRecord salesRecord) {
        try {
            RawSalesEntity entity = RawSalesEntity.builder()
                    .saleId(salesRecord.getSaleId())
                    .productType(salesRecord.getProductType())
                    .productName(salesRecord.getProductName())
                    .amount(salesRecord.getAmount())
                    .salespersonId(salesRecord.getSalespersonId())
                    .salespersonName(salesRecord.getSalespersonName())
                    .country(salesRecord.getCountry())
                    .city(salesRecord.getCity())
                    .warehouse(salesRecord.getWarehouse())
                    .retailer(salesRecord.getRetailer())
                    .saleDate(salesRecord.getSaleDate())
                    .source(salesRecord.getSource())
                    .ingestedAt(salesRecord.getIngestedAt())
                    .build();

            rawSalesRepository.save(entity);
            log.info("Saved raw sales record: {}", salesRecord.getSaleId());
        } catch (Exception e) {
            log.error("Error saving raw sales record", e);
        }
    }

    public void saveProcessedSales(SalesRecord salesRecord) {
        try {
            ProcessedSalesEntity entity = ProcessedSalesEntity.builder()
                    .saleId(salesRecord.getSaleId())
                    .productType(salesRecord.getProductType())
                    .productName(salesRecord.getProductName())
                    .amount(salesRecord.getAmount())
                    .salespersonId(salesRecord.getSalespersonId())
                    .salespersonName(salesRecord.getSalespersonName())
                    .country(salesRecord.getCountry())
                    .city(salesRecord.getCity())
                    .warehouse(salesRecord.getWarehouse())
                    .retailer(salesRecord.getRetailer())
                    .saleDate(salesRecord.getSaleDate())
                    .processedAt(LocalDateTime.now())
                    .build();

            processedSalesRepository.save(entity);
            log.info("Saved processed sales record: {}", salesRecord.getSaleId());
        } catch (Exception e) {
            log.error("Error saving processed sales record", e);
        }
    }
}

