package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardAdminResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardAdmin;
import com.sgi.fiis.dashboards.domain.port.DashboardRepository;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerDashboardAdminUseCase {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardAdminResponse ejecutar(Integer idUsuario) {
        DashboardAdmin modelo = dashboardRepository.obtenerDashboardAdmin(idUsuario);
        return dashboardMapper.toAdminResponse(modelo);
    }
}