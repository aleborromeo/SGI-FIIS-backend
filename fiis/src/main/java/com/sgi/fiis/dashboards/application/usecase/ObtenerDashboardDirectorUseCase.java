package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardDirectorResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardDirector;
import com.sgi.fiis.dashboards.domain.port.DashboardRepository;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerDashboardDirectorUseCase {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardDirectorResponse ejecutar(Integer idUsuario) {
        DashboardDirector modelo = dashboardRepository.obtenerDashboardDirector(idUsuario);
        return dashboardMapper.toDirectorResponse(modelo);
    }
}