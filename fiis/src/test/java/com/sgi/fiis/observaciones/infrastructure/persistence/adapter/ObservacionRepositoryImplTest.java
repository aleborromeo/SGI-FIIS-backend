package com.sgi.fiis.observaciones.infrastructure.persistence.adapter;

import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.model.ObservacionEstado;
import com.sgi.fiis.observaciones.domain.model.TipoObservacion;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.ObservacionJpaEntity;
import com.sgi.fiis.observaciones.infrastructure.persistence.mapper.ObservacionMapper;
import com.sgi.fiis.observaciones.infrastructure.persistence.repository.ObservacionJpaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObservacionRepositoryImpl Unit Tests")
class ObservacionRepositoryImplTest {

    @Mock
    private ObservacionJpaRepository jpaRepository;

    @Mock
    private ObservacionMapper mapper;

    private ObservacionRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ObservacionRepositoryImpl(jpaRepository, mapper);
    }

    @Test
    @DisplayName("Should save and return mapped domain object")
    void shouldSaveAndReturnDomain() {
        Observacion domain = Observacion.builder().build();
        ObservacionJpaEntity entity = new ObservacionJpaEntity();
        
        when(mapper.toJpa(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Observacion result = repository.save(domain);

        assertNotNull(result);
        verify(mapper, times(1)).toJpa(domain);
        verify(jpaRepository, times(1)).save(entity);
        verify(mapper, times(1)).toDomain(entity);
    }

    @Test
    @DisplayName("Should find observation by id and map to domain")
    void shouldFindByIdAndMap() {
        Integer id = 100;
        ObservacionJpaEntity entity = new ObservacionJpaEntity();
        Observacion domain = Observacion.builder().build();

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Observacion> result = repository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return empty optional when not found")
    void shouldReturnEmptyOptional() {
        Integer id = 999;
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Observacion> result = repository.findById(id);

        assertFalse(result.isPresent());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should find observations by tramite id")
    void shouldFindByTramite() {
        Integer idTramite = 1;
        ObservacionJpaEntity entity = new ObservacionJpaEntity();
        Observacion domain = Observacion.builder().build();

        when(jpaRepository.findByIdTramiteOrderByFechaRegistroDesc(idTramite)).thenReturn(Collections.singletonList(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Observacion> result = repository.findByIdTramite(idTramite);

        assertEquals(1, result.size());
        assertEquals(domain, result.get(0));
    }

    @Test
    @DisplayName("Should find pending observations by tramite id")
    void shouldFindPendientesByTramite() {
        Integer idTramite = 1;
        ObservacionJpaEntity entity = new ObservacionJpaEntity();
        Observacion domain = Observacion.builder().build();

        when(jpaRepository.findByIdTramiteAndEstadoObservacion(idTramite, "PENDIENTE")).thenReturn(Collections.singletonList(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Observacion> result = repository.findPendientesByIdTramite(idTramite);

        assertEquals(1, result.size());
        assertEquals(domain, result.get(0));
    }
}
