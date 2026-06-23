package com.sgi.fiis.reportes.application.service;

import com.sgi.fiis.reportes.domain.model.*;
import com.sgi.fiis.reportes.domain.repository.ReporteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link ReporteService}.
 * Verifica paginación y delegación al repositorio para RF-94 y RF-95.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteService - Pruebas unitarias")
class ReporteServiceTest {

    @Mock
    private ReporteRepositoryPort repo;

    @InjectMocks
    private ReporteService service;

    private FiltroReporte filtro;

    @BeforeEach
    void setUp() {
        filtro = new FiltroReporte();
        filtro.setPage(0);
        filtro.setSize(10);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RF-94: Reporte de proyectos
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("RF-94: Debe retornar PaginatedResponse con proyectos y total correcto")
    void generarReporteProyectos_conDatos_retornaPaginadoCorrectamente() {
        // arrange
        ReporteProyecto proyecto = new ReporteProyecto();
        when(repo.findProyectos(any())).thenReturn(List.of(proyecto));
        when(repo.countProyectos(any())).thenReturn(1L);

        // act
        PaginatedResponse<ReporteProyecto> respuesta = service.generarReporteProyectos(filtro);

        // assert
        assertThat(respuesta.getData()).hasSize(1);
        assertThat(respuesta.getTotal()).isEqualTo(1L);
        assertThat(respuesta.getPage()).isEqualTo(0);
        assertThat(respuesta.getSize()).isEqualTo(10);
        verify(repo, times(1)).findProyectos(filtro);
        verify(repo, times(1)).countProyectos(filtro);
    }

    @Test
    @DisplayName("RF-94: Debe retornar lista vacía cuando no hay proyectos")
    void generarReporteProyectos_sinDatos_retornaListaVacia() {
        // arrange
        when(repo.findProyectos(any())).thenReturn(Collections.emptyList());
        when(repo.countProyectos(any())).thenReturn(0L);

        // act
        PaginatedResponse<ReporteProyecto> respuesta = service.generarReporteProyectos(filtro);

        // assert
        assertThat(respuesta.getData()).isEmpty();
        assertThat(respuesta.getTotal()).isZero();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RF-95: Reporte de trámites
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("RF-95: Debe retornar PaginatedResponse con trámites y total correcto")
    void generarReporteTramites_conDatos_retornaPaginadoCorrectamente() {
        // arrange
        ReporteTramite tramite = new ReporteTramite();
        when(repo.findTramites(any())).thenReturn(List.of(tramite));
        when(repo.countTramites(any())).thenReturn(5L);

        // act
        PaginatedResponse<ReporteTramite> respuesta = service.generarReporteTramites(filtro);

        // assert
        assertThat(respuesta.getData()).hasSize(1);
        assertThat(respuesta.getTotal()).isEqualTo(5L);
        verify(repo, times(1)).findTramites(filtro);
        verify(repo, times(1)).countTramites(filtro);
    }

    @Test
    @DisplayName("RF-95: Debe retornar lista vacía cuando no hay trámites")
    void generarReporteTramites_sinDatos_retornaListaVacia() {
        // arrange
        when(repo.findTramites(any())).thenReturn(Collections.emptyList());
        when(repo.countTramites(any())).thenReturn(0L);

        // act
        PaginatedResponse<ReporteTramite> respuesta = service.generarReporteTramites(filtro);

        // assert
        assertThat(respuesta.getData()).isEmpty();
        assertThat(respuesta.getTotal()).isZero();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay resoluciones")
    void generarReporteResoluciones_sinDatos_retornaListaVacia() {
        // arrange
        when(repo.findResoluciones(any())).thenReturn(Collections.emptyList());
        when(repo.countResoluciones(any())).thenReturn(0L);

        // act
        PaginatedResponse<ReporteResolucion> respuesta = service.generarReporteResoluciones(filtro);

        // assert
        assertThat(respuesta.getData()).isEmpty();
        assertThat(respuesta.getTotal()).isZero();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay informes de avance")
    void generarReporteInformes_sinDatos_retornaListaVacia() {
        // arrange
        when(repo.findInformesAvance(any())).thenReturn(Collections.emptyList());
        when(repo.countInformesAvance(any())).thenReturn(0L);

        // act
        PaginatedResponse<ReporteInformeAvance> respuesta = service.generarReporteInformes(filtro);

        // assert
        assertThat(respuesta.getData()).isEmpty();
        assertThat(respuesta.getTotal()).isZero();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Reporte de resoluciones
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Debe retornar PaginatedResponse con resoluciones correctamente")
    void generarReporteResoluciones_conDatos_retornaPaginadoCorrectamente() {
        // arrange
        ReporteResolucion resolucion = new ReporteResolucion();
        when(repo.findResoluciones(any())).thenReturn(List.of(resolucion));
        when(repo.countResoluciones(any())).thenReturn(3L);

        // act
        PaginatedResponse<ReporteResolucion> respuesta = service.generarReporteResoluciones(filtro);

        // assert
        assertThat(respuesta.getData()).hasSize(1);
        assertThat(respuesta.getTotal()).isEqualTo(3L);
        verify(repo, times(1)).findResoluciones(filtro);
        verify(repo, times(1)).countResoluciones(filtro);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Reporte de informes de avance
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Debe retornar PaginatedResponse con informes de avance correctamente")
    void generarReporteInformes_conDatos_retornaPaginadoCorrectamente() {
        // arrange
        ReporteInformeAvance informe = new ReporteInformeAvance();
        when(repo.findInformesAvance(any())).thenReturn(List.of(informe));
        when(repo.countInformesAvance(any())).thenReturn(2L);

        // act
        PaginatedResponse<ReporteInformeAvance> respuesta = service.generarReporteInformes(filtro);

        // assert
        assertThat(respuesta.getData()).hasSize(1);
        assertThat(respuesta.getTotal()).isEqualTo(2L);
        verify(repo, times(1)).findInformesAvance(filtro);
        verify(repo, times(1)).countInformesAvance(filtro);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Verificación de paginación: page y size se propagan correctamente
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Debe propagar page y size del filtro a la respuesta paginada")
    void generarReporteProyectos_propagaPageYSize() {
        // arrange
        filtro.setPage(2);
        filtro.setSize(5);
        when(repo.findProyectos(any())).thenReturn(Collections.emptyList());
        when(repo.countProyectos(any())).thenReturn(50L);

        // act
        PaginatedResponse<ReporteProyecto> respuesta = service.generarReporteProyectos(filtro);

        // assert
        assertThat(respuesta.getPage()).isEqualTo(2);
        assertThat(respuesta.getSize()).isEqualTo(5);
        assertThat(respuesta.getTotal()).isEqualTo(50L);
    }
}
