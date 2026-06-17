package com.sgi.fiis.observaciones.domain.port;

import com.sgi.fiis.observaciones.domain.model.Observacion;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida del dominio para la persistencia de observaciones.
 */
public interface ObservacionRepository {
    Observacion save(Observacion observacion);
    Optional<Observacion> findById(Integer id);
    List<Observacion> findByIdTramite(Integer idTramite);
    List<Observacion> findPendientesByIdTramite(Integer idTramite);
}
