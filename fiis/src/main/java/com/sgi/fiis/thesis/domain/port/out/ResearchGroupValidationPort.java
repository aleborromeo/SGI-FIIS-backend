package com.sgi.fiis.thesis.domain.port.out;

public interface ResearchGroupValidationPort {
    boolean existeGrupoActivo(Integer idGrupo);
    boolean existeLineaActiva(Integer idLinea);
    boolean lineaPerteneceAlGrupo(Integer idGrupo, Integer idLinea);
}
