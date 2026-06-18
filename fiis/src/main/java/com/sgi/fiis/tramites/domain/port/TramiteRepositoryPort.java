package com.sgi.fiis.tramites.domain.port;

import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.Tramite;

import java.util.List;
import java.util.Optional;

public interface TramiteRepositoryPort {

    Tramite guardar(Tramite tramite);

    Optional<Tramite> buscarPorId(Long id);

    Optional<Tramite> buscarPorCodigo(String codigoTramite);

    List<Tramite> buscarPorIdSolicitante(Long idSolicitante);

    List<Tramite> buscarPorEstado(EstadoTramite estado);

    boolean existePorCodigo(String codigoTramite);
}
