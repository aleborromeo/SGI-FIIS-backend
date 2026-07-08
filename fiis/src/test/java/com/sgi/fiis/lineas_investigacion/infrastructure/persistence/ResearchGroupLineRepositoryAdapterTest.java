package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResearchGroupLineRepositoryAdapterTest {

    @Mock
    private SpringDataResearchGroupLineRepository springDataRepository;

    @InjectMocks
    private ResearchGroupLineRepositoryAdapter adapter;

    @Test
    void assignGroupToLine_shouldSaveEntity() {
        adapter.assignGroupToLine(1, 2);
        
        verify(springDataRepository).save(any(ResearchGroupLineEntity.class));
    }

    @Test
    void removeGroupFromLine_shouldDeleteById() {
        adapter.removeGroupFromLine(1, 2);

        verify(springDataRepository).deleteById(any());
    }

    @Test
    void isGroupAssignedToLine_shouldReturnTrue_whenExists() {
        when(springDataRepository.existsByGroupIdAndLineId(1, 2)).thenReturn(true);

        boolean result = adapter.isGroupAssignedToLine(1, 2);

        assertTrue(result);
        verify(springDataRepository).existsByGroupIdAndLineId(1, 2);
    }

    @Test
    void isGroupAssignedToLine_shouldReturnFalse_whenDoesNotExist() {
        when(springDataRepository.existsByGroupIdAndLineId(1, 2)).thenReturn(false);

        boolean result = adapter.isGroupAssignedToLine(1, 2);

        assertFalse(result);
        verify(springDataRepository).existsByGroupIdAndLineId(1, 2);
    }
}
