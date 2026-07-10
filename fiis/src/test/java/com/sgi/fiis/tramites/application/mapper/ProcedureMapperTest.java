package com.sgi.fiis.tramites.application.mapper;

import com.sgi.fiis.tramites.application.dto.ProcedureMovementResponseDto;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ProcedureMapperTest {

    @Test
    void toResponse_mapeaTodosLosCamposCorrectamente() {
        LocalDateTime ahora = LocalDateTime.of(2026, Month.JUNE, 17, 10, 0);

        Procedure tramite = Procedure.builder()
                .id(1L)
                .code("TRM-2026-000001")
                .procedureType(ProcedureType.PROJECT)
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .applicantId(42L)
                .groupId(5L)
                .currentReviewerRole(RoleEnum.COORDINADOR_GRUPO)
                .currentObservation(null)
                .projectReferenceId(100L)
                .thesisReferenceId(null)
                .reportReferenceId(null)
                .sentAt(ahora)
                .updatedAt(ahora)
                .build();

        ProcedureResponseDto dto = ProcedureMapper.toResponse(tramite);

        assertEquals(1L, dto.getId());
        assertEquals("TRM-2026-000001", dto.getCode());
        assertEquals(ProcedureType.PROJECT, dto.getProcedureType());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, dto.getCurrentStatus());
        assertEquals(42L, dto.getApplicantId());
        assertEquals(5L, dto.getGroupId());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, dto.getCurrentReviewerRole());
        assertNull(dto.getCurrentObservation());
        assertEquals(100L, dto.getProjectReferenceId());
        assertNull(dto.getThesisReferenceId());
        assertNull(dto.getReportReferenceId());
        assertEquals(ahora, dto.getSentAt());
        assertEquals(ahora, dto.getUpdatedAt());
    }

    @Test
    void toMovimientoResponse_mapeaTodosLosCamposCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, Month.JUNE, 17, 11, 0);

        ProcedureMovement movimiento = ProcedureMovement.builder()
                .actionUserId(10L)
                .action("APROBADO_POR_COORDINADOR")
                .previousStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .newStatus(ProcedureStatus.PENDIENTE_DIRECCION)
                .comment(null)
                .movementAt(fecha)
                .build();

        ProcedureMovementResponseDto dto = ProcedureMapper.toMovementResponse(movimiento);

        assertEquals(10L, dto.getActionUserId());
        assertEquals("APROBADO_POR_COORDINADOR", dto.getAction());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, dto.getPreviousStatus());
        assertEquals(ProcedureStatus.PENDIENTE_DIRECCION, dto.getNewStatus());
        assertNull(dto.getComment());
        assertEquals(fecha, dto.getMovementAt());
    }
}
