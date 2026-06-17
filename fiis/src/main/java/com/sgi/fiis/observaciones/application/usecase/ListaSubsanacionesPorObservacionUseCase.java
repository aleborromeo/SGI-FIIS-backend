package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.SubsanacionResponseDTO;
import com.sgi.fiis.observaciones.domain.model.Subsanacion;
import com.sgi.fiis.observaciones.domain.port.SubsanacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Caso de uso: Listar subsanaciones por observación (RF-64 trazabilidad).
 */
@Service
@RequiredArgsConstructor
public class ListaSubsanacionesPorObservacionUseCase {

    private final SubsanacionRepository subsanacionRepository;

    @Transactional(readOnly = true)
    public List<SubsanacionResponseDTO> execute(Integer idObservacion) {
        return subsanacionRepository.findByIdObservacion(idObservacion).stream()
                .map(this::toDTO).toList();
    }

    private SubsanacionResponseDTO toDTO(Subsanacion s) {
        return SubsanacionResponseDTO.builder()
                .id(s.getId()).idObservacion(s.getIdObservacion())
                .idSolicitante(s.getIdSolicitante()).descripcion(s.getDescripcion())
                .idDocumentoAdjunto(s.getIdDocumentoAdjunto())
                .fechaRegistro(s.getFechaRegistro())
                .fechaActualizacion(s.getFechaActualizacion()).build();
    }
}
