package com.sgi.fiis.tramites.application.usecase;

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
class RegisterResolutionUseCaseTest {

    @Mock
    private ProcedureRepositoryPort procedureRepositoryPort;

    @Mock
    private ProcedureEventPublisherPort eventPublisherPort;

    @InjectMocks
    private RegisterResolutionUseCase registerResolutionUseCase;

    private Procedure buildPendienteDecanatoProcedure() {
        return Procedure.builder()
                .id(1L)
                .code("TRM-2026-000001")
                .procedureType(ProcedureType.PROJECT)
                .applicantId(2L)
                .groupId(10L)
                .currentStatus(ProcedureStatus.PENDIENTE_DECANATO)
                .currentReviewerRole(RoleEnum.DECANO)
                .projectReferenceId(100L)
                .sentAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_decanoRegistraResolucion_retornaFinalizado() {
        Procedure tramite = buildPendienteDecanatoProcedure();
        when(procedureRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(procedureRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = registerResolutionUseCase.execute(1L, 30L);

        assertEquals(ProcedureStatus.FINALIZADO, result.getCurrentStatus());
        verify(eventPublisherPort).publishProcedureFinalized(any());
    }

    @Test
    void execute_tramiteNoEncontrado_lanzaResourceNotFoundException() {
        when(procedureRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> registerResolutionUseCase.execute(999L, 30L));
    }
}
