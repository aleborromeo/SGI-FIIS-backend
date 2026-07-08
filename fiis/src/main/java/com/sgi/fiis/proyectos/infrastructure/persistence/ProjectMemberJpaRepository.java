package com.sgi.fiis.proyectos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMemberEntity, Integer> {

    List<ProjectMemberEntity> findByProjectId(Integer projectId);

    void deleteByProjectId(Integer projectId);

}
