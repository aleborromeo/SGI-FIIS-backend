package com.sgi.fiis.tramites.application.dto;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureResponseDto {

    private Long id;
    private String codigoTramite;
    private ProcedureType tipoTramite;
    private ProcedureStatus estadoActual;
    private Long idSolicitante;
    private Long idGrupo;
    private RoleEnum rolRevisorActual;
    private String observacionActual;
    private Long idReferenciaProyecto;
    private Long idReferenciaTesis;
    private Long idReferenciaInforme;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaActualizacion;
}
