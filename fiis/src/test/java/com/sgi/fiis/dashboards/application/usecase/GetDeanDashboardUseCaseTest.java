package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardDeanResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardDean;
import com.sgi.fiis.dashboards.domain.port.DashboardRepositoryPort;
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
@DisplayName("Get Dean Dashboard UseCase Unit Tests")
class GetDeanDashboardUseCaseTest {

    @Mock
    private DashboardRepositoryPort repository;

    @Mock
    private DashboardMapper mapper;

    @Test
    @DisplayName("Should execute dean dashboard use case")
    void execute_shouldReturnDeanDashboardResponse() {
        Integer userId = 6;

        DashboardDean model = DashboardDean.builder()
                .totalFacultyProjects(30)
                .activeProjects(15)
                .pendingSignatureProcedures(4)
                .build();

        DashboardDeanResponse response = DashboardDeanResponse.builder()
                .totalFacultyProjects(30)
                .activeProjects(15)
                .pendingSignatureProcedures(4)
                .build();

        when(repository.getDeanDashboard(userId)).thenReturn(model);
        when(mapper.toDeanResponse(model)).thenReturn(response);

        GetDeanDashboardUseCase useCase = new GetDeanDashboardUseCase(repository, mapper);

        DashboardDeanResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getDeanDashboard(userId);
        verify(mapper).toDeanResponse(model);
    }
}