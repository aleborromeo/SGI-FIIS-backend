package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListProceduresUseCase Unit Tests")
class ListProceduresUseCaseTest {

    @Mock
    private ProcedureRepositoryPort procedureRepositoryPort;

    @InjectMocks
    private ListProceduresUseCase useCase;

    private Procedure buildProcedure(Long id, ProcedureStatus status) {
        return Procedure.builder()
                .id(id)
                .code("TRAM-" + id)
                .currentStatus(status)
                .applicantId(1L)
                .build();
    }

    @Test
    @DisplayName("execute() - returns all procedures")
    void executeNoArgsReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.REGISTRADO),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DECANATO)
        );
        when(procedureRepositoryPort.findAll()).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute();

        assertEquals(2, result.size());
        verify(procedureRepositoryPort).findAll();
    }

    @Test
    @DisplayName("execute(null) - returns all procedures")
    void executeNullRoleReturnsAll() {
        List<Procedure> all = List.of(buildProcedure(1L, ProcedureStatus.REGISTRADO));
        when(procedureRepositoryPort.findAll()).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute((RoleEnum) null);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findAll();
    }

    @Test
    @DisplayName("execute(DECANO) - returns non-terminal procedures for reviewer role")
    void executeDecanoReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.REGISTRADO),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DECANATO),
                buildProcedure(3L, ProcedureStatus.FINALIZADO)
        );
        when(procedureRepositoryPort.findByReviewerRole(RoleEnum.DECANO)).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.DECANO);

        assertEquals(2, result.size());
        verify(procedureRepositoryPort).findByReviewerRole(RoleEnum.DECANO);
    }

    @Test
    @DisplayName("execute(COORDINADOR_GRUPO) - returns non-terminal procedures for reviewer role")
    void executeCoordinadorReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.PENDIENTE_COORDINADOR),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DIRECCION),
                buildProcedure(3L, ProcedureStatus.APROBADO_CON_RESOLUCION)
        );
        when(procedureRepositoryPort.findByReviewerRole(RoleEnum.COORDINADOR_GRUPO)).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.COORDINADOR_GRUPO);

        assertEquals(3, result.size());
        verify(procedureRepositoryPort).findByReviewerRole(RoleEnum.COORDINADOR_GRUPO);
    }

    @Test
    @DisplayName("execute(DIRECTOR_INVESTIGACION) - returns non-terminal procedures for reviewer role")
    void executeDirectorReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.REGISTRADO),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DIRECCION),
                buildProcedure(3L, ProcedureStatus.PENDIENTE_COORDINADOR)
        );
        when(procedureRepositoryPort.findByReviewerRole(RoleEnum.DIRECTOR_INVESTIGACION)).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.DIRECTOR_INVESTIGACION);

        assertEquals(3, result.size());
        verify(procedureRepositoryPort).findByReviewerRole(RoleEnum.DIRECTOR_INVESTIGACION);
    }

    @Test
    @DisplayName("execute(ESTUDIANTE) - returns non-terminal procedures for reviewer role")
    void executeEstudianteReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.REGISTRADO),
                buildProcedure(2L, ProcedureStatus.FINALIZADO)
        );
        when(procedureRepositoryPort.findByReviewerRole(RoleEnum.ESTUDIANTE)).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.ESTUDIANTE, 1L);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findByReviewerRole(RoleEnum.ESTUDIANTE);
    }

    @Test
    @DisplayName("execute(ADMIN) - returns non-terminal procedures for reviewer role")
    void executeAdminReturnsAll() {
        List<Procedure> all = List.of(buildProcedure(1L, ProcedureStatus.PENDIENTE_COORDINADOR));
        when(procedureRepositoryPort.findByReviewerRole(RoleEnum.ADMIN)).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.ADMIN);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findByReviewerRole(RoleEnum.ADMIN);
    }

    @Test
    @DisplayName("execute(COORDINADOR_GRUPO, userId) - returns non-terminal procedures for reviewer role")
    void executeCoordinadorWithUserIdReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.PENDIENTE_COORDINADOR),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DECANATO)
        );
        when(procedureRepositoryPort.findByReviewerRole(RoleEnum.COORDINADOR_GRUPO)).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.COORDINADOR_GRUPO, 10L);

        assertEquals(2, result.size());
        verify(procedureRepositoryPort).findByReviewerRole(RoleEnum.COORDINADOR_GRUPO);
    }
}
