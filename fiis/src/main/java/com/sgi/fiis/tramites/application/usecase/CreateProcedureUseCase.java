package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.tramites.application.dto.ProcedureRequestDto;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class CreateProcedureUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;

    public CreateProcedureUseCase(ProcedureRepositoryPort procedureRepositoryPort) {
        this.procedureRepositoryPort = procedureRepositoryPort;
    }

    @Transactional
    public ProcedureResponseDto execute(ProcedureRequestDto dto) {
        Procedure tramite = Procedure.builder()
                .code(generarCodigo())
                .procedureType(dto.getProcedureType())
                .applicantId(dto.getApplicantId())
                .groupId(dto.getGroupId())
                .currentStatus(ProcedureStatus.REGISTRADO)
                .currentReviewerRole(null)
                .projectReferenceId(dto.getProjectReferenceId())
                .thesisReferenceId(dto.getThesisReferenceId())
                .reportReferenceId(dto.getReportReferenceId())
                .sentAt(LocalDateTime.now(ZoneId.systemDefault()))
                .updatedAt(LocalDateTime.now(ZoneId.systemDefault()))
                .build();

        tramite.validateExclusiveReference();

        // REGISTRADO → PENDIENTE_COORDINADOR: presentación automática al crear
        tramite.transitionTo(
                ProcedureStatus.PENDIENTE_COORDINADOR,
                null,
                dto.getApplicantId(),
                "PRESENTADO_POR_SOLICITANTE",
                null,
                RoleEnum.COORDINADOR_GRUPO
        );

        return ProcedureMapper.toResponse(procedureRepositoryPort.save(tramite));
    }

    private String generarCodigo() {
        int anio = LocalDateTime.now(ZoneId.systemDefault()).getYear();
        long secuencia = System.nanoTime() % 1_000_000L;
        return String.format("TRM-%d-%06d", anio, secuencia);
    }
}
