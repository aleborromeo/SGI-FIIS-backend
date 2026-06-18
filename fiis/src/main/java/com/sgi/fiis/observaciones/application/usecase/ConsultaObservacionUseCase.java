package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.ObservacionResponseDTO;
import com.sgi.fiis.observaciones.domain.exception.ObservacionNotFoundException;
import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.port.ObservacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Consultar detalle de una observación (RF-61).
 */
@Service
@RequiredArgsConstructor
public class ConsultaObservacionUseCase {

    private final ObservacionRepository observacionRepository;

    @Transactional(readOnly = true)
    public ObservacionResponseDTO execute(Integer idObservacion) {
        Observacion o = observacionRepository.findById(idObservacion)
                .orElseThrow(() -> new ObservacionNotFoundException(idObservacion));

        return ObservacionResponseDTO.builder()
                .id(o.getId()).idTramite(o.getIdTramite()).idRevisor(o.getIdRevisor())
                .tipoObservacion(o.getTipoObservacion().name())
                .descripcion(o.getDescripcion()).estado(o.getEstado().name())
                .rolRevisor(o.getRolRevisor()).fechaRegistro(o.getFechaRegistro())
                .fechaActualizacion(o.getFechaActualizacion()).build();
    }
}
