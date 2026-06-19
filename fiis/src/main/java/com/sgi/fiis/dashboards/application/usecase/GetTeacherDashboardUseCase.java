package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardTeacherResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardTeacher;
import com.sgi.fiis.dashboards.domain.port.DashboardRepositoryPort;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTeacherDashboardUseCase {

    private final DashboardRepositoryPort dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardTeacherResponse execute(Integer userId) {
        DashboardTeacher model = dashboardRepository.getTeacherDashboard(userId);
        return dashboardMapper.toTeacherResponse(model);
    }
}
