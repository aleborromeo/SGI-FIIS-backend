package com.sgi.fiis.thesis.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.sgi.fiis.thesis.domain.ThesisReport;
import com.sgi.fiis.thesis.domain.port.out.ThesisReportRepositoryPort;
import com.sgi.fiis.thesis.infrastructure.persistence.mapper.ThesisReportMapper;
import com.sgi.fiis.thesis.infrastructure.persistence.repository.ThesisReportJpaRepository;

@Repository
public class ThesisReportRepositoryAdapter implements ThesisReportRepositoryPort {
    private final ThesisReportJpaRepository repository;
    private final ThesisReportMapper mapper;

    public ThesisReportRepositoryAdapter(ThesisReportJpaRepository repository, ThesisReportMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    public ThesisReport save(ThesisReport thesisReport) { return mapper.toDomain(repository.save(mapper.toEntity(thesisReport))); }
    public Optional<ThesisReport> findById(Integer idInformeTesis) { return repository.findById(idInformeTesis).map(mapper::toDomain); }
    public List<ThesisReport> findByPlanTesis(Integer idPlanTesis) { return repository.findByIdPlanTesis(idPlanTesis).stream().map(mapper::toDomain).toList(); }
}
