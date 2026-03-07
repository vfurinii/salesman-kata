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
public class CityRevenueDTO {
    private String country;
    private String city;
    private BigDecimal totalRevenue;
    private Long salesCount;
}

