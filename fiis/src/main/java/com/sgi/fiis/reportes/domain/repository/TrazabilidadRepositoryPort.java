package com.sgi.fiis.reportes.domain.repository;

import com.sgi.fiis.reportes.domain.model.TrazabilidadMovimiento;

import java.util.List;

/**
 * Contrato del repositorio de trazabilidad de trámites (RF-96 a RF-99).
 * Definido en domain para aplicar Dependency Inversion:
 * application depende de esta interfaz, infrastructure la implementa.
 */
public interface TrazabilidadRepositoryPort {

    /**
     * Devuelve todos los movimientos de un trámite en orden cronológico ascendente.
     */
    List<TrazabilidadMovimiento> findByIdTramite(Integer idTramite);
}
