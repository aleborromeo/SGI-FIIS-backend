package com.sgi.fiis.reportes.infrastructure.persistence;

import com.sgi.fiis.reportes.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteRepositoryImpl Unit Tests")
class ReporteRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private ReporteRepositoryImpl repository;

    @Test
    @DisplayName("Should successfully find and map Proyectos with various filters")
    @SuppressWarnings("unchecked")
    void testFindProyectos() throws SQLException {
        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdGrupo(1);
        filtro.setEstado("ACTIVO");
        filtro.setFechaDesde(LocalDate.of(2026, Month.JANUARY, 1));
        filtro.setFechaHasta(LocalDate.of(2026, Month.DECEMBER, 31));
        filtro.setIdInvestigador(2);
        filtro.setIdConvocatoria(3);
        filtro.setSize(10);
        filtro.setPage(0);

        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_proyecto")).thenReturn(100);
        when(rs.getString("codigo_proyecto")).thenReturn("PRJ-100");
        when(rs.getString("titulo_proyecto")).thenReturn("Proyecto de Prueba");
        when(rs.getString("estado_proyecto")).thenReturn("ACTIVO");
        when(rs.getString("nombre_grupo")).thenReturn("Grupo Alfa");
        when(rs.getString("nombre_linea")).thenReturn("Linea de Tecnologia");
        when(rs.getString("nombre_responsable")).thenReturn("Juan Perez");
        when(rs.getString("titulo_convocatoria")).thenReturn("Convocatoria 2026");
        when(rs.getBigDecimal("presupuesto")).thenReturn(new BigDecimal("15000.00"));
        when(rs.getObject("fecha_inicio", LocalDate.class)).thenReturn(LocalDate.of(2026, Month.FEBRUARY, 1));
        when(rs.getObject("fecha_fin", LocalDate.class)).thenReturn(LocalDate.of(2026, Month.NOVEMBER, 30));
        when(rs.getObject("fecha_creacion", LocalDateTime.class)).thenReturn(LocalDateTime.of(2026, Month.JANUARY, 15, 10, 0));

        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenAnswer(invocation -> {
            RowMapper<ReporteProyecto> mapper = invocation.getArgument(1);
            ReporteProyecto mapped = mapper.mapRow(rs, 0);
            return List.of(mapped);
        });

        List<ReporteProyecto> result = repository.findProyectos(filtro);

        assertNotNull(result);
        assertEquals(1, result.size());
        ReporteProyecto p = result.get(0);
        assertEquals(100, p.getIdProyecto());
        assertEquals("PRJ-100", p.getCodigoProyecto());
        assertEquals("Proyecto de Prueba", p.getTituloProyecto());
        assertEquals("ACTIVO", p.getEstadoProyecto());
        assertEquals("Grupo Alfa", p.getNombreGrupo());
        assertEquals("Linea de Tecnologia", p.getNombreLinea());
        assertEquals("Juan Perez", p.getNombreResponsable());
        assertEquals("Convocatoria 2026", p.getTituloConvocatoria());
        assertEquals(new BigDecimal("15000.00"), p.getPresupuesto());
        assertEquals(LocalDate.of(2026, Month.FEBRUARY, 1), p.getFechaInicio());
        assertEquals(LocalDate.of(2026, Month.NOVEMBER, 30), p.getFechaFin());
        assertEquals(LocalDateTime.of(2026, Month.JANUARY, 15, 10, 0), p.getFechaCreacion());
    }

    @Test
    @DisplayName("Should successfully count Proyectos with filters")
    void testCountProyectos() {
        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdGrupo(1);
        filtro.setEstado("ACTIVO");

        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(42L);

        long count = repository.countProyectos(filtro);

        assertEquals(42L, count);
    }

    @Test
    @DisplayName("Should return 0 for countProyectos when jdbc returns null")
    void testCountProyectosNull() {
        FiltroReporte filtro = new FiltroReporte();
        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(null);

        long count = repository.countProyectos(filtro);

        assertEquals(0L, count);
    }

    @Test
    @DisplayName("Should successfully find and map Tramites with filters")
    @SuppressWarnings("unchecked")
    void testFindTramites() throws SQLException {
        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdGrupo(5);
        filtro.setEstado("EN_REVISION");
        filtro.setFechaDesde(LocalDate.of(2026, Month.MAY, 1));
        filtro.setFechaHasta(LocalDate.of(2026, Month.MAY, 31));
        filtro.setIdInvestigador(10);
        filtro.setTipoTramite("TESIS");
        filtro.setSize(5);
        filtro.setPage(0); // since size is 5, page 0 means offset 0. Let's set page to 0. Or if offset was 2, offset = page * size is not direct, so let's set page to 1. But offset is page * size, let's set page to 0.

        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_tramite")).thenReturn(200);
        when(rs.getString("codigo_tramite")).thenReturn("TRM-200");
        when(rs.getString("tipo_tramite")).thenReturn("TESIS");
        when(rs.getString("nombre_solicitante")).thenReturn("Ana Torres");
        when(rs.getString("estado_actual")).thenReturn("EN_REVISION");
        when(rs.getString("rol_revisor_actual")).thenReturn("DECANO");
        when(rs.getString("nombre_grupo")).thenReturn("Grupo Beta");
        when(rs.getObject("fecha_envio", LocalDateTime.class)).thenReturn(LocalDateTime.of(2026, Month.MAY, 10, 9, 0));
        when(rs.getObject("fecha_actualizacion", LocalDateTime.class)).thenReturn(LocalDateTime.of(2026, Month.MAY, 12, 15, 30));

        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenAnswer(invocation -> {
            RowMapper<ReporteTramite> mapper = invocation.getArgument(1);
            ReporteTramite mapped = mapper.mapRow(rs, 0);
            return List.of(mapped);
        });

        List<ReporteTramite> result = repository.findTramites(filtro);

        assertNotNull(result);
        assertEquals(1, result.size());
        ReporteTramite t = result.get(0);
        assertEquals(200, t.getIdTramite());
        assertEquals("TRM-200", t.getCodigoTramite());
        assertEquals("TESIS", t.getTipoTramite());
        assertEquals("Ana Torres", t.getNombreSolicitante());
        assertEquals("EN_REVISION", t.getEstadoActual());
        assertEquals("DECANO", t.getRolRevisorActual());
        assertEquals("Grupo Beta", t.getNombreGrupo());
        assertEquals(LocalDateTime.of(2026, Month.MAY, 10, 9, 0), t.getFechaEnvio());
        assertEquals(LocalDateTime.of(2026, Month.MAY, 12, 15, 30), t.getFechaActualizacion());
    }

    @Test
    @DisplayName("Should successfully count Tramites with filters")
    void testCountTramites() {
        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdGrupo(5);
        filtro.setEstado("EN_REVISION");

        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(15L);

        long count = repository.countTramites(filtro);

        assertEquals(15L, count);
    }

    @Test
    @DisplayName("Should successfully find and map Resoluciones with filters")
    @SuppressWarnings("unchecked")
    void testFindResoluciones() throws SQLException {
        FiltroReporte filtro = new FiltroReporte();
        filtro.setFechaDesde(LocalDate.of(2026, Month.MARCH, 1));
        filtro.setFechaHasta(LocalDate.of(2026, Month.MARCH, 31));
        filtro.setIdInvestigador(8);
        filtro.setTipoTramite("PROYECTO");
        filtro.setSize(20);
        filtro.setPage(0); // page 0 for offset 0

        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_resolucion")).thenReturn(300);
        when(rs.getString("numero_resolucion")).thenReturn("RES-045-2026");
        when(rs.getObject("fecha_emision", LocalDate.class)).thenReturn(LocalDate.of(2026, Month.MARCH, 15));
        when(rs.getString("asunto")).thenReturn("Aprobacion de Proyecto");
        when(rs.getString("codigo_tramite")).thenReturn("TRM-105");
        when(rs.getString("tipo_tramite")).thenReturn("PROYECTO");
        when(rs.getString("nombre_solicitante")).thenReturn("Carlos Ruiz");
        when(rs.getObject("fecha_registro", LocalDateTime.class)).thenReturn(LocalDateTime.of(2026, Month.MARCH, 16, 8, 30));

        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenAnswer(invocation -> {
            RowMapper<ReporteResolucion> mapper = invocation.getArgument(1);
            ReporteResolucion mapped = mapper.mapRow(rs, 0);
            return List.of(mapped);
        });

        List<ReporteResolucion> result = repository.findResoluciones(filtro);

        assertNotNull(result);
        assertEquals(1, result.size());
        ReporteResolucion r = result.get(0);
        assertEquals(300, r.getIdResolucion());
        assertEquals("RES-045-2026", r.getNumeroResolucion());
        assertEquals(LocalDate.of(2026, Month.MARCH, 15), r.getFechaEmision());
        assertEquals("Aprobacion de Proyecto", r.getAsunto());
        assertEquals("TRM-105", r.getCodigoTramite());
        assertEquals("PROYECTO", r.getTipoTramite());
        assertEquals("Carlos Ruiz", r.getNombreSolicitante());
        assertEquals(LocalDateTime.of(2026, Month.MARCH, 16, 8, 30), r.getFechaRegistro());
    }

    @Test
    @DisplayName("Should successfully count Resoluciones with filters")
    void testCountResoluciones() {
        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdInvestigador(8);

        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(8L);

        long count = repository.countResoluciones(filtro);

        assertEquals(8L, count);
    }

    @Test
    @DisplayName("Should successfully find and map Informes de Avance with filters")
    @SuppressWarnings("unchecked")
    void testFindInformesAvance() throws SQLException {
        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdGrupo(2);
        filtro.setEstado("APROBADO");
        filtro.setFechaDesde(LocalDate.of(2026, Month.APRIL, 1));
        filtro.setFechaHasta(LocalDate.of(2026, Month.APRIL, 30));
        filtro.setTipoTramite("INFORME_FINAL");
        filtro.setSize(10);
        filtro.setPage(0);

        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_informe")).thenReturn(400);
        when(rs.getString("codigo_proyecto")).thenReturn("PRJ-45");
        when(rs.getString("titulo_proyecto")).thenReturn("Estudio de Suelos");
        when(rs.getString("tipo_informe")).thenReturn("INFORME_FINAL");
        when(rs.getString("periodo")).thenReturn("2026-I");
        when(rs.getBigDecimal("porcentaje_avance")).thenReturn(new BigDecimal("100.00"));
        when(rs.getString("estado_informe")).thenReturn("APROBADO");
        when(rs.getString("nombre_grupo")).thenReturn("Grupo Geotecnia");
        when(rs.getObject("fecha_registro", LocalDateTime.class)).thenReturn(LocalDateTime.of(2026, Month.APRIL, 25, 11, 45));

        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenAnswer(invocation -> {
            RowMapper<ReporteInformeAvance> mapper = invocation.getArgument(1);
            ReporteInformeAvance mapped = mapper.mapRow(rs, 0);
            return List.of(mapped);
        });

        List<ReporteInformeAvance> result = repository.findInformesAvance(filtro);

        assertNotNull(result);
        assertEquals(1, result.size());
        ReporteInformeAvance ia = result.get(0);
        assertEquals(400, ia.getIdInforme());
        assertEquals("PRJ-45", ia.getCodigoProyecto());
        assertEquals("Estudio de Suelos", ia.getTituloProyecto());
        assertEquals("INFORME_FINAL", ia.getTipoInforme());
        assertEquals("2026-I", ia.getPeriodo());
        assertEquals(new BigDecimal("100.00"), ia.getPorcentajeAvance());
        assertEquals("APROBADO", ia.getEstadoInforme());
        assertEquals("Grupo Geotecnia", ia.getNombreGrupo());
        assertEquals(LocalDateTime.of(2026, Month.APRIL, 25, 11, 45), ia.getFechaRegistro());
    }

    @Test
    @DisplayName("Should successfully count Informes de Avance with filters")
    void testCountInformesAvance() {
        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdGrupo(2);
        filtro.setEstado("APROBADO");

        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(3L);

        long count = repository.countInformesAvance(filtro);

        assertEquals(3L, count);
    }
}
