package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardDocenteResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardDocente;
import com.sgi.fiis.dashboards.domain.port.DashboardRepository;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerDashboardDocenteUseCase {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardDocenteResponse ejecutar(Integer idUsuario) {
        DashboardDocente modelo = dashboardRepository.obtenerDashboardDocente(idUsuario);
        return dashboardMapper.toDocenteResponse(modelo);
    }
}