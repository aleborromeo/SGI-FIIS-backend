package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.ObservacionRequestDTO;
import com.sgi.fiis.observaciones.application.dto.ObservacionResponseDTO;
import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.model.TipoObservacion;
import com.sgi.fiis.observaciones.domain.port.ObservacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para registrar una nueva observación sobre un trámite.
 */
@Service
@RequiredArgsConstructor
public class RegistraObservacionUseCase {

    private final ObservacionRepository observacionRepository;

    @Transactional
    public ObservacionResponseDTO execute(ObservacionRequestDTO dto) {
        TipoObservacion tipo = TipoObservacion.valueOf(dto.getTipoObservacion());

        Observacion observacion = Observacion.crear(
                dto.getIdTramite(),
                dto.getIdRevisor(),
                tipo,
                dto.getDescripcion(),
                dto.getRolRevisor()
        );

        Observacion guardada = observacionRepository.save(observacion);
        return toResponseDTO(guardada);
    }

    private ObservacionResponseDTO toResponseDTO(Observacion observacion) {
        return ObservacionResponseDTO.builder()
                .id(observacion.getId())
                .idTramite(observacion.getIdTramite())
                .idRevisor(observacion.getIdRevisor())
                .tipoObservacion(observacion.getTipoObservacion().name())
                .descripcion(observacion.getDescripcion())
                .estado(observacion.getEstado().name())
                .rolRevisor(observacion.getRolRevisor())
                .fechaRegistro(observacion.getFechaRegistro())
                .fechaActualizacion(observacion.getFechaActualizacion())
                .build();
    }
}
