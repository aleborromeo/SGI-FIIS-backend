package com.sgi.fiis.convocatorias.infrastructure.persistence;

import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SaveCallAdapter implements SaveCallPort {

    private final ResearchCallJpaRepository jpaRepository;

    public SaveCallAdapter(ResearchCallJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ResearchCall save(ResearchCall researchCall) {
        ResearchCallEntity entity = toEntity(researchCall);
        ResearchCallEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<ResearchCall> findById(Integer id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ResearchCall> findByStatus(CallStatus status) {
        String dbStatus = "ABIERTA";
        if (status == CallStatus.CLOSED) {
            dbStatus = "CERRADA";
        } else if (status == CallStatus.FINISHED) {
            dbStatus = "FINALIZADA";
        }
        return jpaRepository.findByStatus(dbStatus).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResearchCall> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ResearchCallEntity toEntity(ResearchCall domain) {
        String dbStatus = "ABIERTA";
        if (domain.getStatus() == CallStatus.CLOSED) {
            dbStatus = "CERRADA";
        } else if (domain.getStatus() == CallStatus.FINISHED) {
            dbStatus = "FINALIZADA";
        }

        return ResearchCallEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status(dbStatus)
                .build();
    }

    private ResearchCall toDomain(ResearchCallEntity entity) {
        CallStatus domainStatus = CallStatus.OPEN;
        if ("CERRADA".equalsIgnoreCase(entity.getStatus())) {
            domainStatus = CallStatus.CLOSED;
        } else if ("FINALIZADA".equalsIgnoreCase(entity.getStatus())) {
            domainStatus = CallStatus.FINISHED;
        }

        return new ResearchCall(
                entity.getId(),
                entity.getTitle(),
                entity.getStartDate(),
                entity.getEndDate(),
                domainStatus
        );
    }
}
