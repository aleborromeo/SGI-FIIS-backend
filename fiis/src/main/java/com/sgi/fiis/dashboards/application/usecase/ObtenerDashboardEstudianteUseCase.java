package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardEstudianteResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardEstudiante;
import com.sgi.fiis.dashboards.domain.port.DashboardRepository;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerDashboardEstudianteUseCase {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardEstudianteResponse ejecutar(Integer idUsuario) {
        DashboardEstudiante modelo = dashboardRepository.obtenerDashboardEstudiante(idUsuario);
        return dashboardMapper.toEstudianteResponse(modelo);
    }
}