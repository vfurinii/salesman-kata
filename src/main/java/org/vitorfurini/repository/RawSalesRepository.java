package org.vitorfurini.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vitorfurini.entity.RawSalesEntity;

import java.util.Optional;

@Repository
public interface RawSalesRepository extends JpaRepository<RawSalesEntity, Long> {
    Optional<RawSalesEntity> findBySaleId(String saleId);
}

