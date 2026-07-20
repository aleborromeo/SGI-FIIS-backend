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
    @DisplayName("execute(DECANO) - returns all procedures")
    void executeDecanoReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.REGISTRADO),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DECANATO),
                buildProcedure(3L, ProcedureStatus.FINALIZADO)
        );
        when(procedureRepositoryPort.findAll()).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.DECANO);

        assertEquals(3, result.size());
        verify(procedureRepositoryPort).findAll();
    }

    @Test
    @DisplayName("execute(COORDINADOR_GRUPO) - returns all procedures")
    void executeCoordinadorReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.PENDIENTE_COORDINADOR),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DIRECCION),
                buildProcedure(3L, ProcedureStatus.APROBADO_CON_RESOLUCION)
        );
        when(procedureRepositoryPort.findAll()).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.COORDINADOR_GRUPO);

        assertEquals(3, result.size());
        verify(procedureRepositoryPort).findAll();
    }

    @Test
    @DisplayName("execute(DIRECTOR_INVESTIGACION) - returns all procedures")
    void executeDirectorReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.REGISTRADO),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DIRECCION),
                buildProcedure(3L, ProcedureStatus.PENDIENTE_COORDINADOR)
        );
        when(procedureRepositoryPort.findAll()).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.DIRECTOR_INVESTIGACION);

        assertEquals(3, result.size());
        verify(procedureRepositoryPort).findAll();
    }

    @Test
    @DisplayName("execute(ESTUDIANTE) - returns all procedures")
    void executeEstudianteReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.REGISTRADO),
                buildProcedure(2L, ProcedureStatus.FINALIZADO)
        );
        when(procedureRepositoryPort.findAll()).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.ESTUDIANTE, 1L);

        assertEquals(2, result.size());
        verify(procedureRepositoryPort).findAll();
    }

    @Test
    @DisplayName("execute(ADMIN) - returns all procedures")
    void executeAdminReturnsAll() {
        List<Procedure> all = List.of(buildProcedure(1L, ProcedureStatus.FINALIZADO));
        when(procedureRepositoryPort.findAll()).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.ADMIN);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findAll();
    }

    @Test
    @DisplayName("execute(COORDINADOR_GRUPO, userId) - returns all procedures")
    void executeCoordinadorWithUserIdReturnsAll() {
        List<Procedure> all = List.of(
                buildProcedure(1L, ProcedureStatus.PENDIENTE_COORDINADOR),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DECANATO)
        );
        when(procedureRepositoryPort.findAll()).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.COORDINADOR_GRUPO, 10L);

        assertEquals(2, result.size());
        verify(procedureRepositoryPort).findAll();
    }
}
