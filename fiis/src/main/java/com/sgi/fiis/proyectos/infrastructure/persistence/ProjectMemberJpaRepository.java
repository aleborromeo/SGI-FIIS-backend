package com.sgi.fiis.proyectos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMemberEntity, Integer> {

    List<ProjectMemberEntity> findByProjectId(Integer projectId);

    void deleteByProjectId(Integer projectId);

}
