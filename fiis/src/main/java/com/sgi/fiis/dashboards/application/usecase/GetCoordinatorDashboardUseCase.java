package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardCoordinatorResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardCoordinator;
import com.sgi.fiis.dashboards.domain.port.DashboardRepositoryPort;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCoordinatorDashboardUseCase {

    private final DashboardRepositoryPort dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardCoordinatorResponse execute(Integer userId) {
        DashboardCoordinator model = dashboardRepository.getCoordinatorDashboard(userId);
        return dashboardMapper.toCoordinatorResponse(model);
    }
}
