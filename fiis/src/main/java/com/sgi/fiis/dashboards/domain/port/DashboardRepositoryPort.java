package com.sgi.fiis.dashboards.domain.port;

import com.sgi.fiis.dashboards.domain.model.*;

public interface DashboardRepositoryPort {

    DashboardAdmin getAdminDashboard(Integer userId);

    DashboardDirector getDirectorDashboard(Integer userId);

    DashboardCoordinator getCoordinatorDashboard(Integer userId);

    DashboardTeacher getTeacherDashboard(Integer userId);

    DashboardEvaluator getEvaluatorDashboard(Integer userId);

    DashboardDean getDeanDashboard(Integer userId);

    DashboardStudent getStudentDashboard(Integer userId);
}
