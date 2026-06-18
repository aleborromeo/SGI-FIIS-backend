package com.sgi.fiis.observations.infrastructure.persistence.adapter;

import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.infrastructure.persistence.entity.ObservationJpaEntity;
import com.sgi.fiis.observations.infrastructure.persistence.mapper.ObservationMapper;
import com.sgi.fiis.observations.infrastructure.persistence.repository.ObservationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObservationRepositoryImpl Unit Tests")
class ObservationRepositoryImplTest {

    @Mock
    private ObservationJpaRepository jpaRepository;

    @Mock
    private ObservationMapper mapper;

    private ObservationRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ObservationRepositoryImpl(jpaRepository, mapper);
    }

    @Test
    @DisplayName("Should save and return mapped domain object")
    void shouldSaveAndReturnDomain() {
        Observation domain = Observation.builder().build();
        ObservationJpaEntity entity = new ObservationJpaEntity();
        
        when(mapper.toJpa(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Observation result = repository.save(domain);

        assertNotNull(result);
        verify(mapper, times(1)).toJpa(domain);
        verify(jpaRepository, times(1)).save(entity);
        verify(mapper, times(1)).toDomain(entity);
    }

    @Test
    @DisplayName("Should find observation by id and map to domain")
    void shouldFindByIdAndMap() {
        Integer id = 100;
        ObservationJpaEntity entity = new ObservationJpaEntity();
        Observation domain = Observation.builder().build();

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Observation> result = repository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return empty optional when not found")
    void shouldReturnEmptyOptional() {
        Integer id = 999;
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Observation> result = repository.findById(id);

        assertFalse(result.isPresent());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should find observations by procedure id")
    void shouldFindByProcedure() {
        Integer procedureId = 1;
        ObservationJpaEntity entity = new ObservationJpaEntity();
        Observation domain = Observation.builder().build();

        when(jpaRepository.findByProcedureIdOrderByCreatedAtDesc(procedureId)).thenReturn(Collections.singletonList(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Observation> result = repository.findByProcedureId(procedureId);

        assertEquals(1, result.size());
        assertEquals(domain, result.get(0));
    }

    @Test
    @DisplayName("Should find pending observations by procedure id")
    void shouldFindPendingByProcedure() {
        Integer procedureId = 1;
        ObservationJpaEntity entity = new ObservationJpaEntity();
        Observation domain = Observation.builder().build();

        when(jpaRepository.findByProcedureIdAndStatus(procedureId, "PENDIENTE")).thenReturn(Collections.singletonList(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Observation> result = repository.findPendingByProcedureId(procedureId);

        assertEquals(1, result.size());
        assertEquals(domain, result.get(0));
    }
}
