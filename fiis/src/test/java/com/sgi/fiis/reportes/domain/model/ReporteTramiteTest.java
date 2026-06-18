package com.sgi.fiis.reportes.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para {@link ReporteTramite}.
 * Verifica que el constructor por defecto y todos los getters/setters funcionan correctamente.
 */
@DisplayName("ReporteTramite - Pruebas unitarias")
class ReporteTramiteTest {

    @Test
    @DisplayName("Constructor por defecto debe crear instancia sin errores")
    void constructor_debeCrearInstanciaVacia() {
        ReporteTramite t = new ReporteTramite();
        assertThat(t).isNotNull();
    }

    @Test
    @DisplayName("Getters y setters deben funcionar correctamente para todos los campos")
    void gettersYSetters_debenFuncionarCorrectamente() {
        ReporteTramite t = new ReporteTramite();

        LocalDateTime envio  = LocalDateTime.of(2024, Month.JANUARY, 10, 8, 0);
        LocalDateTime actual = LocalDateTime.of(2024, Month.FEBRUARY, 5, 16, 45);

        t.setIdTramite(5);
        t.setCodigoTramite("TRM-2024-005");
        t.setTipoTramite("PROYECTO");
        t.setNombreSolicitante("Ana García");
        t.setEstadoActual("EN_REVISION");
        t.setRolRevisorActual("DIRECTOR");
        t.setNombreGrupo("Grupo de Software");
        t.setFechaEnvio(envio);
        t.setFechaActualizacion(actual);

        assertThat(t.getIdTramite()).isEqualTo(5);
        assertThat(t.getCodigoTramite()).isEqualTo("TRM-2024-005");
        assertThat(t.getTipoTramite()).isEqualTo("PROYECTO");
        assertThat(t.getNombreSolicitante()).isEqualTo("Ana García");
        assertThat(t.getEstadoActual()).isEqualTo("EN_REVISION");
        assertThat(t.getRolRevisorActual()).isEqualTo("DIRECTOR");
        assertThat(t.getNombreGrupo()).isEqualTo("Grupo de Software");
        assertThat(t.getFechaEnvio()).isEqualTo(envio);
        assertThat(t.getFechaActualizacion()).isEqualTo(actual);
    }

    @Test
    @DisplayName("Todos los campos deben ser null en instancia recién creada")
    void camposNulos_enInstanciaRecienCreada() {
        ReporteTramite t = new ReporteTramite();

        assertThat(t.getIdTramite()).isNull();
        assertThat(t.getCodigoTramite()).isNull();
        assertThat(t.getTipoTramite()).isNull();
        assertThat(t.getNombreSolicitante()).isNull();
        assertThat(t.getEstadoActual()).isNull();
        assertThat(t.getRolRevisorActual()).isNull();
        assertThat(t.getNombreGrupo()).isNull();
        assertThat(t.getFechaEnvio()).isNull();
        assertThat(t.getFechaActualizacion()).isNull();
    }
}
