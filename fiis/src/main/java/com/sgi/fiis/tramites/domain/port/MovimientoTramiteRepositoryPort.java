package com.sgi.fiis.tramites.domain.port;

import com.sgi.fiis.tramites.domain.model.MovimientoTramite;

import java.util.List;

public interface MovimientoTramiteRepositoryPort {

    List<MovimientoTramite> buscarPorIdTramite(Long idTramite);
}
