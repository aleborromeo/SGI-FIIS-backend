package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter;

import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportRepositoryPort;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.entity.ProgressReportEntity;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.repository.ProgressReportJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain repository port using Spring Data JPA.
 * Bridges the domain layer with the persistence layer.
 */
@Repository
public class ProgressReportRepositoryAdapter implements ProgressReportRepositoryPort {

    private final ProgressReportJpaRepository jpaRepository;

    public ProgressReportRepositoryAdapter(ProgressReportJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ProgressReport save(ProgressReport report) {
        ProgressReportEntity entity = ProgressReportMapper.toEntity(report);
        ProgressReportEntity saved = jpaRepository.save(entity);
        return ProgressReportMapper.toDomain(saved);
    }

    @Override
    public Optional<ProgressReport> findById(Long id) {
        return jpaRepository.findById(id)
                .map(ProgressReportMapper::toDomain);
    }

    @Override
    public List<ProgressReport> findByProjectId(Long projectId) {
        return jpaRepository.findByProjectIdOrderByRegistrationDateDesc(projectId)
                .stream()
                .map(ProgressReportMapper::toDomain)
                .toList();
    }

    @Override
    public List<ProgressReport> findAll() {
        return jpaRepository.findAllByOrderByRegistrationDateDesc()
                .stream()
                .map(ProgressReportMapper::toDomain)
                .toList();
    }

    @Override
    public List<ProgressReport> findByStatus(String status) {
        String dbStatus = ProgressReportMapper.mapStatusToEntity(ProgressReportStatus.valueOf(status));
        return jpaRepository.findByReportStatusOrderByRegistrationDateDesc(dbStatus)
                .stream()
                .map(ProgressReportMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
