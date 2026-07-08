package com.sgi.fiis.convocatorias.infrastructure.persistence;

import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineJpaRepository;
import com.sgi.fiis.shared.infrastructure.persistence.DocumentEntity;
import com.sgi.fiis.shared.infrastructure.persistence.DocumentJpaRepository;
import com.sgi.fiis.shared.infrastructure.persistence.JsonbHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SaveCallAdapter implements SaveCallPort {

    private static final String STATUS_ABIERTA   = "ABIERTA";
    private static final String STATUS_CERRADA   = "CERRADA";
    private static final String STATUS_FINALIZADA = "FINALIZADA";

    private final ResearchCallJpaRepository jpaRepository;
    private final DocumentJpaRepository documentRepository;
    private final ResearchLineJpaRepository lineRepository;

    public SaveCallAdapter(ResearchCallJpaRepository jpaRepository,
                           DocumentJpaRepository documentRepository,
                           ResearchLineJpaRepository lineRepository) {
        this.jpaRepository = jpaRepository;
        this.documentRepository = documentRepository;
        this.lineRepository = lineRepository;
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
        String dbStatus = STATUS_ABIERTA;
        if (status == CallStatus.CLOSED) {
            dbStatus = STATUS_CERRADA;
        } else if (status == CallStatus.FINISHED) {
            dbStatus = STATUS_FINALIZADA;
        }
        return jpaRepository.findByStatus(dbStatus).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean areLinesActive(List<Integer> lineIds) {
        if (lineIds == null || lineIds.isEmpty()) {
            return false;
        }
        long activeCount = lineIds.stream()
                .map(lineRepository::findById)
                .filter(opt -> opt.isPresent() && opt.get().isActive())
                .count();
        return activeCount == lineIds.size();
    }

    @Override
    public List<ResearchCall> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private ResearchCallEntity toEntity(ResearchCall domain) {
        String dbStatus = STATUS_ABIERTA;
        if (domain.getStatus() == CallStatus.CLOSED) {
            dbStatus = STATUS_CERRADA;
        } else if (domain.getStatus() == CallStatus.FINISHED) {
            dbStatus = STATUS_FINALIZADA;
        }

        DocumentEntity doc = null;
        if (domain.getDocumentId() != null) {
            doc = documentRepository.findById(domain.getDocumentId()).orElse(null);
        }

        List<ResearchLineEntity> lines = null;
        if (domain.getResearchLineIds() != null) {
            lines = domain.getResearchLineIds().stream()
                    .map(id -> lineRepository.findById(id).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList();
        }

        return ResearchCallEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .titleJson(JsonbHelper.toJson(Map.of("es", domain.getTitle() != null ? domain.getTitle() : "")))
                .descriptionJson(JsonbHelper.toJson(Map.of("es", domain.getDescription() != null ? domain.getDescription() : "")))
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status(dbStatus)
                .document(doc)
                .researchLines(lines)
                .build();
    }

    private ResearchCall toDomain(ResearchCallEntity entity) {
        CallStatus domainStatus = CallStatus.OPEN;
        if (STATUS_CERRADA.equalsIgnoreCase(entity.getStatus())) {
            domainStatus = CallStatus.CLOSED;
        } else if (STATUS_FINALIZADA.equalsIgnoreCase(entity.getStatus())) {
            domainStatus = CallStatus.FINISHED;
        }

        List<Integer> lineIds = null;
        if (entity.getResearchLines() != null) {
            lineIds = entity.getResearchLines().stream()
                    .map(ResearchLineEntity::getId)
                    .toList();
        }

        return new ResearchCall(
                entity.getId(),
                JsonbHelper.getText(entity.getTitleJson(), "es"),
                JsonbHelper.getText(entity.getDescriptionJson(), "es"),
                entity.getStartDate(),
                entity.getEndDate(),
                domainStatus,
                entity.getDocument() != null ? entity.getDocument().getId() : null,
                lineIds
        );
    }
}
