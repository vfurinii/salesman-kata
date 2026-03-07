package org.vitorfurini.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vitorfurini.entity.ProcessedSalesEntity;

import java.util.Optional;

@Repository
public interface ProcessedSalesRepository extends JpaRepository<ProcessedSalesEntity, Long> {
    Optional<ProcessedSalesEntity> findBySaleId(String saleId);
}

