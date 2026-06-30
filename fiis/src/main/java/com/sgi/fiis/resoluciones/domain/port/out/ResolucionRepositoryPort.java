package com.sgi.fiis.resoluciones.domain.port.out;

import com.sgi.fiis.resoluciones.domain.model.Resolucion;
import java.util.Optional;

public interface ResolucionRepositoryPort {
    Resolucion guardar(Resolucion resolucion);
    Optional<Resolucion> buscarPorId(Long idResolucion);
    boolean existePorNumero(String numeroResolucion);
}
