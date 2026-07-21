package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter;

import com.sgi.fiis.tramites.application.usecase.CreateProcedureUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProgressReportEventAdapter Unit Tests")
class ProgressReportEventAdapterTest {

    @Mock
    private CreateProcedureUseCase createProcedureUseCase;

    @InjectMocks
    private ProgressReportEventAdapter progressReportEventAdapter;

    @Test
    @DisplayName("Should publish report procedure without throwing exceptionss")
    void shouldPublishReportProcedureWithoutExceptions() {
        when(createProcedureUseCase.execute(any())).thenReturn(null);
        assertDoesNotThrow(() -> progressReportEventAdapter.publishReportProcedure(1L, 2L, 3L, 4L));
    }
}
