package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter;

import com.sgi.fiis.reportes_progresivos.ProgressReportTestHelper;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.entity.ProgressReportEntity;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.repository.ProgressReportJpaRepository;
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
@DisplayName("ProgressReportRepositoryAdapter Unit Tests")
class ProgressReportRepositoryAdapterTest {

    @Mock
    private ProgressReportJpaRepository jpaRepository;

    @InjectMocks
    private ProgressReportRepositoryAdapter repositoryAdapter;

    @Test
    @DisplayName("Should save progress report successfully")
    void shouldSaveReport() {
        ProgressReport domain = ProgressReportTestHelper.createReport(1L, 10L, null);

        ProgressReportEntity entity = ProgressReportMapper.toEntity(domain);
        when(jpaRepository.save(any(ProgressReportEntity.class))).thenReturn(entity);

        ProgressReport saved = repositoryAdapter.save(domain);

        assertNotNull(saved);
        assertEquals(domain.getId(), saved.getId());
        verify(jpaRepository, times(1)).save(any(ProgressReportEntity.class));
    }

    @Test
    @DisplayName("Should find progress report by id")
    void shouldFindById() {
        ProgressReport domain = ProgressReportTestHelper.createReport(1L, 10L, null);
        ProgressReportEntity entity = ProgressReportMapper.toEntity(domain);

        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<ProgressReport> found = repositoryAdapter.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        verify(jpaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should find progress reports by project id ordered by registration date desc")
    void shouldFindByProjectId() {
        ProgressReport domain = ProgressReportTestHelper.createReport(1L, 10L, null);
        ProgressReportEntity entity = ProgressReportMapper.toEntity(domain);

        when(jpaRepository.findByProjectIdOrderByRegistrationDateDesc(10L)).thenReturn(List.of(entity));

        List<ProgressReport> found = repositoryAdapter.findByProjectId(10L);

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        assertEquals(1L, found.get(0).getId());
        verify(jpaRepository, times(1)).findByProjectIdOrderByRegistrationDateDesc(10L);
    }

    @Test
    @DisplayName("Should check if progress report exists by id")
    void shouldCheckExistsById() {
        when(jpaRepository.existsById(1L)).thenReturn(true);

        boolean exists = repositoryAdapter.existsById(1L);

        assertTrue(exists);
        verify(jpaRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Should find all progress reports")
    void shouldFindAll() {
        ProgressReport domain = ProgressReportTestHelper.createReport(1L, 10L, null);
        ProgressReportEntity entity = ProgressReportMapper.toEntity(domain);

        when(jpaRepository.findAllByOrderByRegistrationDateDesc()).thenReturn(List.of(entity));

        List<ProgressReport> found = repositoryAdapter.findAll();

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        verify(jpaRepository, times(1)).findAllByOrderByRegistrationDateDesc();
    }

    @Test
    @DisplayName("Should find progress reports by status")
    void shouldFindByStatus() {
        ProgressReport domain = ProgressReportTestHelper.createReport(1L, 10L, null);
        ProgressReportEntity entity = ProgressReportMapper.toEntity(domain);

        when(jpaRepository.findByReportStatusOrderByRegistrationDateDesc("PENDIENTE")).thenReturn(List.of(entity));

        List<ProgressReport> found = repositoryAdapter.findByStatus("PENDING");

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        verify(jpaRepository, times(1)).findByReportStatusOrderByRegistrationDateDesc("PENDIENTE");
    }
}
