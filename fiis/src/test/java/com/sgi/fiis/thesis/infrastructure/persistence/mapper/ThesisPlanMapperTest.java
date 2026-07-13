package com.sgi.fiis.thesis.infrastructure.persistence.mapper;

import com.sgi.fiis.thesis.domain.ThesisPlan;
import com.sgi.fiis.thesis.domain.ThesisPlanStatus;
import com.sgi.fiis.thesis.infrastructure.persistence.entity.ThesisPlanEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ThesisPlanMapper Unit Tests")
class ThesisPlanMapperTest {

    private final ThesisPlanMapper mapper = new ThesisPlanMapper();

    @Test
    @DisplayName("toDomain should map all fields correctly")
    void toDomainShouldMapAllFields() {
        ThesisPlanEntity entity = new ThesisPlanEntity();
        entity.setIdPlanTesis(1);
        entity.setTituloTesis("AI in Education");
        entity.setResumen("Research abstract");
        entity.setIdEstudiante(101L);
        entity.setIdLinea(3);
        entity.setIdGrupo(2);
        entity.setIdDocumentoActual(99);
        entity.setEstadoPlan(ThesisPlanStatus.APROBADO);
        entity.setFechaCreacion(LocalDateTime.of(2026, 1, 15, 10, 0));
        entity.setFechaActualizacion(LocalDateTime.of(2026, 6, 20, 14, 30));

        ThesisPlan result = mapper.toDomain(entity);

        assertEquals(entity.getIdPlanTesis(), result.getIdPlanTesis());
        assertEquals(entity.getTituloTesis(), result.getTituloTesis());
        assertEquals(entity.getResumen(), result.getResumen());
        assertEquals(entity.getIdEstudiante(), result.getIdEstudiante());
        assertEquals(entity.getIdLinea(), result.getIdLinea());
        assertEquals(entity.getIdGrupo(), result.getIdGrupo());
        assertEquals(entity.getIdDocumentoActual(), result.getIdDocumentoActual());
        assertEquals(entity.getEstadoPlan(), result.getEstadoPlan());
        assertEquals(entity.getFechaCreacion(), result.getFechaCreacion());
        assertEquals(entity.getFechaActualizacion(), result.getFechaActualizacion());
    }

    @Test
    @DisplayName("toEntity should map all fields correctly")
    void toEntityShouldMapAllFields() {
        ThesisPlan domain = new ThesisPlan(
                2, "Blockchain Thesis", "Abstract", 202L, 4, 3, 88,
                ThesisPlanStatus.OBSERVADO, LocalDateTime.of(2026, 3, 1, 9, 0),
                LocalDateTime.of(2026, 5, 10, 11, 0)
        );

        ThesisPlanEntity result = mapper.toEntity(domain);

        assertEquals(domain.getIdPlanTesis(), result.getIdPlanTesis());
        assertEquals(domain.getTituloTesis(), result.getTituloTesis());
        assertEquals(domain.getResumen(), result.getResumen());
        assertEquals(domain.getIdEstudiante(), result.getIdEstudiante());
        assertEquals(domain.getIdLinea(), result.getIdLinea());
        assertEquals(domain.getIdGrupo(), result.getIdGrupo());
        assertEquals(domain.getIdDocumentoActual(), result.getIdDocumentoActual());
        assertEquals(domain.getEstadoPlan(), result.getEstadoPlan());
        assertEquals(domain.getFechaCreacion(), result.getFechaCreacion());
        assertEquals(domain.getFechaActualizacion(), result.getFechaActualizacion());
    }

    @Test
    @DisplayName("toDomain should handle null fields")
    void toDomainShouldHandleNullFields() {
        ThesisPlanEntity entity = new ThesisPlanEntity();
        entity.setTituloTesis("Test");
        entity.setIdEstudiante(1L);
        entity.setIdLinea(1);
        entity.setIdGrupo(1);
        entity.setEstadoPlan(ThesisPlanStatus.POSTULADO);

        ThesisPlan result = mapper.toDomain(entity);

        assertNull(result.getIdPlanTesis());
        assertNull(result.getIdDocumentoActual());
        assertNull(result.getFechaCreacion());
        assertNull(result.getFechaActualizacion());
    }
}
