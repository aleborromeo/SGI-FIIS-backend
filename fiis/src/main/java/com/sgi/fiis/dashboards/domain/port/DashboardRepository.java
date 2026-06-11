package com.sgi.fiis.dashboards.domain.port;

import com.sgi.fiis.dashboards.domain.model.*;

public interface DashboardRepository {

    DashboardAdmin obtenerDashboardAdmin(Integer idUsuario);

    DashboardDirector obtenerDashboardDirector(Integer idUsuario);

    DashboardCoordinador obtenerDashboardCoordinador(Integer idUsuario);

    DashboardDocente obtenerDashboardDocente(Integer idUsuario);

    DashboardEvaluador obtenerDashboardEvaluador(Integer idUsuario);

    DashboardDecano obtenerDashboardDecano(Integer idUsuario);

    DashboardEstudiante obtenerDashboardEstudiante(Integer idUsuario);
}