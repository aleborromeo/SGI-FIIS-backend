package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardCoordinadorResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardCoordinador;
import com.sgi.fiis.dashboards.domain.port.DashboardRepository;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerDashboardCoordinadorUseCase {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardCoordinadorResponse ejecutar(Integer idUsuario) {
        DashboardCoordinador modelo = dashboardRepository.obtenerDashboardCoordinador(idUsuario);
        return dashboardMapper.toCoordinadorResponse(modelo);
    }
}