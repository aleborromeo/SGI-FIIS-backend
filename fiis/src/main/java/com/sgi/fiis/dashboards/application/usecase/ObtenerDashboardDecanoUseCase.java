package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardDecanoResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardDecano;
import com.sgi.fiis.dashboards.domain.port.DashboardRepository;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerDashboardDecanoUseCase {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardDecanoResponse ejecutar(Integer idUsuario) {
        DashboardDecano modelo = dashboardRepository.obtenerDashboardDecano(idUsuario);
        return dashboardMapper.toDecanoResponse(modelo);
    }
}