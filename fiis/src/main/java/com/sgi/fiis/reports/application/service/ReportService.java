package com.sgi.fiis.reports.application.service;

import com.sgi.fiis.reports.domain.model.*;
import com.sgi.fiis.reports.domain.repository.ReportRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for institutional reports.
 * Orchestrates calls to the repository and returns paginated responses
 * ready for the controller.
 */
@Service
public class ReportService {

    private final ReportRepositoryPort repo;

    public ReportService(ReportRepositoryPort repo) {
        this.repo = repo;
    }

    /**
     * Generates the project report using the specified filters.
     * Supports combining: group, status, dates, researcher, and call.
     */
    public PaginatedResponse<ProjectReport> generateProjectReport(ReportFilter filter) {
        List<ProjectReport> data  = repo.findProjects(filter);
        long                total = repo.countProjects(filter);
        return new PaginatedResponse<>(data, total, filter.getPage(), filter.getSize());
    }

    /**
     * Generates the procedure report using the specified filters.
     * Supports combining: group, status, dates, researcher, and procedure type.
     */
    public PaginatedResponse<ProcedureReport> generateProcedureReport(ReportFilter filter) {
        List<ProcedureReport> data  = repo.findProcedures(filter);
        long                  total = repo.countProcedures(filter);
        return new PaginatedResponse<>(data, total, filter.getPage(), filter.getSize());
    }

    /**
     * Generates the resolution report using the specified filters.
     * Supports combining: dates, researcher, and procedure type.
     */
    public PaginatedResponse<ResolutionReport> generateResolutionReport(ReportFilter filter) {
        List<ResolutionReport> data  = repo.findResolutions(filter);
        long                   total = repo.countResolutions(filter);
        return new PaginatedResponse<>(data, total, filter.getPage(), filter.getSize());
    }

    /**
     * Generates the progress report using the specified filters.
     * Supports combining: group, status, dates, and report type.
     */
    public PaginatedResponse<ProgressReport> generateProgressReport(ReportFilter filter) {
        List<ProgressReport> data  = repo.findProgressReports(filter);
        long                 total = repo.countProgressReports(filter);
        return new PaginatedResponse<>(data, total, filter.getPage(), filter.getSize());
    }
}
