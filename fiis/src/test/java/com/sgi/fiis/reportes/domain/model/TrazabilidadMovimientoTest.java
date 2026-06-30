package com.sgi.fiis.reportes.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para {@link TrazabilidadMovimiento}.
 * Verifica que el constructor por defecto y todos los getters/setters funcionan correctamente.
 */
@DisplayName("TrazabilidadMovimiento - Pruebas unitarias")
class TrazabilidadMovimientoTest {

    @Test
    @DisplayName("Constructor por defecto debe crear instancia sin errores")
    void constructor_debeCrearInstanciaVacia() {
        TrazabilidadMovimiento m = new TrazabilidadMovimiento();
        assertThat(m).isNotNull();
    }

    @Test
    @DisplayName("Getters y setters deben funcionar correctamente para todos los campos")
    void gettersYSetters_debenFuncionarCorrectamente() {
        TrazabilidadMovimiento m = new TrazabilidadMovimiento();

        LocalDateTime fecha = LocalDateTime.of(2024, Month.JANUARY, 15, 9, 0);

        m.setIdMovimiento(1);
        m.setIdTramite(10);
        m.setCodigoTramite("TRM-2024-001");
        m.setNombreUsuarioAccion("Juan Pérez");
        m.setAccion("CREACION");
        m.setEstadoAnterior(null);
        m.setEstadoNuevo("PENDIENTE");
        m.setObservacion("Trámite creado");
        m.setFechaMovimiento(fecha);

        assertThat(m.getIdMovimiento()).isEqualTo(1);
        assertThat(m.getIdTramite()).isEqualTo(10);
        assertThat(m.getCodigoTramite()).isEqualTo("TRM-2024-001");
        assertThat(m.getNombreUsuarioAccion()).isEqualTo("Juan Pérez");
        assertThat(m.getAccion()).isEqualTo("CREACION");
        assertThat(m.getEstadoAnterior()).isNull();
        assertThat(m.getEstadoNuevo()).isEqualTo("PENDIENTE");
        assertThat(m.getObservacion()).isEqualTo("Trámite creado");
        assertThat(m.getFechaMovimiento()).isEqualTo(fecha);
    }

    @Test
    @DisplayName("Todos los campos deben ser null en instancia recién creada")
    void camposNulos_enInstanciaRecienCreada() {
        TrazabilidadMovimiento m = new TrazabilidadMovimiento();

        assertThat(m.getIdMovimiento()).isNull();
        assertThat(m.getIdTramite()).isNull();
        assertThat(m.getCodigoTramite()).isNull();
        assertThat(m.getNombreUsuarioAccion()).isNull();
        assertThat(m.getAccion()).isNull();
        assertThat(m.getEstadoAnterior()).isNull();
        assertThat(m.getEstadoNuevo()).isNull();
        assertThat(m.getObservacion()).isNull();
        assertThat(m.getFechaMovimiento()).isNull();
    }
}
