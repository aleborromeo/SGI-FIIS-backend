package com.sgi.fiis.convocatorias.infrastructure.scheduler;

import com.sgi.fiis.convocatorias.infrastructure.persistence.ResearchCallJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CallExpirationScheduler Unit Tests")
class CallExpirationSchedulerTest {

    @Mock
    private ResearchCallJpaRepository jpaRepository;

    private CallExpirationScheduler scheduler;

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-07-14T12:00:00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        scheduler = new CallExpirationScheduler(jpaRepository, FIXED_CLOCK);
    }

    @Test
    @DisplayName("closeExpiredCalls: logs when some calls are closed")
    void closeExpiredCalls_logsWhenClosed() {
        when(jpaRepository.closeExpiredCalls(any())).thenReturn(5);

        scheduler.closeExpiredCalls();

        verify(jpaRepository).closeExpiredCalls(any());
    }

    @Test
    @DisplayName("closeExpiredCalls: no log when zero calls closed")
    void closeExpiredCalls_noLogWhenZero() {
        when(jpaRepository.closeExpiredCalls(any())).thenReturn(0);

        scheduler.closeExpiredCalls();

        verify(jpaRepository).closeExpiredCalls(any());
    }
}
