package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.*;
import com.sgi.fiis.dashboards.domain.model.*;
import com.sgi.fiis.dashboards.domain.port.DashboardRepository;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Obtener Dashboard UseCase Unit Tests")
class ObtenerDashboardUseCaseTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private DashboardMapper dashboardMapper;

    @Test
    @DisplayName("Debe ejecutar caso de uso de dashboard admin")
    void ejecutarAdmin_debeRetornarResponse() {
        Integer idUsuario = 1;
        DashboardAdmin modelo = DashboardAdmin.builder().totalUsuarios(10).build();
        DashboardAdminResponse response = DashboardAdminResponse.builder().totalUsuarios(10).build();

        when(dashboardRepository.obtenerDashboardAdmin(idUsuario)).thenReturn(modelo);
        when(dashboardMapper.toAdminResponse(modelo)).thenReturn(response);

        ObtenerDashboardAdminUseCase useCase =
                new ObtenerDashboardAdminUseCase(dashboardRepository, dashboardMapper);

        DashboardAdminResponse result = useCase.ejecutar(idUsuario);

        assertSame(response, result);
        verify(dashboardRepository).obtenerDashboardAdmin(idUsuario);
        verify(dashboardMapper).toAdminResponse(modelo);
    }

    @Test
    @DisplayName("Debe ejecutar caso de uso de dashboard director")
    void ejecutarDirector_debeRetornarResponse() {
        Integer idUsuario = 2;
        DashboardDirector modelo = DashboardDirector.builder().totalProyectos(20).build();
        DashboardDirectorResponse response = DashboardDirectorResponse.builder().totalProyectos(20).build();

        when(dashboardRepository.obtenerDashboardDirector(idUsuario)).thenReturn(modelo);
        when(dashboardMapper.toDirectorResponse(modelo)).thenReturn(response);

        ObtenerDashboardDirectorUseCase useCase =
                new ObtenerDashboardDirectorUseCase(dashboardRepository, dashboardMapper);

        DashboardDirectorResponse result = useCase.ejecutar(idUsuario);

        assertSame(response, result);
        verify(dashboardRepository).obtenerDashboardDirector(idUsuario);
        verify(dashboardMapper).toDirectorResponse(modelo);
    }

    @Test
    @DisplayName("Debe ejecutar caso de uso de dashboard coordinador")
    void ejecutarCoordinador_debeRetornarResponse() {
        Integer idUsuario = 3;
        DashboardCoordinador modelo = DashboardCoordinador.builder().idGrupo(5).build();
        DashboardCoordinadorResponse response = DashboardCoordinadorResponse.builder().idGrupo(5).build();

        when(dashboardRepository.obtenerDashboardCoordinador(idUsuario)).thenReturn(modelo);
        when(dashboardMapper.toCoordinadorResponse(modelo)).thenReturn(response);

        ObtenerDashboardCoordinadorUseCase useCase =
                new ObtenerDashboardCoordinadorUseCase(dashboardRepository, dashboardMapper);

        DashboardCoordinadorResponse result = useCase.ejecutar(idUsuario);

        assertSame(response, result);
        verify(dashboardRepository).obtenerDashboardCoordinador(idUsuario);
        verify(dashboardMapper).toCoordinadorResponse(modelo);
    }

    @Test
    @DisplayName("Debe ejecutar caso de uso de dashboard docente")
    void ejecutarDocente_debeRetornarResponse() {
        Integer idUsuario = 4;
        DashboardDocente modelo = DashboardDocente.builder().proyectosComoResponsable(2).build();
        DashboardDocenteResponse response = DashboardDocenteResponse.builder().proyectosComoResponsable(2).build();

        when(dashboardRepository.obtenerDashboardDocente(idUsuario)).thenReturn(modelo);
        when(dashboardMapper.toDocenteResponse(modelo)).thenReturn(response);

        ObtenerDashboardDocenteUseCase useCase =
                new ObtenerDashboardDocenteUseCase(dashboardRepository, dashboardMapper);

        DashboardDocenteResponse result = useCase.ejecutar(idUsuario);

        assertSame(response, result);
        verify(dashboardRepository).obtenerDashboardDocente(idUsuario);
        verify(dashboardMapper).toDocenteResponse(modelo);
    }

    @Test
    @DisplayName("Debe ejecutar caso de uso de dashboard evaluador")
    void ejecutarEvaluador_debeRetornarResponse() {
        Integer idUsuario = 5;
        DashboardEvaluador modelo = DashboardEvaluador.builder().evaluacionesAsignadas(8).build();
        DashboardEvaluadorResponse response = DashboardEvaluadorResponse.builder().evaluacionesAsignadas(8).build();

        when(dashboardRepository.obtenerDashboardEvaluador(idUsuario)).thenReturn(modelo);
        when(dashboardMapper.toEvaluadorResponse(modelo)).thenReturn(response);

        ObtenerDashboardEvaluadorUseCase useCase =
                new ObtenerDashboardEvaluadorUseCase(dashboardRepository, dashboardMapper);

        DashboardEvaluadorResponse result = useCase.ejecutar(idUsuario);

        assertSame(response, result);
        verify(dashboardRepository).obtenerDashboardEvaluador(idUsuario);
        verify(dashboardMapper).toEvaluadorResponse(modelo);
    }

    @Test
    @DisplayName("Debe ejecutar caso de uso de dashboard decano")
    void ejecutarDecano_debeRetornarResponse() {
        Integer idUsuario = 6;
        DashboardDecano modelo = DashboardDecano.builder().totalProyectosFacultad(15).build();
        DashboardDecanoResponse response = DashboardDecanoResponse.builder().totalProyectosFacultad(15).build();

        when(dashboardRepository.obtenerDashboardDecano(idUsuario)).thenReturn(modelo);
        when(dashboardMapper.toDecanoResponse(modelo)).thenReturn(response);

        ObtenerDashboardDecanoUseCase useCase =
                new ObtenerDashboardDecanoUseCase(dashboardRepository, dashboardMapper);

        DashboardDecanoResponse result = useCase.ejecutar(idUsuario);

        assertSame(response, result);
        verify(dashboardRepository).obtenerDashboardDecano(idUsuario);
        verify(dashboardMapper).toDecanoResponse(modelo);
    }

    @Test
    @DisplayName("Debe ejecutar caso de uso de dashboard estudiante")
    void ejecutarEstudiante_debeRetornarResponse() {
        Integer idUsuario = 7;
        DashboardEstudiante modelo = DashboardEstudiante.builder().planesTesisPresentados(1).build();
        DashboardEstudianteResponse response = DashboardEstudianteResponse.builder().planesTesisPresentados(1).build();

        when(dashboardRepository.obtenerDashboardEstudiante(idUsuario)).thenReturn(modelo);
        when(dashboardMapper.toEstudianteResponse(modelo)).thenReturn(response);

        ObtenerDashboardEstudianteUseCase useCase =
                new ObtenerDashboardEstudianteUseCase(dashboardRepository, dashboardMapper);

        DashboardEstudianteResponse result = useCase.ejecutar(idUsuario);

        assertSame(response, result);
        verify(dashboardRepository).obtenerDashboardEstudiante(idUsuario);
        verify(dashboardMapper).toEstudianteResponse(modelo);
    }
}