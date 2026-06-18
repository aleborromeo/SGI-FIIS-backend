package pe.unas.fiis.sgifiis.thesis.domain.port.out;

public interface GrupoInvestigacionValidationPort {
    boolean existeGrupoActivo(Integer idGrupo);
    boolean existeLineaActiva(Integer idLinea);
    boolean lineaPerteneceAlGrupo(Integer idGrupo, Integer idLinea);
}
