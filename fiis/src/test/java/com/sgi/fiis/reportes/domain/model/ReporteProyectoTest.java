package com.sgi.fiis.reportes.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para {@link ReporteProyecto}.
 * Verifica que el constructor por defecto y todos los getters/setters funcionan correctamente.
 */
@DisplayName("ReporteProyecto - Pruebas unitarias")
class ReporteProyectoTest {

    @Test
    @DisplayName("Constructor por defecto debe crear instancia sin errores")
    void constructor_debeCrearInstanciaVacia() {
        ReporteProyecto r = new ReporteProyecto();
        assertThat(r).isNotNull();
    }

    @Test
    @DisplayName("Getters y setters deben funcionar correctamente para todos los campos")
    void gettersYSetters_debenFuncionarCorrectamente() {
        ReporteProyecto r = new ReporteProyecto();

        LocalDate inicio    = LocalDate.of(2024, Month.MARCH, 1);
        LocalDate fin       = LocalDate.of(2024, Month.DECEMBER, 31);
        LocalDateTime crea  = LocalDateTime.of(2024, Month.FEBRUARY, 15, 10, 30);

        r.setIdProyecto(1);
        r.setCodigoProyecto("PRY-2024-001");
        r.setTituloProyecto("Sistema de Gestión");
        r.setEstadoProyecto("APROBADO");
        r.setNombreGrupo("Grupo IA");
        r.setNombreLinea("Ingeniería de Software");
        r.setNombreResponsable("Juan Pérez");
        r.setTituloConvocatoria("Convocatoria 2024-I");
        r.setPresupuesto(BigDecimal.valueOf(15000.50));
        r.setFechaInicio(inicio);
        r.setFechaFin(fin);
        r.setFechaCreacion(crea);

        assertThat(r.getIdProyecto()).isEqualTo(1);
        assertThat(r.getCodigoProyecto()).isEqualTo("PRY-2024-001");
        assertThat(r.getTituloProyecto()).isEqualTo("Sistema de Gestión");
        assertThat(r.getEstadoProyecto()).isEqualTo("APROBADO");
        assertThat(r.getNombreGrupo()).isEqualTo("Grupo IA");
        assertThat(r.getNombreLinea()).isEqualTo("Ingeniería de Software");
        assertThat(r.getNombreResponsable()).isEqualTo("Juan Pérez");
        assertThat(r.getTituloConvocatoria()).isEqualTo("Convocatoria 2024-I");
        assertThat(r.getPresupuesto()).isEqualByComparingTo(BigDecimal.valueOf(15000.50));
        assertThat(r.getFechaInicio()).isEqualTo(inicio);
        assertThat(r.getFechaFin()).isEqualTo(fin);
        assertThat(r.getFechaCreacion()).isEqualTo(crea);
    }

    @Test
    @DisplayName("Todos los campos deben ser null en instancia recién creada")
    void camposNulos_enInstanciaRecienCreada() {
        ReporteProyecto r = new ReporteProyecto();

        assertThat(r.getIdProyecto()).isNull();
        assertThat(r.getCodigoProyecto()).isNull();
        assertThat(r.getTituloProyecto()).isNull();
        assertThat(r.getEstadoProyecto()).isNull();
        assertThat(r.getNombreGrupo()).isNull();
        assertThat(r.getNombreLinea()).isNull();
        assertThat(r.getNombreResponsable()).isNull();
        assertThat(r.getTituloConvocatoria()).isNull();
        assertThat(r.getPresupuesto()).isNull();
        assertThat(r.getFechaInicio()).isNull();
        assertThat(r.getFechaFin()).isNull();
        assertThat(r.getFechaCreacion()).isNull();
    }
}
