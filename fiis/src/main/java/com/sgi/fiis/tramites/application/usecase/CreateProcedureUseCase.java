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

    private final ProcedureRepositoryPort tramiteRepositoryPort;

    public CreateProcedureUseCase(ProcedureRepositoryPort tramiteRepositoryPort) {
        this.tramiteRepositoryPort = tramiteRepositoryPort;
    }

    @Transactional
    public ProcedureResponseDto execute(ProcedureRequestDto dto) {
        Procedure tramite = Procedure.builder()
                .codigoTramite(generarCodigo())
                .tipoTramite(dto.getTipoTramite())
                .idSolicitante(dto.getIdSolicitante())
                .idGrupo(dto.getIdGrupo())
                .estadoActual(ProcedureStatus.REGISTRADO)
                .rolRevisorActual(null)
                .idReferenciaProyecto(dto.getIdReferenciaProyecto())
                .idReferenciaTesis(dto.getIdReferenciaTesis())
                .idReferenciaInforme(dto.getIdReferenciaInforme())
                .fechaEnvio(LocalDateTime.now(ZoneId.systemDefault()))
                .fechaActualizacion(LocalDateTime.now(ZoneId.systemDefault()))
                .build();

        tramite.validateExclusiveReference();

        // REGISTRADO → PENDIENTE_COORDINADOR: presentación automática al crear
        tramite.transitionTo(
                ProcedureStatus.PENDIENTE_COORDINADOR,
                null,
                dto.getIdSolicitante(),
                "PRESENTADO_POR_SOLICITANTE",
                null,
                RoleEnum.COORDINADOR_GRUPO
        );

        return ProcedureMapper.toResponse(tramiteRepositoryPort.save(tramite));
    }

    private String generarCodigo() {
        int anio = LocalDateTime.now(ZoneId.systemDefault()).getYear();
        long secuencia = System.nanoTime() % 1_000_000L;
        return String.format("TRM-%d-%06d", anio, secuencia);
    }
}
