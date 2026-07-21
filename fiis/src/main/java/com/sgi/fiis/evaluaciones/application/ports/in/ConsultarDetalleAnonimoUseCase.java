package com.sgi.fiis.evaluaciones.application.ports.in;

import com.sgi.fiis.evaluaciones.presentation.dto.AnonymousProjectDetailResponse;

public interface ConsultarDetalleAnonimoUseCase {
    AnonymousProjectDetailResponse consultarDetalleAnonimo(Long idEvaluacion);
}
