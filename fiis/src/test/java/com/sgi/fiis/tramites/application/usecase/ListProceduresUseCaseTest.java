package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupJpaRepository;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private ResearchGroupJpaRepository groupRepository;

    private ListProceduresUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListProceduresUseCase(procedureRepositoryPort, groupRepository);
    }

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
    @DisplayName("execute(DECANO) - returns only PENDIENTE_DECANATO procedures")
    void executeDecanoFiltersByDecanato() {
        List<Procedure> decanoList = List.of(
                buildProcedure(1L, ProcedureStatus.PENDIENTE_DECANATO),
                buildProcedure(2L, ProcedureStatus.PENDIENTE_DECANATO)
        );
        when(procedureRepositoryPort.findByStatus(ProcedureStatus.PENDIENTE_DECANATO)).thenReturn(decanoList);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.DECANO);

        assertEquals(2, result.size());
        verify(procedureRepositoryPort).findByStatus(ProcedureStatus.PENDIENTE_DECANATO);
        verifyNoMoreInteractions(procedureRepositoryPort);
    }

    @Test
    @DisplayName("execute(COORDINADOR_GRUPO) - returns only PENDIENTE_COORDINADOR procedures")
    void executeCoordinadorFiltersByCoordinador() {
        List<Procedure> coordList = List.of(buildProcedure(3L, ProcedureStatus.PENDIENTE_COORDINADOR));
        when(procedureRepositoryPort.findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR)).thenReturn(coordList);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.COORDINADOR_GRUPO);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR);
    }

    @Test
    @DisplayName("execute(DIRECTOR_INVESTIGACION) - returns only PENDIENTE_DIRECCION procedures")
    void executeDirectorFiltersByDireccion() {
        List<Procedure> dirList = List.of(
                buildProcedure(4L, ProcedureStatus.PENDIENTE_DIRECCION),
                buildProcedure(5L, ProcedureStatus.PENDIENTE_DIRECCION),
                buildProcedure(6L, ProcedureStatus.PENDIENTE_DIRECCION)
        );
        when(procedureRepositoryPort.findByStatus(ProcedureStatus.PENDIENTE_DIRECCION)).thenReturn(dirList);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.DIRECTOR_INVESTIGACION);

        assertEquals(3, result.size());
        verify(procedureRepositoryPort).findByStatus(ProcedureStatus.PENDIENTE_DIRECCION);
    }

    @Test
    @DisplayName("execute(ESTUDIANTE) - returns procedures by applicant id")
    void executeEstudianteReturnsByApplicantId() {
        List<Procedure> byApplicant = List.of(buildProcedure(1L, ProcedureStatus.REGISTRADO));
        when(procedureRepositoryPort.findByApplicantId(1L)).thenReturn(byApplicant);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.ESTUDIANTE, 1L);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findByApplicantId(1L);
    }

    @Test
    @DisplayName("execute(ESTUDIANTE, null) - returns empty list")
    void executeEstudianteNullUserIdReturnsEmpty() {
        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.ESTUDIANTE, null);

        assertTrue(result.isEmpty());
        verifyNoInteractions(procedureRepositoryPort);
    }

    @Test
    @DisplayName("execute(ADMIN) - returns all procedures (default case)")
    void executeAdminReturnsAll() {
        List<Procedure> all = List.of(buildProcedure(1L, ProcedureStatus.FINALIZADO));
        when(procedureRepositoryPort.findAll()).thenReturn(all);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.ADMIN);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findAll();
    }

    @Test
    @DisplayName("execute(DECANO) - returns empty when no pending decanato procedures")
    void executeDecanoReturnsEmpty() {
        when(procedureRepositoryPort.findByStatus(ProcedureStatus.PENDIENTE_DECANATO)).thenReturn(List.of());

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.DECANO);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("execute(COORDINADOR_GRUPO, userId) - finds group and filters by group")
    void executeCoordinadorWithUserIdAndGroupFound() {
        var groupEntity = new ResearchGroupEntity();
        groupEntity.setId(5);

        when(groupRepository.findByCurrentCoordinatorId(10L)).thenReturn(groupEntity);

        List<Procedure> coordList = List.of(buildProcedure(1L, ProcedureStatus.PENDIENTE_COORDINADOR));
        when(procedureRepositoryPort.findByStatusAndGroupId(ProcedureStatus.PENDIENTE_COORDINADOR, 5L))
                .thenReturn(coordList);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.COORDINADOR_GRUPO, 10L);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findByStatusAndGroupId(ProcedureStatus.PENDIENTE_COORDINADOR, 5L);
        verify(procedureRepositoryPort, never()).findByStatus(any());
    }

    @Test
    @DisplayName("execute(COORDINADOR_GRUPO, userId) - falls back when group not found")
    void executeCoordinadorWithUserIdAndGroupNotFound() {
        when(groupRepository.findByCurrentCoordinatorId(99L)).thenReturn(null);

        List<Procedure> coordList = List.of(buildProcedure(1L, ProcedureStatus.PENDIENTE_COORDINADOR));
        when(procedureRepositoryPort.findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR)).thenReturn(coordList);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.COORDINADOR_GRUPO, 99L);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR);
        verify(procedureRepositoryPort, never()).findByStatusAndGroupId(any(), any());
    }

    @Test
    @DisplayName("execute(COORDINADOR_GRUPO, null) - falls back without userId")
    void executeCoordinadorNullUserId() {
        List<Procedure> coordList = List.of(buildProcedure(1L, ProcedureStatus.PENDIENTE_COORDINADOR));
        when(procedureRepositoryPort.findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR)).thenReturn(coordList);

        List<ProcedureResponseDto> result = useCase.execute(RoleEnum.COORDINADOR_GRUPO, null);

        assertEquals(1, result.size());
        verify(procedureRepositoryPort).findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR);
        verifyNoInteractions(groupRepository);
    }
}
