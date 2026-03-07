package org.vitorfurini.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_lineage")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataLineageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lineage_id")
    private String lineageId;

    @Column(name = "record_id")
    private String recordId;

    @Column(name = "source")
    private String source;

    @Column(name = "stage")
    private String stage;

    @Column(name = "transformation", length = 1000)
    private String transformation;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;
}

