package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardAdminResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardAdmin;
import com.sgi.fiis.dashboards.domain.port.DashboardRepositoryPort;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAdminDashboardUseCase {

    private final DashboardRepositoryPort dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardAdminResponse execute(Integer userId) {
        DashboardAdmin model = dashboardRepository.getAdminDashboard(userId);
        return dashboardMapper.toAdminResponse(model);
    }
}
