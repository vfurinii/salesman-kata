package org.vitorfurini.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataLineage {
    private String lineageId;
    private String recordId;
    private String source;
    private String stage; // RAW, CLEANED, AGGREGATED
    private String transformation;
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED
    private String errorMessage;
}

