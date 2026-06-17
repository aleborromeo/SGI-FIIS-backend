package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.ObservacionResponseDTO;
import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.port.ObservacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de uso: Listar observaciones por trámite (RF-64 trazabilidad).
 */
@Service
@RequiredArgsConstructor
public class ListaObservacionesPorTramiteUseCase {

    private final ObservacionRepository observacionRepository;

    @Transactional(readOnly = true)
    public List<ObservacionResponseDTO> execute(Integer idTramite) {
        return observacionRepository.findByIdTramite(idTramite).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    private ObservacionResponseDTO toDTO(Observacion o) {
        return ObservacionResponseDTO.builder()
                .id(o.getId()).idTramite(o.getIdTramite()).idRevisor(o.getIdRevisor())
                .tipoObservacion(o.getTipoObservacion().name())
                .descripcion(o.getDescripcion()).estado(o.getEstado().name())
                .rolRevisor(o.getRolRevisor()).fechaRegistro(o.getFechaRegistro())
                .fechaActualizacion(o.getFechaActualizacion()).build();
    }
}
