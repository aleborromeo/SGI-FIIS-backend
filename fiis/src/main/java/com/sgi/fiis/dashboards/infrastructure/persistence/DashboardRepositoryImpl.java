package com.sgi.fiis.dashboards.infrastructure.persistence;

import com.sgi.fiis.dashboards.domain.model.*;
import com.sgi.fiis.dashboards.domain.port.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardRepositoryImpl implements DashboardRepository {

    private static final String TIPO_ALERTA = "ALERTA";
    private static final String TIPO_REVISION = "REVISION";
    private static final String TIPO_INFO = "INFO";

    private static final String TITULO_CONVOCATORIA_ACTIVA = "Convocatoria activa";

    private static final String SQL_COUNT_PROYECTOS = "SELECT COUNT(*) FROM proyectos";
    private static final String SQL_COUNT_PROYECTOS_EN_EJECUCION =
            "SELECT COUNT(*) FROM proyectos WHERE estado_proyecto = 'EN_EJECUCION'";
    private static final String SQL_COUNT_RESOLUCIONES = "SELECT COUNT(*) FROM resoluciones";
    private static final String SQL_COUNT_CONVOCATORIAS_ABIERTAS =
            "SELECT COUNT(*) FROM convocatorias WHERE estado = 'ABIERTA'";

    private static final String SQL_COUNT_TRAMITES_GRUPO =
            "SELECT COUNT(*) FROM tramites WHERE id_grupo = ";
    private static final String SQL_COUNT_PROYECTOS_RESPONSABLE =
            "SELECT COUNT(*) FROM proyectos WHERE id_responsable = ";
    private static final String SQL_COUNT_EVALUACIONES_EVALUADOR =
            "SELECT COUNT(*) FROM evaluaciones WHERE id_evaluador = ";

    private static final String FILTRO_ACTIVO = " AND es_activo = TRUE";
    private static final String FILTRO_TRAMITE_NO_CERRADO =
            " AND estado_actual NOT IN ('APROBADO','RECHAZADO')";

    private final JdbcTemplate jdbcTemplate;

    // =========================================================================
    // ADMIN (RF-88)
    // =========================================================================
    @Override
    public DashboardAdmin obtenerDashboardAdmin(Integer idUsuario) {

        int totalUsuarios = count("SELECT COUNT(*) FROM usuarios");
        int totalUsuariosActivos = count("SELECT COUNT(*) FROM usuarios WHERE es_activo = TRUE");
        int totalGrupos = count("SELECT COUNT(*) FROM grupos_investigacion");
        int totalGruposActivos = count("SELECT COUNT(*) FROM grupos_investigacion WHERE es_activo = TRUE");
        int totalProyectos = count(SQL_COUNT_PROYECTOS);
        int proyectosActivos = count(SQL_COUNT_PROYECTOS_EN_EJECUCION);
        int tramitesPendientes = count("SELECT COUNT(*) FROM tramites WHERE estado_actual NOT IN ('APROBADO','RECHAZADO','FINALIZADO')");
        int resolucionesEmitidas = count(SQL_COUNT_RESOLUCIONES);
        int tramitesEnRevision = count("SELECT COUNT(*) FROM tramites WHERE estado_actual = 'EN_REVISION'");
        int tramitesAprobados = count("SELECT COUNT(*) FROM tramites WHERE estado_actual = 'APROBADO'");
        int tramitesRechazados = count("SELECT COUNT(*) FROM tramites WHERE estado_actual = 'RECHAZADO'");

        List<DashboardAdmin.AlertaItem> alertas = new ArrayList<>();

        int proyectosObservados = count("SELECT COUNT(*) FROM proyectos WHERE estado_proyecto = 'OBSERVADO'");
        if (proyectosObservados > 0) {
            alertas.add(DashboardAdmin.AlertaItem.builder()
                    .tipo(TIPO_ALERTA)
                    .titulo("Proyectos observados")
                    .descripcion(proyectosObservados + " proyecto(s) requieren subsanación.")
                    .build());
        }

        if (tramitesPendientes > 0) {
            alertas.add(DashboardAdmin.AlertaItem.builder()
                    .tipo(TIPO_REVISION)
                    .titulo("Trámites pendientes de atención")
                    .descripcion(tramitesPendientes + " trámite(s) sin resolver.")
                    .build());
        }

        int convocatoriasAbiertas = count(SQL_COUNT_CONVOCATORIAS_ABIERTAS);
        if (convocatoriasAbiertas > 0) {
            alertas.add(DashboardAdmin.AlertaItem.builder()
                    .tipo(TIPO_INFO)
                    .titulo(TITULO_CONVOCATORIA_ACTIVA)
                    .descripcion(convocatoriasAbiertas + " convocatoria(s) abiertas actualmente.")
                    .build());
        }

        return DashboardAdmin.builder()
                .totalUsuarios(totalUsuarios)
                .totalUsuariosActivos(totalUsuariosActivos)
                .totalGrupos(totalGrupos)
                .totalGruposActivos(totalGruposActivos)
                .totalProyectos(totalProyectos)
                .proyectosActivos(proyectosActivos)
                .tramitesPendientes(tramitesPendientes)
                .resolucionesEmitidas(resolucionesEmitidas)
                .tramitesEnRevision(tramitesEnRevision)
                .tramitesAprobados(tramitesAprobados)
                .tramitesRechazados(tramitesRechazados)
                .alertas(alertas)
                .build();
    }

    // =========================================================================
    // DIRECTOR DE INVESTIGACIÓN (RF-89)
    // =========================================================================
    @Override
    public DashboardDirector obtenerDashboardDirector(Integer idUsuario) {

        int totalProyectos = count(SQL_COUNT_PROYECTOS);
        int proyectosActivos = count(SQL_COUNT_PROYECTOS_EN_EJECUCION);
        int proyectosPostulados = count("SELECT COUNT(*) FROM proyectos WHERE estado_proyecto = 'POSTULADO'");
        int proyectosObservados = count("SELECT COUNT(*) FROM proyectos WHERE estado_proyecto = 'OBSERVADO'");
        int tramitesPendientesRevision = count(
                "SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'DIRECTOR_INVESTIGACION'"
                        + FILTRO_TRAMITE_NO_CERRADO);
        int informesPorVencer = count(
                "SELECT COUNT(*) FROM informes_avance WHERE estado_informe = 'PENDIENTE'");
        int resolucionesEmitidas = count(SQL_COUNT_RESOLUCIONES);
        int convocatoriasAbiertas = count(SQL_COUNT_CONVOCATORIAS_ABIERTAS);

        int tramitesEnCoordinador = count("SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'COORDINADOR_GRUPO'");
        int tramitesEnDirector = count("SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'DIRECTOR_INVESTIGACION'");
        int tramitesEnDecano = count("SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'DECANO'");
        int tramitesFinalizados = count("SELECT COUNT(*) FROM tramites WHERE estado_actual IN ('APROBADO','RECHAZADO')");

        List<DashboardDirector.AlertaItem> alertas = new ArrayList<>();

        if (tramitesPendientesRevision > 0) {
            alertas.add(DashboardDirector.AlertaItem.builder()
                    .tipo(TIPO_REVISION)
                    .titulo("Trámites pendientes de revisión")
                    .descripcion(tramitesPendientesRevision + " trámite(s) esperan su revisión.")
                    .build());
        }
        if (informesPorVencer > 0) {
            alertas.add(DashboardDirector.AlertaItem.builder()
                    .tipo(TIPO_ALERTA)
                    .titulo("Informes de avance pendientes")
                    .descripcion(informesPorVencer + " informe(s) de avance sin aprobar.")
                    .build());
        }
        if (convocatoriasAbiertas > 0) {
            alertas.add(DashboardDirector.AlertaItem.builder()
                    .tipo(TIPO_INFO)
                    .titulo(TITULO_CONVOCATORIA_ACTIVA)
                    .descripcion("Fondo concursable FIIS disponible.")
                    .build());
        }

        return DashboardDirector.builder()
                .totalProyectos(totalProyectos)
                .proyectosActivos(proyectosActivos)
                .proyectosPostulados(proyectosPostulados)
                .proyectosObservados(proyectosObservados)
                .tramitesPendientesRevision(tramitesPendientesRevision)
                .informesPorVencer(informesPorVencer)
                .resolucionesEmitidas(resolucionesEmitidas)
                .convocatoriasAbiertas(convocatoriasAbiertas)
                .tramitesEnCoordinador(tramitesEnCoordinador)
                .tramitesEnDirector(tramitesEnDirector)
                .tramitesEnDecano(tramitesEnDecano)
                .tramitesFinalizados(tramitesFinalizados)
                .alertas(alertas)
                .build();
    }

    // =========================================================================
    // COORDINADOR DE GRUPO (RF-90, RF-91)
    // =========================================================================
    @Override
    public DashboardCoordinador obtenerDashboardCoordinador(Integer idUsuario) {

        String sqlGrupo = """
                SELECT g.id_grupo, g.nombre_grupo, g.codigo_grupo
                FROM grupos_investigacion g
                WHERE g.id_coordinador_actual = ?
                AND g.es_activo = TRUE
                LIMIT 1
                """;

        Integer idGrupo = null;
        String nombreGrupo = null;
        String codigoGrupo = null;

        List<java.util.Map<String, Object>> grupoResult = jdbcTemplate.queryForList(sqlGrupo, idUsuario);
        if (!grupoResult.isEmpty()) {
            java.util.Map<String, Object> row = grupoResult.get(0);
            idGrupo = (Integer) row.get("id_grupo");
            nombreGrupo = (String) row.get("nombre_grupo");
            codigoGrupo = (String) row.get("codigo_grupo");
        }

        if (idGrupo == null) {
            return DashboardCoordinador.builder()
                    .nombreGrupo("Sin grupo asignado")
                    .codigoGrupo("")
                    .alertas(List.of(DashboardCoordinador.AlertaItem.builder()
                            .tipo(TIPO_ALERTA)
                            .titulo("Sin grupo asignado")
                            .descripcion("No se encontró un grupo activo coordinado por este usuario.")
                            .build()))
                    .build();
        }

        int totalMiembros = count("SELECT COUNT(*) FROM membresias_grupo WHERE id_grupo = " + idGrupo);
        int miembrosActivos = count("SELECT COUNT(*) FROM membresias_grupo WHERE id_grupo = " + idGrupo + FILTRO_ACTIVO);
        int totalProyectosGrupo = count("SELECT COUNT(*) FROM proyectos WHERE id_grupo = " + idGrupo);
        int proyectosActivosGrupo = count(
                "SELECT COUNT(*) FROM proyectos WHERE id_grupo = " + idGrupo + " AND estado_proyecto = 'EN_EJECUCION'");
        int tramitesPendientesGrupo = count(SQL_COUNT_TRAMITES_GRUPO + idGrupo + FILTRO_TRAMITE_NO_CERRADO);
        int informesAvanceGrupo = count(
                "SELECT COUNT(*) FROM informes_avance ia INNER JOIN proyectos p ON ia.id_proyecto = p.id_proyecto WHERE p.id_grupo = "
                        + idGrupo);
        int planesTesisGrupo = count("SELECT COUNT(*) FROM planes_tesis WHERE id_grupo = " + idGrupo);

        int tramitesPostulados = count(SQL_COUNT_TRAMITES_GRUPO + idGrupo + " AND estado_actual = 'POSTULADO'");
        int tramitesEnRevision = count(SQL_COUNT_TRAMITES_GRUPO + idGrupo + " AND estado_actual = 'EN_REVISION'");
        int tramitesAprobados = count(SQL_COUNT_TRAMITES_GRUPO + idGrupo + " AND estado_actual = 'APROBADO'");
        int tramitesObservados = count(SQL_COUNT_TRAMITES_GRUPO + idGrupo + " AND estado_actual = 'OBSERVADO'");

        List<DashboardCoordinador.AlertaItem> alertas = new ArrayList<>();

        if (tramitesPendientesGrupo > 0) {
            alertas.add(DashboardCoordinador.AlertaItem.builder()
                    .tipo(TIPO_REVISION)
                    .titulo("Trámites pendientes en tu grupo")
                    .descripcion(tramitesPendientesGrupo + " trámite(s) de tu grupo sin resolver.")
                    .build());
        }
        if (tramitesObservados > 0) {
            alertas.add(DashboardCoordinador.AlertaItem.builder()
                    .tipo(TIPO_ALERTA)
                    .titulo("Trámites observados")
                    .descripcion(tramitesObservados + " trámite(s) requieren correcciones.")
                    .build());
        }

        return DashboardCoordinador.builder()
                .idGrupo(idGrupo)
                .nombreGrupo(nombreGrupo)
                .codigoGrupo(codigoGrupo)
                .totalMiembros(totalMiembros)
                .miembrosActivos(miembrosActivos)
                .totalProyectosGrupo(totalProyectosGrupo)
                .proyectosActivosGrupo(proyectosActivosGrupo)
                .tramitesPendientesGrupo(tramitesPendientesGrupo)
                .informesAvanceGrupo(informesAvanceGrupo)
                .planesTesisGrupo(planesTesisGrupo)
                .tramitesPostulados(tramitesPostulados)
                .tramitesEnRevision(tramitesEnRevision)
                .tramitesAprobados(tramitesAprobados)
                .tramitesObservados(tramitesObservados)
                .alertas(alertas)
                .build();
    }

    // =========================================================================
    // DOCENTE INVESTIGADOR (RF-92)
    // =========================================================================
    @Override
    public DashboardDocente obtenerDashboardDocente(Integer idUsuario) {

        int proyectosComoResponsable = count(SQL_COUNT_PROYECTOS_RESPONSABLE + idUsuario);
        int proyectosComoIntegrante = count(
                "SELECT COUNT(*) FROM integrantes_proyecto WHERE id_usuario = " + idUsuario);
        int tramitesPendientes = count(
                "SELECT COUNT(*) FROM tramites WHERE id_solicitante = " + idUsuario + FILTRO_TRAMITE_NO_CERRADO);
        int informesAvancePendientes = count(
                "SELECT COUNT(*) FROM informes_avance ia INNER JOIN proyectos p ON ia.id_proyecto = p.id_proyecto WHERE p.id_responsable = "
                        + idUsuario + " AND ia.estado_informe = 'PENDIENTE'");
        int documentosCargados = count(
                "SELECT COUNT(*) FROM documentos WHERE id_usuario_subio = " + idUsuario + FILTRO_ACTIVO);
        int resolucionesRecibidas = count(
                "SELECT COUNT(*) FROM resoluciones r INNER JOIN tramites t ON r.id_tramite = t.id_tramite WHERE t.id_solicitante = "
                        + idUsuario);

        int proyectosPostulados = count(SQL_COUNT_PROYECTOS_RESPONSABLE + idUsuario + " AND estado_proyecto = 'POSTULADO'");
        int proyectosAprobados = count(SQL_COUNT_PROYECTOS_RESPONSABLE + idUsuario + " AND estado_proyecto = 'APROBADO'");
        int proyectosEnEjecucion = count(SQL_COUNT_PROYECTOS_RESPONSABLE + idUsuario + " AND estado_proyecto = 'EN_EJECUCION'");
        int proyectosFinalizados = count(SQL_COUNT_PROYECTOS_RESPONSABLE + idUsuario + " AND estado_proyecto = 'FINALIZADO'");

        List<DashboardDocente.AlertaItem> alertas = new ArrayList<>();

        if (tramitesPendientes > 0) {
            alertas.add(DashboardDocente.AlertaItem.builder()
                    .tipo(TIPO_REVISION)
                    .titulo("Trámites en proceso")
                    .descripcion(tramitesPendientes + " trámite(s) tuyos aún en revisión.")
                    .build());
        }
        if (informesAvancePendientes > 0) {
            alertas.add(DashboardDocente.AlertaItem.builder()
                    .tipo(TIPO_ALERTA)
                    .titulo("Informes de avance pendientes")
                    .descripcion(informesAvancePendientes + " informe(s) de avance sin aprobar.")
                    .build());
        }

        int convocatoriasAbiertas = count(SQL_COUNT_CONVOCATORIAS_ABIERTAS);
        if (convocatoriasAbiertas > 0) {
            alertas.add(DashboardDocente.AlertaItem.builder()
                    .tipo(TIPO_INFO)
                    .titulo(TITULO_CONVOCATORIA_ACTIVA)
                    .descripcion("Hay " + convocatoriasAbiertas + " convocatoria(s) abiertas.")
                    .build());
        }

        return DashboardDocente.builder()
                .proyectosComoResponsable(proyectosComoResponsable)
                .proyectosComoIntegrante(proyectosComoIntegrante)
                .tramitesPendientes(tramitesPendientes)
                .informesAvancePendientes(informesAvancePendientes)
                .documentosCargados(documentosCargados)
                .resolucionesRecibidas(resolucionesRecibidas)
                .proyectosPostulados(proyectosPostulados)
                .proyectosAprobados(proyectosAprobados)
                .proyectosEnEjecucion(proyectosEnEjecucion)
                .proyectosFinalizados(proyectosFinalizados)
                .alertas(alertas)
                .build();
    }

    // =========================================================================
    // EVALUADOR (RF-93)
    // =========================================================================
    @Override
    public DashboardEvaluador obtenerDashboardEvaluador(Integer idUsuario) {

        int evaluacionesAsignadas = count(SQL_COUNT_EVALUACIONES_EVALUADOR + idUsuario);
        int evaluacionesPendientes = count(SQL_COUNT_EVALUACIONES_EVALUADOR + idUsuario + " AND fecha_evaluacion IS NULL");
        int evaluacionesCompletadas = count(SQL_COUNT_EVALUACIONES_EVALUADOR + idUsuario + " AND fecha_evaluacion IS NOT NULL");
        int proyectosAsignados = count(SQL_COUNT_EVALUACIONES_EVALUADOR + idUsuario + " AND id_proyecto IS NOT NULL");
        int planesTesisAsignados = count(SQL_COUNT_EVALUACIONES_EVALUADOR + idUsuario + " AND id_plan_tesis IS NOT NULL");

        int evaluacionesAprobadas = count(SQL_COUNT_EVALUACIONES_EVALUADOR + idUsuario + " AND resultado = 'APROBADO'");
        int evaluacionesRechazadas = count(SQL_COUNT_EVALUACIONES_EVALUADOR + idUsuario + " AND resultado = 'RECHAZADO'");
        int evaluacionesConObservaciones = count(SQL_COUNT_EVALUACIONES_EVALUADOR + idUsuario + " AND resultado = 'CON_OBSERVACIONES'");

        List<DashboardEvaluador.AlertaItem> alertas = new ArrayList<>();

        if (evaluacionesPendientes > 0) {
            alertas.add(DashboardEvaluador.AlertaItem.builder()
                    .tipo(TIPO_REVISION)
                    .titulo("Evaluaciones pendientes")
                    .descripcion(evaluacionesPendientes + " evaluación(es) asignadas sin completar.")
                    .build());
        }

        return DashboardEvaluador.builder()
                .evaluacionesAsignadas(evaluacionesAsignadas)
                .evaluacionesPendientes(evaluacionesPendientes)
                .evaluacionesCompletadas(evaluacionesCompletadas)
                .proyectosAsignados(proyectosAsignados)
                .planesTesisAsignados(planesTesisAsignados)
                .evaluacionesAprobadas(evaluacionesAprobadas)
                .evaluacionesRechazadas(evaluacionesRechazadas)
                .evaluacionesConObservaciones(evaluacionesConObservaciones)
                .alertas(alertas)
                .build();
    }

    // =========================================================================
    // DECANO
    // =========================================================================
    @Override
    public DashboardDecano obtenerDashboardDecano(Integer idUsuario) {

        int totalProyectosFacultad = count(SQL_COUNT_PROYECTOS);
        int proyectosActivos = count(SQL_COUNT_PROYECTOS_EN_EJECUCION);
        int tramitesPendientesFirma = count(
                "SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'DECANO'" + FILTRO_TRAMITE_NO_CERRADO);
        int resolucionesEmitidas = count(SQL_COUNT_RESOLUCIONES);
        int convocatoriasActivas = count(SQL_COUNT_CONVOCATORIAS_ABIERTAS);
        int totalGruposActivos = count("SELECT COUNT(*) FROM grupos_investigacion WHERE es_activo = TRUE");

        int tramitesEnEspera = tramitesPendientesFirma;

        int tramitesAprobadosMes = count(
                "SELECT COUNT(*) FROM tramites WHERE estado_actual = 'APROBADO' AND DATE_TRUNC('month', fecha_actualizacion) = DATE_TRUNC('month', CURRENT_DATE)");
        int tramitesRechazadosMes = count(
                "SELECT COUNT(*) FROM tramites WHERE estado_actual = 'RECHAZADO' AND DATE_TRUNC('month', fecha_actualizacion) = DATE_TRUNC('month', CURRENT_DATE)");

        List<DashboardDecano.AlertaItem> alertas = new ArrayList<>();

        if (tramitesPendientesFirma > 0) {
            alertas.add(DashboardDecano.AlertaItem.builder()
                    .tipo(TIPO_REVISION)
                    .titulo("Trámites pendientes de firma")
                    .descripcion(tramitesPendientesFirma + " trámite(s) esperan aprobación del Decano.")
                    .build());
        }
        if (convocatoriasActivas > 0) {
            alertas.add(DashboardDecano.AlertaItem.builder()
                    .tipo(TIPO_INFO)
                    .titulo(TITULO_CONVOCATORIA_ACTIVA)
                    .descripcion("Hay " + convocatoriasActivas + " convocatoria(s) abiertas en la facultad.")
                    .build());
        }

        return DashboardDecano.builder()
                .totalProyectosFacultad(totalProyectosFacultad)
                .proyectosActivos(proyectosActivos)
                .tramitesPendientesFirma(tramitesPendientesFirma)
                .resolucionesEmitidas(resolucionesEmitidas)
                .convocatoriasActivas(convocatoriasActivas)
                .totalGruposActivos(totalGruposActivos)
                .tramitesEnEspera(tramitesEnEspera)
                .tramitesAprobadosMes(tramitesAprobadosMes)
                .tramitesRechazadosMes(tramitesRechazadosMes)
                .alertas(alertas)
                .build();
    }

    // =========================================================================
    // ESTUDIANTE / TESISTA
    // =========================================================================
    @Override
    public DashboardEstudiante obtenerDashboardEstudiante(Integer idUsuario) {

        int planesTesisPresentados = count("SELECT COUNT(*) FROM planes_tesis WHERE id_estudiante = " + idUsuario);
        int tramitesPendientes = count(
                "SELECT COUNT(*) FROM tramites WHERE id_solicitante = " + idUsuario + FILTRO_TRAMITE_NO_CERRADO);
        int documentosCargados = count(
                "SELECT COUNT(*) FROM documentos WHERE id_usuario_subio = " + idUsuario + FILTRO_ACTIVO);
        int convocatoriasAbiertas = count(SQL_COUNT_CONVOCATORIAS_ABIERTAS);

        String estadoPlan = null;
        List<java.util.Map<String, Object>> planResult = jdbcTemplate.queryForList(
                "SELECT estado_plan FROM planes_tesis WHERE id_estudiante = ? ORDER BY fecha_creacion DESC LIMIT 1",
                idUsuario);
        if (!planResult.isEmpty()) {
            estadoPlan = (String) planResult.get(0).get("estado_plan");
        }

        String nombreGrupo = null;
        String codigoGrupo = null;
        List<java.util.Map<String, Object>> grupoResult = jdbcTemplate.queryForList(
                """
                SELECT g.nombre_grupo, g.codigo_grupo
                FROM membresias_grupo m
                INNER JOIN grupos_investigacion g ON m.id_grupo = g.id_grupo
                WHERE m.id_usuario = ? AND m.es_activo = TRUE
                LIMIT 1
                """, idUsuario);
        if (!grupoResult.isEmpty()) {
            nombreGrupo = (String) grupoResult.get(0).get("nombre_grupo");
            codigoGrupo = (String) grupoResult.get(0).get("codigo_grupo");
        }

        List<DashboardEstudiante.AlertaItem> alertas = new ArrayList<>();

        if (tramitesPendientes > 0) {
            alertas.add(DashboardEstudiante.AlertaItem.builder()
                    .tipo(TIPO_REVISION)
                    .titulo("Trámites en proceso")
                    .descripcion(tramitesPendientes + " trámite(s) tuyos en revisión.")
                    .build());
        }
        if ("OBSERVADO".equals(estadoPlan)) {
            alertas.add(DashboardEstudiante.AlertaItem.builder()
                    .tipo(TIPO_ALERTA)
                    .titulo("Plan de tesis observado")
                    .descripcion("Tu plan de tesis tiene observaciones que debes subsanar.")
                    .build());
        }
        if (convocatoriasAbiertas > 0) {
            alertas.add(DashboardEstudiante.AlertaItem.builder()
                    .tipo(TIPO_INFO)
                    .titulo(TITULO_CONVOCATORIA_ACTIVA)
                    .descripcion("Hay " + convocatoriasAbiertas + " convocatoria(s) abiertas.")
                    .build());
        }

        return DashboardEstudiante.builder()
                .planesTesisPresentados(planesTesisPresentados)
                .estadoPlanActual(estadoPlan != null ? estadoPlan : "SIN_PLAN")
                .tramitesPendientes(tramitesPendientes)
                .documentosCargados(documentosCargados)
                .convocatoriasAbiertas(convocatoriasAbiertas)
                .nombreGrupo(nombreGrupo != null ? nombreGrupo : "Sin grupo")
                .codigoGrupo(codigoGrupo != null ? codigoGrupo : "")
                .alertas(alertas)
                .build();
    }

    // =========================================================================
    // Helper: ejecutar COUNT(*) de forma segura
    // =========================================================================
    private int count(String sql) {
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        return result != null ? result : 0;
    }
}