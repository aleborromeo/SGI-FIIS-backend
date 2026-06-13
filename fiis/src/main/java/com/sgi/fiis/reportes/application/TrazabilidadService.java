package com.sgi.fiis.reportes.application;

import com.sgi.fiis.reportes.domain.TrazabilidadMovimiento;
import com.sgi.fiis.reportes.infrastructure.persistence.TrazabilidadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de trazabilidad de trámites (RF-96 a RF-99).
 * Devuelve el historial cronológico de movimientos de un trámite específico.
 */
@Service
public class TrazabilidadService {

    private final TrazabilidadRepository repo;

    public TrazabilidadService(TrazabilidadRepository repo) {
        this.repo = repo;
    }

    /**
     * Consulta todos los movimientos de un trámite en orden cronológico ascendente.
     * Incluye: usuario, acción, estado anterior/nuevo, observación y fecha.
     *
     * @param idTramite identificador del trámite a consultar
     * @return lista vacía si el trámite no existe o no tiene movimientos registrados
     */
    public List<TrazabilidadMovimiento> consultarTrazabilidad(Integer idTramite) {
        return repo.findByIdTramite(idTramite);
    }
}
