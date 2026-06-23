package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.tramites.application.dto.TramiteRequestDto;
import com.sgi.fiis.tramites.application.dto.TramiteResponseDto;
import com.sgi.fiis.tramites.application.mapper.TramiteMapper;
import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.tramites.domain.port.TramiteRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CrearTramiteUseCase {

    private final TramiteRepositoryPort tramiteRepositoryPort;

    public CrearTramiteUseCase(TramiteRepositoryPort tramiteRepositoryPort) {
        this.tramiteRepositoryPort = tramiteRepositoryPort;
    }

    @Transactional
    public TramiteResponseDto execute(TramiteRequestDto dto) {
        Tramite tramite = Tramite.builder()
                .codigoTramite(generarCodigo())
                .tipoTramite(dto.getTipoTramite())
                .idSolicitante(dto.getIdSolicitante())
                .idGrupo(dto.getIdGrupo())
                .estadoActual(EstadoTramite.REGISTRADO)
                .rolRevisorActual(null)
                .idReferenciaProyecto(dto.getIdReferenciaProyecto())
                .idReferenciaTesis(dto.getIdReferenciaTesis())
                .idReferenciaInforme(dto.getIdReferenciaInforme())
                .fechaEnvio(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        tramite.validarArcoExcluyente();

        // REGISTRADO → PENDIENTE_COORDINADOR: presentación automática al crear
        tramite.transicionarA(
                EstadoTramite.PENDIENTE_COORDINADOR,
                null,
                dto.getIdSolicitante(),
                "PRESENTADO_POR_SOLICITANTE",
                null,
                RoleEnum.COORDINADOR_GRUPO
        );

        return TramiteMapper.toResponse(tramiteRepositoryPort.guardar(tramite));
    }

    private String generarCodigo() {
        int anio = LocalDateTime.now().getYear();
        long secuencia = System.nanoTime() % 1_000_000L;
        return String.format("TRM-%d-%06d", anio, secuencia);
    }
}
