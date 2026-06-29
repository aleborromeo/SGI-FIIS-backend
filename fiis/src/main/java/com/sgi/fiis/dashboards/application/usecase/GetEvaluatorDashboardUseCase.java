package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardEvaluatorResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardEvaluator;
import com.sgi.fiis.dashboards.domain.port.DashboardRepositoryPort;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetEvaluatorDashboardUseCase {

    private final DashboardRepositoryPort dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardEvaluatorResponse execute(Integer userId) {
        DashboardEvaluator model = dashboardRepository.getEvaluatorDashboard(userId);
        return dashboardMapper.toEvaluatorResponse(model);
    }
}
