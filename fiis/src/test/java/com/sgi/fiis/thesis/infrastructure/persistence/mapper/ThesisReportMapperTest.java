package com.sgi.fiis.thesis.infrastructure.persistence.mapper;

import com.sgi.fiis.thesis.domain.ThesisReport;
import com.sgi.fiis.thesis.domain.ThesisReportStatus;
import com.sgi.fiis.thesis.infrastructure.persistence.entity.ThesisReportEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ThesisReportMapper Unit Tests")
class ThesisReportMapperTest {

    private final ThesisReportMapper mapper = new ThesisReportMapper();

    @Test
    @DisplayName("toDomain should map all fields correctly")
    void toDomainShouldMapAllFields() {
        ThesisReportEntity entity = new ThesisReportEntity();
        entity.setIdInformeTesis(5);
        entity.setIdPlanTesis(1);
        entity.setTituloFinal("AI Final Report");
        entity.setIdDocumentoTesis(200);
        entity.setFechaPresentacion(LocalDateTime.of(2026, 7, 10, 15, 0));
        entity.setEstadoInforme(ThesisReportStatus.APROBADO);

        ThesisReport result = mapper.toDomain(entity);

        assertEquals(entity.getIdInformeTesis(), result.getIdInformeTesis());
        assertEquals(entity.getIdPlanTesis(), result.getIdPlanTesis());
        assertEquals(entity.getTituloFinal(), result.getTituloFinal());
        assertEquals(entity.getIdDocumentoTesis(), result.getIdDocumentoTesis());
        assertEquals(entity.getFechaPresentacion(), result.getFechaPresentacion());
        assertEquals(entity.getEstadoInforme(), result.getEstadoInforme());
    }

    @Test
    @DisplayName("toEntity should map all fields correctly")
    void toEntityShouldMapAllFields() {
        ThesisReport domain = new ThesisReport(
                10, 3, "Blockchain Report", 300,
                LocalDateTime.of(2026, 6, 1, 12, 0), ThesisReportStatus.OBSERVADO
        );

        ThesisReportEntity result = mapper.toEntity(domain);

        assertEquals(domain.getIdInformeTesis(), result.getIdInformeTesis());
        assertEquals(domain.getIdPlanTesis(), result.getIdPlanTesis());
        assertEquals(domain.getTituloFinal(), result.getTituloFinal());
        assertEquals(domain.getIdDocumentoTesis(), result.getIdDocumentoTesis());
        assertEquals(domain.getFechaPresentacion(), result.getFechaPresentacion());
        assertEquals(domain.getEstadoInforme(), result.getEstadoInforme());
    }

    @Test
    @DisplayName("toDomain should handle null fields")
    void toDomainShouldHandleNullFields() {
        ThesisReportEntity entity = new ThesisReportEntity();
        entity.setIdPlanTesis(1);
        entity.setTituloFinal("Test");
        entity.setIdDocumentoTesis(99);

        ThesisReport result = mapper.toDomain(entity);

        assertNull(result.getIdInformeTesis());
        assertNull(result.getFechaPresentacion());
        assertEquals(ThesisReportStatus.EN_REVISION, result.getEstadoInforme());
    }
}
