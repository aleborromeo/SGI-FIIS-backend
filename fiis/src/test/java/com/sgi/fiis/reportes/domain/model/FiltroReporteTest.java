package com.sgi.fiis.reportes.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para {@link FiltroReporte}.
 * Verifica valores por defecto, validaciones de rango y cálculo de offset.
 */
@DisplayName("FiltroReporte - Pruebas unitarias")
class FiltroReporteTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Valores por defecto
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Constructor sin args debe inicializar page=0 y size=20")
    void constructor_debeTenerValoresPorDefecto() {
        FiltroReporte f = new FiltroReporte();

        assertThat(f.getPage()).isZero();
        assertThat(f.getSize()).isEqualTo(20);
        assertThat(f.getOffset()).isZero();
    }

    @Test
    @DisplayName("Todos los campos opcionales deben ser null por defecto")
    void constructor_camposOpcionalesDebenSerNull() {
        FiltroReporte f = new FiltroReporte();

        assertThat(f.getIdGrupo()).isNull();
        assertThat(f.getEstado()).isNull();
        assertThat(f.getFechaDesde()).isNull();
        assertThat(f.getFechaHasta()).isNull();
        assertThat(f.getIdInvestigador()).isNull();
        assertThat(f.getIdConvocatoria()).isNull();
        assertThat(f.getTipoTramite()).isNull();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validación de setPage
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("setPage con valor negativo debe quedar en 0")
    void setPage_conValorNegativo_debeQuedarEnCero() {
        FiltroReporte f = new FiltroReporte();
        f.setPage(-5);
        assertThat(f.getPage()).isZero();
    }

    @Test
    @DisplayName("setPage con valor positivo debe asignarlo correctamente")
    void setPage_conValorPositivo_debeAsignarse() {
        FiltroReporte f = new FiltroReporte();
        f.setPage(3);
        assertThat(f.getPage()).isEqualTo(3);
    }

    @Test
    @DisplayName("setPage con cero debe asignarlo correctamente")
    void setPage_conCero_debeAsignarse() {
        FiltroReporte f = new FiltroReporte();
        f.setPage(0);
        assertThat(f.getPage()).isZero();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validación de setSize
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("setSize con valor válido (1-100) debe asignarse")
    void setSize_conValorValido_debeAsignarse() {
        FiltroReporte f = new FiltroReporte();
        f.setSize(50);
        assertThat(f.getSize()).isEqualTo(50);
    }

    @Test
    @DisplayName("setSize con 0 debe caer al default 20")
    void setSize_conCero_debeUsarDefault() {
        FiltroReporte f = new FiltroReporte();
        f.setSize(0);
        assertThat(f.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("setSize con valor negativo debe caer al default 20")
    void setSize_conNegativo_debeUsarDefault() {
        FiltroReporte f = new FiltroReporte();
        f.setSize(-10);
        assertThat(f.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("setSize con valor mayor a 100 debe caer al default 20")
    void setSize_conMasDe100_debeUsarDefault() {
        FiltroReporte f = new FiltroReporte();
        f.setSize(200);
        assertThat(f.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("setSize con valor 100 (límite máximo) debe asignarse")
    void setSize_con100_debeAsignarse() {
        FiltroReporte f = new FiltroReporte();
        f.setSize(100);
        assertThat(f.getSize()).isEqualTo(100);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cálculo de offset
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getOffset debe retornar page * size correctamente")
    void getOffset_debeCalcularseCorrectamente() {
        FiltroReporte f = new FiltroReporte();
        f.setPage(2);
        f.setSize(10);
        assertThat(f.getOffset()).isEqualTo(20);
    }

    @Test
    @DisplayName("getOffset en página 0 debe ser 0")
    void getOffset_enPaginaCero_debeSerCero() {
        FiltroReporte f = new FiltroReporte();
        f.setPage(0);
        f.setSize(15);
        assertThat(f.getOffset()).isZero();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Setters de campos opcionales
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Getters y setters de campos opcionales deben funcionar correctamente")
    void settersYGettersOpcionales_debenFuncionar() {
        FiltroReporte f = new FiltroReporte();
        LocalDate desde = LocalDate.of(2024, 1, 1);
        LocalDate hasta = LocalDate.of(2024, 12, 31);

        f.setIdGrupo(1);
        f.setEstado("APROBADO");
        f.setFechaDesde(desde);
        f.setFechaHasta(hasta);
        f.setIdInvestigador(5);
        f.setIdConvocatoria(2);
        f.setTipoTramite("PROYECTO");

        assertThat(f.getIdGrupo()).isEqualTo(1);
        assertThat(f.getEstado()).isEqualTo("APROBADO");
        assertThat(f.getFechaDesde()).isEqualTo(desde);
        assertThat(f.getFechaHasta()).isEqualTo(hasta);
        assertThat(f.getIdInvestigador()).isEqualTo(5);
        assertThat(f.getIdConvocatoria()).isEqualTo(2);
        assertThat(f.getTipoTramite()).isEqualTo("PROYECTO");
    }
}
