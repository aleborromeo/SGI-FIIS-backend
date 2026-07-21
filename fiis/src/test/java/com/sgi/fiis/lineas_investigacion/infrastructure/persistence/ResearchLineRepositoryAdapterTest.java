package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResearchLineRepositoryAdapter Unit Tests")
@SuppressWarnings("all")
class ResearchLineRepositoryAdapterTest {

    @Mock private SpringDataResearchLineRepository springDataRepository;
    @InjectMocks private ResearchLineRepositoryAdapter adapter;

    private ResearchLineEntity getTestLineEntity() {
        ResearchLineEntity entity = new ResearchLineEntity();
        entity.setId(1);
        entity.setName("Tecnologia y Comunicaciones");
        entity.setActive(true);
        return entity;
    }

    private ResearchLine getTestLine() {
        return ResearchLine.builder()
                .id(1)
                .lineName("Tecnologia y Comunicaciones")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully save a ResearchLine")
    void testSave() {
        ResearchLine domain = getTestLine();
        ResearchLineEntity entity = getTestLineEntity();

        when(springDataRepository.save(any(ResearchLineEntity.class))).thenReturn(entity);

        ResearchLine result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(domain.getId(), result.getId());
        assertEquals(domain.getLineName(), result.getLineName());
        assertTrue(result.isActive());
        verify(springDataRepository).save(any(ResearchLineEntity.class));
    }

    @Test
    @DisplayName("Should return null on save when input is null")
    void testSaveNull() {
        assertNull(adapter.save(null));
        verifyNoInteractions(springDataRepository);
    }

    @Test
    @DisplayName("Should find a ResearchLine by ID")
    void testFindById() {
        ResearchLineEntity entity = getTestLineEntity();
        when(springDataRepository.findById(1)).thenReturn(Optional.of(entity));

        Optional<ResearchLine> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(springDataRepository).findById(1);
    }

    @Test
    @DisplayName("Should return empty when ResearchLine by ID is not found")
    void testFindByIdNotFound() {
        when(springDataRepository.findById(1)).thenReturn(Optional.empty());

        Optional<ResearchLine> result = adapter.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find all ResearchLines")
    void testFindAll() {
        ResearchLineEntity entity = getTestLineEntity();
        when(springDataRepository.findAll()).thenReturn(Collections.singletonList(entity));

        List<ResearchLine> result = adapter.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tecnologia y Comunicaciones", result.get(0).getLineName());
        verify(springDataRepository).findAll();
    }

    @Test
    @DisplayName("Should find all active ResearchLines")
    void testFindAllActive() {
        ResearchLineEntity entity = getTestLineEntity();
        when(springDataRepository.findByActiveTrue()).thenReturn(Collections.singletonList(entity));

        List<ResearchLine> result = adapter.findAllActive();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(springDataRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Should find active ResearchLines by group ID")
    void testFindActiveByGroup() {
        ResearchLineEntity entity = getTestLineEntity();
        when(springDataRepository.findActiveByGroupId(10)).thenReturn(Collections.singletonList(entity));

        List<ResearchLine> result = adapter.findActiveByGroup(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(springDataRepository).findActiveByGroupId(10);
    }

    @Test
    @DisplayName("Should check if research line exists by name")
    void testExistsByName() {
        when(springDataRepository.existsByName("Tecnologia y Comunicaciones")).thenReturn(true);

        assertTrue(adapter.existsByName("Tecnologia y Comunicaciones"));
        verify(springDataRepository).existsByName("Tecnologia y Comunicaciones");
    }
}
