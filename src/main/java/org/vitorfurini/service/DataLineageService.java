package org.vitorfurini.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vitorfurini.domain.DataLineage;
import org.vitorfurini.entity.DataLineageEntity;
import org.vitorfurini.repository.DataLineageRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataLineageService {

    private final DataLineageRepository lineageRepository;

    public void saveLineage(DataLineage lineage) {
        try {
            DataLineageEntity entity = DataLineageEntity.builder()
                    .lineageId(lineage.getLineageId())
                    .recordId(lineage.getRecordId())
                    .source(lineage.getSource())
                    .stage(lineage.getStage())
                    .transformation(lineage.getTransformation())
                    .timestamp(lineage.getTimestamp())
                    .status(lineage.getStatus())
                    .errorMessage(lineage.getErrorMessage())
                    .build();

            lineageRepository.save(entity);
            log.info("Saved lineage record: {}", lineage.getLineageId());
        } catch (Exception e) {
            log.error("Error saving lineage record", e);
        }
    }
}

