package com.sgi.fiis.reportes_progresivos.infrastucture;

import com.sgi.fiis.reportes_progresivos.application.usecase.AmendProgressReportService;
import com.sgi.fiis.reportes_progresivos.application.usecase.CreateProgressReportService;
import com.sgi.fiis.reportes_progresivos.application.usecase.QueryProgressReportService;
import com.sgi.fiis.reportes_progresivos.application.usecase.ReviewProgressReportService;
import com.sgi.fiis.reportes_progresivos.domain.port.in.AmendProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.in.CreateProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.in.QueryProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.in.ReviewProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportRepositoryPort;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportEventPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the Progress Reports module.
 * Wires use case services with their required domain ports via constructor injection.
 */
@Configuration
public class ProgressReportsConfig {

    @Bean
    public CreateProgressReportUseCase createProgressReportUseCase(
            ProgressReportRepositoryPort repositoryPort,
            ProgressReportEventPort procedureEventPort) {
        return new CreateProgressReportService(repositoryPort, procedureEventPort);
    }

    @Bean
    public ReviewProgressReportUseCase reviewProgressReportUseCase(
            ProgressReportRepositoryPort repositoryPort) {
        return new ReviewProgressReportService(repositoryPort);
    }

    @Bean
    public QueryProgressReportUseCase queryProgressReportUseCase(
            ProgressReportRepositoryPort repositoryPort) {
        return new QueryProgressReportService(repositoryPort);
    }

    @Bean
    public AmendProgressReportUseCase amendProgressReportUseCase(
            ProgressReportRepositoryPort repositoryPort) {
        return new AmendProgressReportService(repositoryPort);
    }
}
