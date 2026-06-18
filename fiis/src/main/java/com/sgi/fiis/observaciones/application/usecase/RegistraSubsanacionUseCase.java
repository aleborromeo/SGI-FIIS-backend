package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.SubsanacionRequestDTO;
import com.sgi.fiis.observaciones.application.dto.SubsanacionResponseDTO;
import com.sgi.fiis.observaciones.domain.exception.ObservacionNotFoundException;
import com.sgi.fiis.observaciones.domain.exception.SubsanacionInvalidaException;
import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.model.Subsanacion;
import com.sgi.fiis.observaciones.domain.port.ObservacionRepository;
import com.sgi.fiis.observaciones.domain.port.SubsanacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Registrar una subsanación (RF-62, RF-63).
 */
@Service
@RequiredArgsConstructor
public class RegistraSubsanacionUseCase {

    private final ObservacionRepository observacionRepository;
    private final SubsanacionRepository subsanacionRepository;

    @Transactional
    public SubsanacionResponseDTO execute(Integer idObservacion, SubsanacionRequestDTO dto) {
        Observacion observacion = observacionRepository.findById(idObservacion)
                .orElseThrow(() -> new ObservacionNotFoundException(idObservacion));

        if (!observacion.esSubsanable()) {
            throw new SubsanacionInvalidaException(idObservacion);
        }

        Subsanacion subsanacion = Subsanacion.crear(
                idObservacion, dto.getIdSolicitante(),
                dto.getDescripcion(), dto.getIdDocumentoAdjunto());

        observacion.marcarSubsanada();
        observacionRepository.save(observacion);
        Subsanacion guardada = subsanacionRepository.save(subsanacion);

        return SubsanacionResponseDTO.builder()
                .id(guardada.getId()).idObservacion(guardada.getIdObservacion())
                .idSolicitante(guardada.getIdSolicitante())
                .descripcion(guardada.getDescripcion())
                .idDocumentoAdjunto(guardada.getIdDocumentoAdjunto())
                .fechaRegistro(guardada.getFechaRegistro())
                .fechaActualizacion(guardada.getFechaActualizacion()).build();
    }
}
