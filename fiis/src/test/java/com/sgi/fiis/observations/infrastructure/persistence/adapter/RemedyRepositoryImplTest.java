package com.sgi.fiis.observations.infrastructure.persistence.adapter;

import com.sgi.fiis.observations.domain.model.Remedy;
import com.sgi.fiis.observations.infrastructure.persistence.entity.RemedyJpaEntity;
import com.sgi.fiis.observations.infrastructure.persistence.mapper.RemedyMapper;
import com.sgi.fiis.observations.infrastructure.persistence.repository.RemedyJpaRepository;
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
@DisplayName("RemedyRepositoryImpl Unit Tests")
class RemedyRepositoryImplTest {

    @Mock
    private RemedyJpaRepository jpaRepository;

    @Mock
    private RemedyMapper mapper;

    private RemedyRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new RemedyRepositoryImpl(jpaRepository, mapper);
    }

    @Test
    @DisplayName("Should save and return mapped domain object")
    void shouldSaveAndReturnDomain() {
        Remedy domain = Remedy.builder().build();
        RemedyJpaEntity entity = new RemedyJpaEntity();

        when(mapper.toJpa(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Remedy result = repository.save(domain);

        assertNotNull(result);
        verify(mapper, times(1)).toJpa(domain);
        verify(jpaRepository, times(1)).save(entity);
        verify(mapper, times(1)).toDomain(entity);
    }

    @Test
    @DisplayName("Should find remedy by id and map to domain")
    void shouldFindByIdAndMap() {
        Integer id = 200;
        RemedyJpaEntity entity = new RemedyJpaEntity();
        Remedy domain = Remedy.builder().build();

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Remedy> result = repository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should find remedies by observation id")
    void shouldFindByObservation() {
        Integer observationId = 100;
        RemedyJpaEntity entity = new RemedyJpaEntity();
        Remedy domain = Remedy.builder().build();

        when(jpaRepository.findByObservationIdOrderByCreatedAtAsc(observationId)).thenReturn(Collections.singletonList(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Remedy> result = repository.findByObservationId(observationId);

        assertEquals(1, result.size());
        assertEquals(domain, result.get(0));
    }
}
