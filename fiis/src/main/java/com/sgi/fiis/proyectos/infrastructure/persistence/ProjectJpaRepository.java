package com.sgi.fiis.proyectos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Integer> {
    List<ProjectEntity> findByResponsibleId(Long responsibleId);
    Optional<ProjectEntity> findByDocumentId(Integer documentId);
    List<ProjectEntity> findByGroupId(Integer groupId);
}
