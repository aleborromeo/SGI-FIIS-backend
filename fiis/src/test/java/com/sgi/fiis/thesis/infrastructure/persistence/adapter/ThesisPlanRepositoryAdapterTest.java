package com.sgi.fiis.thesis.infrastructure.persistence.adapter;

import com.sgi.fiis.thesis.domain.ThesisPlan;
import com.sgi.fiis.thesis.domain.ThesisPlanStatus;
import com.sgi.fiis.thesis.infrastructure.persistence.entity.ThesisPlanEntity;
import com.sgi.fiis.thesis.infrastructure.persistence.mapper.ThesisPlanMapper;
import com.sgi.fiis.thesis.infrastructure.persistence.repository.ThesisPlanJpaRepository;
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
@DisplayName("ThesisPlanRepositoryAdapter Unit Tests")
class ThesisPlanRepositoryAdapterTest {

    @Mock
    private ThesisPlanJpaRepository repository;

    @Mock
    private ThesisPlanMapper mapper;

    @InjectMocks
    private ThesisPlanRepositoryAdapter adapter;

    private ThesisPlan getTestThesisPlan() {
        return ThesisPlan.nuevo("Tesis de IA", "Resumen", 1L, 2, 3, null);
    }

    private ThesisPlanEntity getTestThesisPlanEntity() {
        ThesisPlanEntity entity = new ThesisPlanEntity();
        entity.setIdPlanTesis(1);
        entity.setTituloTesis("Tesis de IA");
        entity.setIdEstudiante(1L);
        entity.setIdGrupo(3);
        entity.setEstadoPlan(ThesisPlanStatus.POSTULADO);
        return entity;
    }

    @Test
    @DisplayName("Should save a thesis plan and return the mapped domain object")
    void shouldSaveThesisPlan() {
        ThesisPlan domain = getTestThesisPlan();
        ThesisPlanEntity entity = getTestThesisPlanEntity();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.saveAndFlush(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        ThesisPlan result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(domain.getTituloTesis(), result.getTituloTesis());
        verify(repository).saveAndFlush(entity);
    }

    @Test
    @DisplayName("Should find a thesis plan by id")
    void shouldFindById() {
        ThesisPlanEntity entity = getTestThesisPlanEntity();
        ThesisPlan domain = getTestThesisPlan();

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<ThesisPlan> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(domain.getTituloTesis(), result.get().getTituloTesis());
    }

    @Test
    @DisplayName("Should return empty when thesis plan by id is not found")
    void shouldReturnEmptyWhenNotFoundById() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        Optional<ThesisPlan> result = adapter.findById(99);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find thesis plans by student id")
    void shouldFindByEstudiante() {
        ThesisPlanEntity entity = getTestThesisPlanEntity();
        ThesisPlan domain = getTestThesisPlan();

        when(repository.findByIdEstudiante(1L)).thenReturn(List.of(entity));
        when(mapper.toDomain(any(ThesisPlanEntity.class))).thenReturn(domain);

        List<ThesisPlan> result = adapter.findByEstudiante(1L);

        assertEquals(1, result.size());
        assertEquals(domain.getTituloTesis(), result.get(0).getTituloTesis());
    }

    @Test
    @DisplayName("Should find thesis plans by group id")
    void shouldFindByGrupo() {
        ThesisPlanEntity entity = getTestThesisPlanEntity();
        ThesisPlan domain = getTestThesisPlan();

        when(repository.findByIdGrupo(3)).thenReturn(List.of(entity));
        when(mapper.toDomain(any(ThesisPlanEntity.class))).thenReturn(domain);

        List<ThesisPlan> result = adapter.findByGrupo(3);

        assertEquals(1, result.size());
        assertEquals(domain.getTituloTesis(), result.get(0).getTituloTesis());
    }

    @Test
    @DisplayName("Should find thesis plans by status")
    void shouldFindByEstado() {
        ThesisPlanEntity entity = getTestThesisPlanEntity();
        ThesisPlan domain = getTestThesisPlan();

        when(repository.findByEstadoPlan(ThesisPlanStatus.POSTULADO)).thenReturn(List.of(entity));
        when(mapper.toDomain(any(ThesisPlanEntity.class))).thenReturn(domain);

        List<ThesisPlan> result = adapter.findByEstado(ThesisPlanStatus.POSTULADO);

        assertEquals(1, result.size());
        assertEquals(domain.getTituloTesis(), result.get(0).getTituloTesis());
    }

    @Test
    @DisplayName("Should return an empty list when no thesis plans match the status")
    void shouldReturnEmptyListWhenNoneMatchStatus() {
        when(repository.findByEstadoPlan(ThesisPlanStatus.RECHAZADO)).thenReturn(List.of());

        List<ThesisPlan> result = adapter.findByEstado(ThesisPlanStatus.RECHAZADO);

        assertTrue(result.isEmpty());
    }
}