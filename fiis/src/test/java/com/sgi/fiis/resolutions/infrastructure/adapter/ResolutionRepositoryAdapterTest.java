package com.sgi.fiis.resolutions.infrastructure.adapter;

import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.infrastructure.entity.ResolutionEntity;
import com.sgi.fiis.resolutions.infrastructure.mapper.ResolutionMapper;
import com.sgi.fiis.resolutions.infrastructure.repository.ResolutionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResolutionRepositoryAdapter Unit Tests")
class ResolutionRepositoryAdapterTest {

    @Mock
    private ResolutionJpaRepository repository;

    @Mock
    private ResolutionMapper mapper;

    @InjectMocks
    private ResolutionRepositoryAdapter adapter;

    private Resolution resolution;
    private ResolutionEntity resolutionEntity;

    @BeforeEach
    void setUp() {
        resolution = new Resolution(
                1L,
                "RES-2023-001",
                null,
                null,
                null,
                null,
                null
        );

        resolutionEntity = new ResolutionEntity();
        resolutionEntity.setIdResolucion(1L);
        resolutionEntity.setNumeroResolucion("RES-2023-001");
    }

    @Test
    @DisplayName("Should save resolution successfully")
    void save_Success() {
        when(mapper.toEntity(resolution)).thenReturn(resolutionEntity);
        when(repository.save(resolutionEntity)).thenReturn(resolutionEntity);
        when(mapper.toDomain(resolutionEntity)).thenReturn(resolution);

        Resolution result = adapter.save(resolution);

        assertNotNull(result);
        assertEquals(1L, result.idResolucion());
        assertEquals("RES-2023-001", result.numeroResolucion());

        verify(mapper).toEntity(resolution);
        verify(repository).save(resolutionEntity);
        verify(mapper).toDomain(resolutionEntity);
    }

    @Test
    @DisplayName("Should find resolution by id successfully")
    void findById_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(resolutionEntity));
        when(mapper.toDomain(resolutionEntity)).thenReturn(resolution);

        Optional<Resolution> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().idResolucion());
        assertEquals("RES-2023-001", result.get().numeroResolucion());

        verify(repository).findById(1L);
        verify(mapper).toDomain(resolutionEntity);
    }

    @Test
    @DisplayName("Should return empty when resolution not found")
    void findById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<Resolution> result = adapter.findById(1L);

        assertTrue(result.isEmpty());

        verify(repository).findById(1L);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should return true if resolution exists by number")
    void existsByNumber_ReturnsTrue() {
        when(repository.existsByNumeroResolucion("RES-2023-001")).thenReturn(true);

        boolean result = adapter.existsByNumber("RES-2023-001");

        assertTrue(result);
        verify(repository).existsByNumeroResolucion("RES-2023-001");
    }

    @Test
    @DisplayName("Should return false if resolution does not exist by number")
    void existsByNumber_ReturnsFalse() {
        when(repository.existsByNumeroResolucion("RES-2023-001")).thenReturn(false);

        boolean result = adapter.existsByNumber("RES-2023-001");

        assertFalse(result);
        verify(repository).existsByNumeroResolucion("RES-2023-001");
    }
}
