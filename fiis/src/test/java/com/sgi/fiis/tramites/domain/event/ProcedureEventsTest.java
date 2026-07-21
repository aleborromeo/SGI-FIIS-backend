package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ProcedureEventsTest {

    @Test
    void tramiteAprobadoEvent_construyeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, Month.JUNE, 17, 10, 0);

        ProcedureApprovedEvent evento = ProcedureApprovedEvent.builder()
                .procedureId(1L)
                .code("TRM-2026-001")
                .procedureType(ProcedureType.PROJECT)
                .applicantId(42L)
                .resultingStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .approverId(10L)
                .approverRole(RoleEnum.COORDINADOR_GRUPO)
                .approvalDate(fecha)
                .build();

        assertEquals(1L, evento.getIdTramite());
        assertEquals("TRM-2026-001", evento.getCode());
        assertEquals(ProcedureType.PROJECT, evento.getProcedureType());
        assertEquals(42L, evento.getApplicantId());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, evento.getResultingStatus());
        assertEquals(10L, evento.getIdAprobador());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, evento.getApproverRole());
        assertEquals(fecha, evento.getFechaAprobacion());
    }

    @Test
    void tramiteObservadoEvent_construyeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, Month.JUNE, 17, 11, 30);

        ProcedureFlaggedEvent evento = ProcedureFlaggedEvent.builder()
                .procedureId(2L)
                .code("TRM-2026-002")
                .procedureType(ProcedureType.PLAN_TESIS)
                .applicantId(55L)
                .observerId(20L)
                .observerRole(RoleEnum.DIRECTOR_INVESTIGACION)
                .observationText("Falta bibliografía actualizada")
                .observationDate(fecha)
                .build();

        assertEquals(2L, evento.getIdTramite());
        assertEquals("TRM-2026-002", evento.getCode());
        assertEquals(ProcedureType.PLAN_TESIS, evento.getProcedureType());
        assertEquals(55L, evento.getApplicantId());
        assertEquals(20L, evento.getIdObservador());
        assertEquals(RoleEnum.DIRECTOR_INVESTIGACION, evento.getObserverRole());
        assertEquals("Falta bibliografía actualizada", evento.getTextoObservacion());
        assertEquals(fecha, evento.getFechaObservacion());
    }

    @Test
    void tramiteFinalizadoEvent_construyeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, Month.JUNE, 17, 15, 45);

        ProcedureFinalizedEvent evento = ProcedureFinalizedEvent.builder()
                .procedureId(3L)
                .code("TRM-2026-003")
                .procedureType(ProcedureType.REPORT_AVANCE)
                .applicantId(77L)
                .finalizationDate(fecha)
                .build();

        assertEquals(3L, evento.getIdTramite());
        assertEquals("TRM-2026-003", evento.getCode());
        assertEquals(ProcedureType.REPORT_AVANCE, evento.getProcedureType());
        assertEquals(77L, evento.getApplicantId());
        assertEquals(fecha, evento.getFechaFinalizacion());
    }
}
