package com.sgi.fiis.reportes.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para {@link ReporteInformeAvance}.
 * Verifica que el constructor por defecto y todos los getters/setters funcionan correctamente.
 */
@DisplayName("ReporteInformeAvance - Pruebas unitarias")
class ReporteInformeAvanceTest {

    @Test
    @DisplayName("Constructor por defecto debe crear instancia sin errores")
    void constructor_debeCrearInstanciaVacia() {
        ReporteInformeAvance ia = new ReporteInformeAvance();
        assertThat(ia).isNotNull();
    }

    @Test
    @DisplayName("Getters y setters deben funcionar correctamente para todos los campos")
    void gettersYSetters_debenFuncionarCorrectamente() {
        ReporteInformeAvance ia = new ReporteInformeAvance();

        LocalDateTime reg = LocalDateTime.of(2024, Month.JUNE, 1, 12, 0);

        ia.setIdInforme(2);
        ia.setCodigoProyecto("PRY-2024-001");
        ia.setTituloProyecto("Sistema de detección IA");
        ia.setTipoInforme("PARCIAL");
        ia.setPeriodo("2024-I");
        ia.setPorcentajeAvance(BigDecimal.valueOf(45.5));
        ia.setEstadoInforme("APROBADO");
        ia.setNombreGrupo("Grupo IA");
        ia.setFechaRegistro(reg);

        assertThat(ia.getIdInforme()).isEqualTo(2);
        assertThat(ia.getCodigoProyecto()).isEqualTo("PRY-2024-001");
        assertThat(ia.getTituloProyecto()).isEqualTo("Sistema de detección IA");
        assertThat(ia.getTipoInforme()).isEqualTo("PARCIAL");
        assertThat(ia.getPeriodo()).isEqualTo("2024-I");
        assertThat(ia.getPorcentajeAvance()).isEqualByComparingTo(BigDecimal.valueOf(45.5));
        assertThat(ia.getEstadoInforme()).isEqualTo("APROBADO");
        assertThat(ia.getNombreGrupo()).isEqualTo("Grupo IA");
        assertThat(ia.getFechaRegistro()).isEqualTo(reg);
    }

    @Test
    @DisplayName("Todos los campos deben ser null en instancia recién creada")
    void camposNulos_enInstanciaRecienCreada() {
        ReporteInformeAvance ia = new ReporteInformeAvance();

        assertThat(ia.getIdInforme()).isNull();
        assertThat(ia.getCodigoProyecto()).isNull();
        assertThat(ia.getTituloProyecto()).isNull();
        assertThat(ia.getTipoInforme()).isNull();
        assertThat(ia.getPeriodo()).isNull();
        assertThat(ia.getPorcentajeAvance()).isNull();
        assertThat(ia.getEstadoInforme()).isNull();
        assertThat(ia.getNombreGrupo()).isNull();
        assertThat(ia.getFechaRegistro()).isNull();
    }
}
