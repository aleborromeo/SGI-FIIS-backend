package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.domain.port.LineaInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class RegistrarLineaUseCase {

    private final LineaInvestigacionRepositoryPort lineaRepository;

    public RegistrarLineaUseCase(LineaInvestigacionRepositoryPort lineaRepository) {
        this.lineaRepository = lineaRepository;
    }

    @Transactional
    public LineaInvestigacion execute(LineaInvestigacion linea) {
        if (lineaRepository.existsByNombre(linea.getNombreLinea())) {
            throw new DuplicateResourceException("LineaInvestigacion", "nombre", linea.getNombreLinea());
        }
        linea.setEsActiva(true);
        linea.setFechaCreacion(LocalDateTime.now(ZoneId.systemDefault()));
        linea.setFechaActualizacion(LocalDateTime.now(ZoneId.systemDefault()));
        return lineaRepository.save(linea);
    }
}
