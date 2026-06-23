package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardEvaluadorResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardEvaluador;
import com.sgi.fiis.dashboards.domain.port.DashboardRepository;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerDashboardEvaluadorUseCase {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardEvaluadorResponse ejecutar(Integer idUsuario) {
        DashboardEvaluador modelo = dashboardRepository.obtenerDashboardEvaluador(idUsuario);
        return dashboardMapper.toEvaluadorResponse(modelo);
    }
}