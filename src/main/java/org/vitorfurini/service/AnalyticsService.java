package org.vitorfurini.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.vitorfurini.dto.CityRevenueDTO;
import org.vitorfurini.dto.TopSalespersonDTO;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Question 1: Which cities are generating the most revenue this month?
     */
    public List<CityRevenueDTO> getTopCitiesByRevenue() {
        log.info("Fetching top cities by revenue for current month");

        String sql = """
            SELECT 
                country,
                city,
                SUM(amount) as total_revenue,
                COUNT(*) as sales_count
            FROM processed_sales
            WHERE EXTRACT(YEAR FROM sale_date) = EXTRACT(YEAR FROM CURRENT_DATE)
              AND EXTRACT(MONTH FROM sale_date) = EXTRACT(MONTH FROM CURRENT_DATE)
            GROUP BY country, city
            ORDER BY total_revenue DESC
            LIMIT 20
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                CityRevenueDTO.builder()
                        .country(rs.getString("country"))
                        .city(rs.getString("city"))
                        .totalRevenue(rs.getBigDecimal("total_revenue"))
                        .salesCount(rs.getLong("sales_count"))
                        .build()
        );
    }

    /**
     * Question 2: Who are our top-performing salespeople in each country?
     */
    public List<TopSalespersonDTO> getTopSalespeopleByCountry() {
        log.info("Fetching top salespeople by country for current month");

        String sql = """
            WITH ranked_salespeople AS (
                SELECT 
                    country,
                    salesperson_id,
                    salesperson_name,
                    SUM(amount) as total_revenue,
                    COUNT(*) as sales_count,
                    ROW_NUMBER() OVER (PARTITION BY country ORDER BY SUM(amount) DESC) as rank
                FROM processed_sales
                WHERE EXTRACT(YEAR FROM sale_date) = EXTRACT(YEAR FROM CURRENT_DATE)
                  AND EXTRACT(MONTH FROM sale_date) = EXTRACT(MONTH FROM CURRENT_DATE)
                GROUP BY country, salesperson_id, salesperson_name
            )
            SELECT 
                country,
                salesperson_id,
                salesperson_name,
                total_revenue,
                sales_count
            FROM ranked_salespeople
            WHERE rank <= 5
            ORDER BY country, total_revenue DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                TopSalespersonDTO.builder()
                        .country(rs.getString("country"))
                        .salespersonId(rs.getString("salesperson_id"))
                        .salespersonName(rs.getString("salesperson_name"))
                        .totalRevenue(rs.getBigDecimal("total_revenue"))
                        .salesCount(rs.getLong("sales_count"))
                        .build()
        );
    }

    /**
     * Get revenue by country
     */
    public List<CityRevenueDTO> getRevenueByCountry() {
        log.info("Fetching revenue by country for current month");

        String sql = """
            SELECT 
                country,
                'ALL' as city,
                SUM(amount) as total_revenue,
                COUNT(*) as sales_count
            FROM processed_sales
            WHERE EXTRACT(YEAR FROM sale_date) = EXTRACT(YEAR FROM CURRENT_DATE)
              AND EXTRACT(MONTH FROM sale_date) = EXTRACT(MONTH FROM CURRENT_DATE)
            GROUP BY country
            ORDER BY total_revenue DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                CityRevenueDTO.builder()
                        .country(rs.getString("country"))
                        .city(rs.getString("city"))
                        .totalRevenue(rs.getBigDecimal("total_revenue"))
                        .salesCount(rs.getLong("sales_count"))
                        .build()
        );
    }
}

