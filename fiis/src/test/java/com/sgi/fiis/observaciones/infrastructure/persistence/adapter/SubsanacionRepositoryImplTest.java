package com.sgi.fiis.observaciones.infrastructure.persistence.adapter;

import com.sgi.fiis.observaciones.domain.model.Subsanacion;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.SubsanacionJpaEntity;
import com.sgi.fiis.observaciones.infrastructure.persistence.mapper.SubsanacionMapper;
import com.sgi.fiis.observaciones.infrastructure.persistence.repository.SubsanacionJpaRepository;
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
@DisplayName("SubsanacionRepositoryImpl Unit Tests")
class SubsanacionRepositoryImplTest {

    @Mock
    private SubsanacionJpaRepository jpaRepository;

    @Mock
    private SubsanacionMapper mapper;

    private SubsanacionRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new SubsanacionRepositoryImpl(jpaRepository, mapper);
    }

    @Test
    @DisplayName("Should save and return mapped domain object")
    void shouldSaveAndReturnDomain() {
        Subsanacion domain = Subsanacion.builder().build();
        SubsanacionJpaEntity entity = new SubsanacionJpaEntity();

        when(mapper.toJpa(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Subsanacion result = repository.save(domain);

        assertNotNull(result);
        verify(mapper, times(1)).toJpa(domain);
        verify(jpaRepository, times(1)).save(entity);
        verify(mapper, times(1)).toDomain(entity);
    }

    @Test
    @DisplayName("Should find subsanacion by id and map to domain")
    void shouldFindByIdAndMap() {
        Integer id = 200;
        SubsanacionJpaEntity entity = new SubsanacionJpaEntity();
        Subsanacion domain = Subsanacion.builder().build();

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Subsanacion> result = repository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should find subsanaciones by observacion id")
    void shouldFindByObservacion() {
        Integer idObservacion = 100;
        SubsanacionJpaEntity entity = new SubsanacionJpaEntity();
        Subsanacion domain = Subsanacion.builder().build();

        when(jpaRepository.findByIdObservacionOrderByFechaRegistroAsc(idObservacion)).thenReturn(Collections.singletonList(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Subsanacion> result = repository.findByIdObservacion(idObservacion);

        assertEquals(1, result.size());
        assertEquals(domain, result.get(0));
    }
}
