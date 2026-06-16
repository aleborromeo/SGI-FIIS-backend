package com.sgi.fiis.reportes.domain.model;

import java.time.LocalDate;

/**
 * Filtros combinables para reportes institucionales.
 * Todos los campos son opcionales; se aplican sólo si no son null.
 */
public class FiltroReporte {

    private Integer idGrupo;
    private String  estado;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Integer idInvestigador;
    private Integer idConvocatoria;
    private String  tipoTramite;
    private int     page;
    private int     size;

    public FiltroReporte() {
        this.page = 0;
        this.size = 20;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer getIdGrupo() { return idGrupo; }
    public void setIdGrupo(Integer idGrupo) { this.idGrupo = idGrupo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }

    public Integer getIdInvestigador() { return idInvestigador; }
    public void setIdInvestigador(Integer idInvestigador) { this.idInvestigador = idInvestigador; }

    public Integer getIdConvocatoria() { return idConvocatoria; }
    public void setIdConvocatoria(Integer idConvocatoria) { this.idConvocatoria = idConvocatoria; }

    public String getTipoTramite() { return tipoTramite; }
    public void setTipoTramite(String tipoTramite) { this.tipoTramite = tipoTramite; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(0, page); }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = (size > 0 && size <= 100) ? size : 20; }

    /** Offset calculado para LIMIT/OFFSET en SQL. */
    public int getOffset() { return page * size; }
}
