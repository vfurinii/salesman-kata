package org.vitorfurini.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSalespersonDTO {
    private String country;
    private String salespersonId;
    private String salespersonName;
    private BigDecimal totalRevenue;
    private Long salesCount;
}

