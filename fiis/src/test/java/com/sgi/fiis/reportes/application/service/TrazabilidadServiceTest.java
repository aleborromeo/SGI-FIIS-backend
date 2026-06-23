package com.sgi.fiis.reportes.application.service;

import com.sgi.fiis.reportes.domain.model.TrazabilidadMovimiento;
import com.sgi.fiis.reportes.domain.repository.TrazabilidadRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link TrazabilidadService}.
 * Se usa Mockito para aislar el servicio del repositorio real (RF-96 a RF-99).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TrazabilidadService - Pruebas unitarias")
class TrazabilidadServiceTest {

    @Mock
    private TrazabilidadRepositoryPort repo;

    @InjectMocks
    private TrazabilidadService service;

    private TrazabilidadMovimiento movimiento1;
    private TrazabilidadMovimiento movimiento2;

    @BeforeEach
    void setUp() {
        movimiento1 = new TrazabilidadMovimiento();
        movimiento1.setIdMovimiento(1);
        movimiento1.setIdTramite(10);
        movimiento1.setCodigoTramite("TRM-2024-001");
        movimiento1.setNombreUsuarioAccion("Juan Pérez");
        movimiento1.setAccion("CREACION");
        movimiento1.setEstadoAnterior(null);
        movimiento1.setEstadoNuevo("PENDIENTE");
        movimiento1.setObservacion("Trámite creado");
        movimiento1.setFechaMovimiento(LocalDateTime.of(2024, Month.JANUARY, 15, 9, 0));

        movimiento2 = new TrazabilidadMovimiento();
        movimiento2.setIdMovimiento(2);
        movimiento2.setIdTramite(10);
        movimiento2.setCodigoTramite("TRM-2024-001");
        movimiento2.setNombreUsuarioAccion("María García");
        movimiento2.setAccion("APROBACION");
        movimiento2.setEstadoAnterior("PENDIENTE");
        movimiento2.setEstadoNuevo("APROBADO");
        movimiento2.setObservacion("Revisado y aprobado");
        movimiento2.setFechaMovimiento(LocalDateTime.of(2024, Month.JANUARY, 16, 14, 30));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RF-96: Consulta exitosa con múltiples movimientos
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("RF-96: Debe retornar historial completo de un trámite con movimientos")
    void consultarTrazabilidad_conMovimientos_retornaLista() {
        // arrange
        when(repo.findByIdTramite(10)).thenReturn(List.of(movimiento1, movimiento2));

        // act
        List<TrazabilidadMovimiento> resultado = service.consultarTrazabilidad(10);

        // assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getAccion()).isEqualTo("CREACION");
        assertThat(resultado.get(1).getAccion()).isEqualTo("APROBACION");
        verify(repo, times(1)).findByIdTramite(10);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RF-97: Trámite sin movimientos → lista vacía
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("RF-97: Debe retornar lista vacía cuando el trámite no tiene movimientos")
    void consultarTrazabilidad_sinMovimientos_retornaListaVacia() {
        // arrange
        when(repo.findByIdTramite(999)).thenReturn(Collections.emptyList());

        // act
        List<TrazabilidadMovimiento> resultado = service.consultarTrazabilidad(999);

        // assert
        assertThat(resultado).isEmpty();
        verify(repo, times(1)).findByIdTramite(999);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RF-98: Verificar que el repositorio se llama exactamente una vez
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("RF-98: Debe delegar la consulta al repositorio exactamente una vez")
    void consultarTrazabilidad_debeInvocarRepositorioUnaVez() {
        // arrange
        when(repo.findByIdTramite(anyInt())).thenReturn(Collections.emptyList());

        // act
        service.consultarTrazabilidad(5);

        // assert
        verify(repo, times(1)).findByIdTramite(5);
        verifyNoMoreInteractions(repo);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RF-99: Verificar campos completos del movimiento retornado
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("RF-99: Debe retornar los campos completos del movimiento correctamente")
    void consultarTrazabilidad_verificaCamposDelMovimiento() {
        // arrange
        when(repo.findByIdTramite(10)).thenReturn(List.of(movimiento1));

        // act
        List<TrazabilidadMovimiento> resultado = service.consultarTrazabilidad(10);

        // assert
        TrazabilidadMovimiento mov = resultado.get(0);
        assertThat(mov.getIdMovimiento()).isEqualTo(1);
        assertThat(mov.getIdTramite()).isEqualTo(10);
        assertThat(mov.getCodigoTramite()).isEqualTo("TRM-2024-001");
        assertThat(mov.getNombreUsuarioAccion()).isEqualTo("Juan Pérez");
        assertThat(mov.getAccion()).isEqualTo("CREACION");
        assertThat(mov.getEstadoAnterior()).isNull();
        assertThat(mov.getEstadoNuevo()).isEqualTo("PENDIENTE");
        assertThat(mov.getObservacion()).isEqualTo("Trámite creado");
        assertThat(mov.getFechaMovimiento()).isEqualTo(LocalDateTime.of(2024, Month.JANUARY, 15, 9, 0));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RNF-46: Trámite con un único movimiento
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("RNF-46: Debe manejar correctamente un trámite con un solo movimiento")
    void consultarTrazabilidad_unSoloMovimiento_retornaListaDeUno() {
        // arrange
        when(repo.findByIdTramite(10)).thenReturn(List.of(movimiento1));

        // act
        List<TrazabilidadMovimiento> resultado = service.consultarTrazabilidad(10);

        // assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdMovimiento()).isEqualTo(1);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RNF-47: Trámite con idTramite = null no debe lanzar excepción interna
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("RNF-47: Debe retornar lista vacía para idTramite inexistente (null simulado)")
    void consultarTrazabilidad_idInexistente_retornaListaVacia() {
        // arrange — simulamos que la BD no encuentra nada para un ID muy grande
        when(repo.findByIdTramite(Integer.MAX_VALUE)).thenReturn(Collections.emptyList());

        // act
        List<TrazabilidadMovimiento> resultado = service.consultarTrazabilidad(Integer.MAX_VALUE);

        // assert
        assertThat(resultado).isNotNull().isEmpty();
    }
}
