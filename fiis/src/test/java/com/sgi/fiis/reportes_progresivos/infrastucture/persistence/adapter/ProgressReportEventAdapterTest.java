package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProgressReportEventAdapter Unit Tests")
class ProgressReportEventAdapterTest {

    @InjectMocks
    private ProgressReportEventAdapter progressReportEventAdapter;

    @Test
    @DisplayName("Should publish report procedure without throwing exceptions")
    void shouldPublishReportProcedureWithoutExceptions() {
        assertDoesNotThrow(() -> progressReportEventAdapter.publishReportProcedure(1L, 2L, 3L, 4L));
    }
}
