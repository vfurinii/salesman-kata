package org.vitorfurini.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_sales")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedSalesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_id", unique = true)
    private String saleId;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "salesperson_id")
    private String salespersonId;

    @Column(name = "salesperson_name")
    private String salespersonName;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "warehouse")
    private String warehouse;

    @Column(name = "retailer")
    private String retailer;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}

