package com.sgi.fiis.reportes.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para {@link ReporteResolucion}.
 * Verifica que el constructor por defecto y todos los getters/setters funcionan correctamente.
 */
@DisplayName("ReporteResolucion - Pruebas unitarias")
class ReporteResolucionTest {

    @Test
    @DisplayName("Constructor por defecto debe crear instancia sin errores")
    void constructor_debeCrearInstanciaVacia() {
        ReporteResolucion r = new ReporteResolucion();
        assertThat(r).isNotNull();
    }

    @Test
    @DisplayName("Getters y setters deben funcionar correctamente para todos los campos")
    void gettersYSetters_debenFuncionarCorrectamente() {
        ReporteResolucion r = new ReporteResolucion();

        LocalDate emision   = LocalDate.of(2024, Month.APRIL, 10);
        LocalDateTime reg   = LocalDateTime.of(2024, Month.APRIL, 10, 9, 0);

        r.setIdResolucion(3);
        r.setNumeroResolucion("RES-001-2024");
        r.setFechaEmision(emision);
        r.setAsunto("Aprobación de proyecto");
        r.setCodigoTramite("TRM-2024-001");
        r.setTipoTramite("PROYECTO");
        r.setNombreSolicitante("Luis Torres");
        r.setFechaRegistro(reg);

        assertThat(r.getIdResolucion()).isEqualTo(3);
        assertThat(r.getNumeroResolucion()).isEqualTo("RES-001-2024");
        assertThat(r.getFechaEmision()).isEqualTo(emision);
        assertThat(r.getAsunto()).isEqualTo("Aprobación de proyecto");
        assertThat(r.getCodigoTramite()).isEqualTo("TRM-2024-001");
        assertThat(r.getTipoTramite()).isEqualTo("PROYECTO");
        assertThat(r.getNombreSolicitante()).isEqualTo("Luis Torres");
        assertThat(r.getFechaRegistro()).isEqualTo(reg);
    }

    @Test
    @DisplayName("Todos los campos deben ser null en instancia recién creada")
    void camposNulos_enInstanciaRecienCreada() {
        ReporteResolucion r = new ReporteResolucion();

        assertThat(r.getIdResolucion()).isNull();
        assertThat(r.getNumeroResolucion()).isNull();
        assertThat(r.getFechaEmision()).isNull();
        assertThat(r.getAsunto()).isNull();
        assertThat(r.getCodigoTramite()).isNull();
        assertThat(r.getTipoTramite()).isNull();
        assertThat(r.getNombreSolicitante()).isNull();
        assertThat(r.getFechaRegistro()).isNull();
    }
}
