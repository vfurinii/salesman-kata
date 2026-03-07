package org.vitorfurini.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesRecord {
    private String saleId;
    private String productType; // CELL_PHONE, COMPUTER, ACCESSORY
    private String productName;
    private BigDecimal amount;
    private String salespersonId;
    private String salespersonName;
    private String country;
    private String city;
    private String warehouse;
    private String retailer;
    private LocalDateTime saleDate;
    private String source; // POSTGRES, CSV, SOAP
    private LocalDateTime ingestedAt;
}

