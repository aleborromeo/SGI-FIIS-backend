package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.observations.application.usecase.RegisterObservationUseCase;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.port.ProcedureEventPublisherPort;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlagProcedureUseCaseTest {

    @Mock
    private ProcedureRepositoryPort procedureRepositoryPort;

    @Mock
    private ProcedureEventPublisherPort eventPublisherPort;

    @Mock
    private RegisterObservationUseCase registerObservationUseCase;

    @InjectMocks
    private FlagProcedureUseCase flagProcedureUseCase;

    private Procedure buildProcedure(ProcedureStatus status, RoleEnum rolRevisor) {
        return Procedure.builder()
                .id(1L)
                .code("TRM-2026-000001")
                .procedureType(ProcedureType.PLAN_TESIS)
                .applicantId(2L)
                .groupId(10L)
                .currentStatus(status)
                .currentReviewerRole(rolRevisor)
                .thesisReferenceId(200L)
                .sentAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_coordinadorObserva_retornaObservado() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_COORDINADOR, RoleEnum.COORDINADOR_GRUPO);
        when(procedureRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(procedureRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = flagProcedureUseCase.execute(1L, RoleEnum.COORDINADOR_GRUPO, 10L, "Falta firma");

        assertEquals(ProcedureStatus.OBSERVADO, result.getCurrentStatus());
        verify(eventPublisherPort).publishProcedureFlagged(any());
    }

    @Test
    void execute_directorObserva_retornaObservado() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_DIRECCION, RoleEnum.DIRECTOR_INVESTIGACION);
        when(procedureRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(procedureRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = flagProcedureUseCase.execute(1L, RoleEnum.DIRECTOR_INVESTIGACION, 20L, "Falta anexo");

        assertEquals(ProcedureStatus.OBSERVADO, result.getCurrentStatus());
        verify(eventPublisherPort).publishProcedureFlagged(any());
    }

    @Test
    void execute_decanoObserva_retornaObservado() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_DECANATO, RoleEnum.DECANO);
        when(procedureRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(procedureRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = flagProcedureUseCase.execute(1L, RoleEnum.DECANO, 30L, "Falta resolución previa");

        assertEquals(ProcedureStatus.OBSERVADO, result.getCurrentStatus());
        verify(eventPublisherPort).publishProcedureFlagged(any());
    }

    @Test
    void execute_rolInvalido_lanzaBusinessException() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_COORDINADOR, RoleEnum.COORDINADOR_GRUPO);
        when(procedureRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));

        assertThrows(BusinessException.class,
                () -> flagProcedureUseCase.execute(1L, RoleEnum.EVALUADOR, 99L, "texto"));
        verify(procedureRepositoryPort, never()).save(any());
    }

    @Test
    void execute_tramiteNoEncontrado_lanzaResourceNotFoundException() {
        when(procedureRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> flagProcedureUseCase.execute(999L, RoleEnum.COORDINADOR_GRUPO, 10L, "texto"));
    }
}
