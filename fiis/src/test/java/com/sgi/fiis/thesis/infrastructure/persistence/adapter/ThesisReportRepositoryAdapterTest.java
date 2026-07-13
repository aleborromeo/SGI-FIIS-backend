package com.sgi.fiis.thesis.infrastructure.persistence.adapter;

import com.sgi.fiis.thesis.domain.ThesisReport;
import com.sgi.fiis.thesis.domain.ThesisReportStatus;
import com.sgi.fiis.thesis.infrastructure.persistence.entity.ThesisReportEntity;
import com.sgi.fiis.thesis.infrastructure.persistence.mapper.ThesisReportMapper;
import com.sgi.fiis.thesis.infrastructure.persistence.repository.ThesisReportJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ThesisReportRepositoryAdapter Unit Tests")
class ThesisReportRepositoryAdapterTest {

    @Mock
    private ThesisReportJpaRepository repository;

    @Mock
    private ThesisReportMapper mapper;

    @InjectMocks
    private ThesisReportRepositoryAdapter adapter;

    private ThesisReport getTestDomain() {
        return ThesisReport.nuevo(1, "Informe Final IA", 200);
    }

    private ThesisReportEntity getTestEntity() {
        ThesisReportEntity entity = new ThesisReportEntity();
        entity.setIdInformeTesis(5);
        entity.setIdPlanTesis(1);
        entity.setTituloFinal("Informe Final IA");
        entity.setIdDocumentoTesis(200);
        entity.setEstadoInforme(ThesisReportStatus.EN_REVISION);
        return entity;
    }

    @Test
    @DisplayName("Should save a thesis report and return the mapped domain object")
    void shouldSave() {
        ThesisReport domain = getTestDomain();
        ThesisReportEntity entity = getTestEntity();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        ThesisReport result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(domain.getTituloFinal(), result.getTituloFinal());
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Should find a thesis report by id")
    void shouldFindById() {
        ThesisReportEntity entity = getTestEntity();
        ThesisReport domain = getTestDomain();

        when(repository.findById(5)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<ThesisReport> result = adapter.findById(5);

        assertTrue(result.isPresent());
        assertEquals(domain.getTituloFinal(), result.get().getTituloFinal());
    }

    @Test
    @DisplayName("Should return empty when thesis report by id is not found")
    void shouldReturnEmptyWhenNotFoundById() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        Optional<ThesisReport> result = adapter.findById(99);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find thesis reports by plan id")
    void shouldFindByPlanTesis() {
        ThesisReportEntity entity = getTestEntity();
        ThesisReport domain = getTestDomain();

        when(repository.findByIdPlanTesis(1)).thenReturn(List.of(entity));
        when(mapper.toDomain(any(ThesisReportEntity.class))).thenReturn(domain);

        List<ThesisReport> result = adapter.findByPlanTesis(1);

        assertEquals(1, result.size());
        assertEquals(domain.getTituloFinal(), result.get(0).getTituloFinal());
    }

    @Test
    @DisplayName("Should return an empty list when no reports match the plan id")
    void shouldReturnEmptyListWhenNoneMatch() {
        when(repository.findByIdPlanTesis(99)).thenReturn(List.of());

        List<ThesisReport> result = adapter.findByPlanTesis(99);

        assertTrue(result.isEmpty());
    }
}
