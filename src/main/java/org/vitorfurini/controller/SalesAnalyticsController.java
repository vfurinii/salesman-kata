package org.vitorfurini.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vitorfurini.dto.CityRevenueDTO;
import org.vitorfurini.dto.TopSalespersonDTO;
import org.vitorfurini.service.AnalyticsService;
import org.vitorfurini.service.DataIngestionOrchestrator;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SalesAnalyticsController {

    private final AnalyticsService analyticsService;
    private final DataIngestionOrchestrator ingestionOrchestrator;

    @GetMapping("/report/cities")
    public ResponseEntity<List<CityRevenueDTO>> getTopCities() {
        return ResponseEntity.ok(analyticsService.getTopCitiesByRevenue());
    }

    @GetMapping("/report/salespeople")
    public ResponseEntity<List<TopSalespersonDTO>> getTopSalespeople() {
        return ResponseEntity.ok(analyticsService.getTopSalespeopleByCountry());
    }

    @GetMapping("/report/countries")
    public ResponseEntity<List<CityRevenueDTO>> getCountryRevenue() {
        return ResponseEntity.ok(analyticsService.getRevenueByCountry());
    }

    @GetMapping("/report/full")
    public ResponseEntity<Map<String, Object>> getFullReport() {
        return ResponseEntity.ok(Map.of(
                "topCities", analyticsService.getTopCitiesByRevenue(),
                "topSalespeople", analyticsService.getTopSalespeopleByCountry(),
                "countryRevenue", analyticsService.getRevenueByCountry()
        ));
    }

    @PostMapping("/ingestion/trigger")
    public ResponseEntity<String> triggerIngestion() {
        ingestionOrchestrator.runFullIngestion();
        return ResponseEntity.ok("Data ingestion triggered successfully");
    }
}

