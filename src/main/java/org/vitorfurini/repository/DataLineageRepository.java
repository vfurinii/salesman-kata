package org.vitorfurini.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vitorfurini.entity.DataLineageEntity;

@Repository
public interface DataLineageRepository extends JpaRepository<DataLineageEntity, Long> {
}

