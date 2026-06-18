package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ResearchLineRepositoryAdapter implements ResearchLineRepositoryPort {

    private final SpringDataResearchLineRepository springDataRepository;

    public ResearchLineRepositoryAdapter(SpringDataResearchLineRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public ResearchLine save(ResearchLine line) {
        return toDomain(springDataRepository.save(toEntity(line)));
    }

    @Override
    public Optional<ResearchLine> findById(Integer id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ResearchLine> findAll() {
        return springDataRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<ResearchLine> findAllActive() {
        return springDataRepository.findByActiveTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public List<ResearchLine> findActiveByGroup(Integer groupId) {
        return springDataRepository.findActiveByGroupId(groupId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByName(String lineName) {
        return springDataRepository.existsByLineName(lineName);
    }

    private ResearchLine toDomain(ResearchLineEntity entity) {
        if (entity == null) {
            return null;
        }
        return ResearchLine.builder()
                .id(entity.getId())
                .lineName(entity.getLineName())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ResearchLineEntity toEntity(ResearchLine domain) {
        if (domain == null) {
            return null;
        }
        ResearchLineEntity entity = new ResearchLineEntity();
        entity.setId(domain.getId());
        entity.setLineName(domain.getLineName());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
