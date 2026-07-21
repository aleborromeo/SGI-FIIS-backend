package com.sgi.fiis.reports.domain.repository;

import com.sgi.fiis.reports.domain.model.*;

import java.util.List;

/**
 * Contract for the institutional reports repository.
 * Defined in domain to apply Dependency Inversion.
 */
public interface ReportRepositoryPort {

    List<ProjectReport>    findProjects(ReportFilter filter);
    long                   countProjects(ReportFilter filter);

    List<ProcedureReport>  findProcedures(ReportFilter filter);
    long                   countProcedures(ReportFilter filter);

    List<ResolutionReport> findResolutions(ReportFilter filter);
    long                   countResolutions(ReportFilter filter);

    List<ProgressReport>   findProgressReports(ReportFilter filter);
    long                   countProgressReports(ReportFilter filter);
}
