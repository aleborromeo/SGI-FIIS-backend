package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardDirectorResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardDirector;
import com.sgi.fiis.dashboards.domain.port.DashboardRepositoryPort;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetDirectorDashboardUseCase {

    private final DashboardRepositoryPort dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardDirectorResponse execute(Integer userId) {
        DashboardDirector model = dashboardRepository.getDirectorDashboard(userId);
        return dashboardMapper.toDirectorResponse(model);
    }
}
