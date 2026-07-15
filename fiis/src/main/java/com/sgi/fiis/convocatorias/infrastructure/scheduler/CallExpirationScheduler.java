package com.sgi.fiis.convocatorias.infrastructure.scheduler;

import com.sgi.fiis.convocatorias.infrastructure.persistence.ResearchCallJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
public class CallExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(CallExpirationScheduler.class);

    private final ResearchCallJpaRepository jpaRepository;
    private final Clock clock;

    public CallExpirationScheduler(ResearchCallJpaRepository jpaRepository, Clock clock) {
        this.jpaRepository = jpaRepository;
        this.clock = clock;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void closeExpiredCalls() {
        int closed = jpaRepository.closeExpiredCalls(LocalDate.now(clock));
        if (closed > 0) {
            log.info("Se cerraron {} convocatoria(s) vencida(s)", closed);
        }
    }
}
