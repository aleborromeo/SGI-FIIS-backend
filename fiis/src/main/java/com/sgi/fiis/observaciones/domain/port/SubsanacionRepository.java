package com.sgi.fiis.observaciones.domain.port;

import com.sgi.fiis.observaciones.domain.model.Subsanacion;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida del dominio para la persistencia de subsanaciones.
 */
public interface SubsanacionRepository {
    Subsanacion save(Subsanacion subsanacion);
    Optional<Subsanacion> findById(Integer id);
    List<Subsanacion> findByIdObservacion(Integer idObservacion);
}
