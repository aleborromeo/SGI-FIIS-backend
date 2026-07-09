package com.sgi.fiis.reports.infrastructure.persistence;

import com.sgi.fiis.reports.domain.model.*;
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
@DisplayName("ReportRepositoryImpl Unit Tests")
class ReportRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private ReportRepositoryImpl repository;

    @Test
    @DisplayName("Should successfully find and map Projects with various filters")
    @SuppressWarnings("unchecked")
    void testFindProjects() throws SQLException {
        ReportFilter filter = new ReportFilter();
        filter.setGroupId(1);
        filter.setStatus("ACTIVO");
        filter.setFromDate(LocalDate.of(2026, Month.JANUARY, 1));
        filter.setToDate(LocalDate.of(2026, Month.DECEMBER, 31));
        filter.setResearcherId(2);
        filter.setCallId(3);
        filter.setSize(10);
        filter.setPage(0);

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
            RowMapper<ProjectReport> mapper = invocation.getArgument(1);
            ProjectReport mapped = mapper.mapRow(rs, 0);
            return List.of(mapped);
        });

        List<ProjectReport> result = repository.findProjects(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        ProjectReport p = result.get(0);
        assertEquals(100, p.getProjectId());
        assertEquals("PRJ-100", p.getProjectCode());
        assertEquals("Proyecto de Prueba", p.getProjectTitle());
        assertEquals("ACTIVO", p.getProjectStatus());
        assertEquals("Grupo Alfa", p.getGroupName());
        assertEquals("Linea de Tecnologia", p.getLineName());
        assertEquals("Juan Perez", p.getResponsibleName());
        assertEquals("Convocatoria 2026", p.getCallTitle());
        assertEquals(new BigDecimal("15000.00"), p.getBudget());
        assertEquals(LocalDate.of(2026, Month.FEBRUARY, 1), p.getStartDate());
        assertEquals(LocalDate.of(2026, Month.NOVEMBER, 30), p.getEndDate());
        assertEquals(LocalDateTime.of(2026, Month.JANUARY, 15, 10, 0), p.getCreatedAt());
    }

    @Test
    @DisplayName("Should successfully count Projects with filters")
    void testCountProjects() {
        ReportFilter filter = new ReportFilter();
        filter.setGroupId(1);
        filter.setStatus("ACTIVO");

        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(42L);

        long count = repository.countProjects(filter);

        assertEquals(42L, count);
    }

    @Test
    @DisplayName("Should return 0 for countProjects when jdbc returns null")
    void testCountProjectsNull() {
        ReportFilter filter = new ReportFilter();
        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(null);

        long count = repository.countProjects(filter);

        assertEquals(0L, count);
    }

    @Test
    @DisplayName("Should successfully find and map Procedures with filters")
    @SuppressWarnings("unchecked")
    void testFindProcedures() throws SQLException {
        ReportFilter filter = new ReportFilter();
        filter.setGroupId(5);
        filter.setStatus("EN_REVISION");
        filter.setFromDate(LocalDate.of(2026, Month.MAY, 1));
        filter.setToDate(LocalDate.of(2026, Month.MAY, 31));
        filter.setResearcherId(10);
        filter.setProcedureType("TESIS");
        filter.setSize(5);
        filter.setPage(0);

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
            RowMapper<ProcedureReport> mapper = invocation.getArgument(1);
            ProcedureReport mapped = mapper.mapRow(rs, 0);
            return List.of(mapped);
        });

        List<ProcedureReport> result = repository.findProcedures(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        ProcedureReport t = result.get(0);
        assertEquals(200, t.getProcedureId());
        assertEquals("TRM-200", t.getProcedureCode());
        assertEquals("TESIS", t.getProcedureType());
        assertEquals("Ana Torres", t.getApplicantName());
        assertEquals("EN_REVISION", t.getCurrentStatus());
        assertEquals("DECANO", t.getCurrentReviewerRole());
        assertEquals("Grupo Beta", t.getGroupName());
        assertEquals(LocalDateTime.of(2026, Month.MAY, 10, 9, 0), t.getSubmittedAt());
        assertEquals(LocalDateTime.of(2026, Month.MAY, 12, 15, 30), t.getUpdatedAt());
    }

    @Test
    @DisplayName("Should successfully count Procedures with filters")
    void testCountProcedures() {
        ReportFilter filter = new ReportFilter();
        filter.setGroupId(5);
        filter.setStatus("EN_REVISION");

        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(15L);

        long count = repository.countProcedures(filter);

        assertEquals(15L, count);
    }

    @Test
    @DisplayName("Should successfully find and map Resolutions with filters")
    @SuppressWarnings("unchecked")
    void testFindResolutions() throws SQLException {
        ReportFilter filter = new ReportFilter();
        filter.setFromDate(LocalDate.of(2026, Month.MARCH, 1));
        filter.setToDate(LocalDate.of(2026, Month.MARCH, 31));
        filter.setResearcherId(8);
        filter.setProcedureType("PROYECTO");
        filter.setSize(20);
        filter.setPage(0);

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
            RowMapper<ResolutionReport> mapper = invocation.getArgument(1);
            ResolutionReport mapped = mapper.mapRow(rs, 0);
            return List.of(mapped);
        });

        List<ResolutionReport> result = repository.findResolutions(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        ResolutionReport r = result.get(0);
        assertEquals(300, r.getResolutionId());
        assertEquals("RES-045-2026", r.getResolutionNumber());
        assertEquals(LocalDate.of(2026, Month.MARCH, 15), r.getIssueDate());
        assertEquals("Aprobacion de Proyecto", r.getSubject());
        assertEquals("TRM-105", r.getProcedureCode());
        assertEquals("PROYECTO", r.getProcedureType());
        assertEquals("Carlos Ruiz", r.getApplicantName());
        assertEquals(LocalDateTime.of(2026, Month.MARCH, 16, 8, 30), r.getRegisteredAt());
    }

    @Test
    @DisplayName("Should successfully count Resolutions with filters")
    void testCountResolutions() {
        ReportFilter filter = new ReportFilter();
        filter.setResearcherId(8);

        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(8L);

        long count = repository.countResolutions(filter);

        assertEquals(8L, count);
    }

    @Test
    @DisplayName("Should successfully find and map Progress Reports with filters")
    @SuppressWarnings("unchecked")
    void testFindProgressReports() throws SQLException {
        ReportFilter filter = new ReportFilter();
        filter.setGroupId(2);
        filter.setStatus("APROBADO");
        filter.setFromDate(LocalDate.of(2026, Month.APRIL, 1));
        filter.setToDate(LocalDate.of(2026, Month.APRIL, 30));
        filter.setProcedureType("INFORME_FINAL");
        filter.setSize(10);
        filter.setPage(0);

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
            RowMapper<ProgressReport> mapper = invocation.getArgument(1);
            ProgressReport mapped = mapper.mapRow(rs, 0);
            return List.of(mapped);
        });

        List<ProgressReport> result = repository.findProgressReports(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        ProgressReport ia = result.get(0);
        assertEquals(400, ia.getReportId());
        assertEquals("PRJ-45", ia.getProjectCode());
        assertEquals("Estudio de Suelos", ia.getProjectTitle());
        assertEquals("INFORME_FINAL", ia.getReportType());
        assertEquals("2026-I", ia.getPeriod());
        assertEquals(new BigDecimal("100.00"), ia.getProgressPercentage());
        assertEquals("APROBADO", ia.getReportStatus());
        assertEquals("Grupo Geotecnia", ia.getGroupName());
        assertEquals(LocalDateTime.of(2026, Month.APRIL, 25, 11, 45), ia.getRegisteredAt());
    }

    @Test
    @DisplayName("Should successfully count Progress Reports with filters")
    void testCountProgressReports() {
        ReportFilter filter = new ReportFilter();
        filter.setGroupId(2);
        filter.setStatus("APROBADO");

        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(3L);

        long count = repository.countProgressReports(filter);

        assertEquals(3L, count);
    }
}
