package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardStudentResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardStudent;
import com.sgi.fiis.dashboards.domain.port.DashboardRepositoryPort;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetStudentDashboardUseCase {

    private final DashboardRepositoryPort dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardStudentResponse execute(Integer userId) {
        DashboardStudent model = dashboardRepository.getStudentDashboard(userId);
        return dashboardMapper.toStudentResponse(model);
    }
}
