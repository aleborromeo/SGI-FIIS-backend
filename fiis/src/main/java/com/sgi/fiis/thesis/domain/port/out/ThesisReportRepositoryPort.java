package com.sgi.fiis.thesis.domain.port.out;

import java.util.List;
import java.util.Optional;
import com.sgi.fiis.thesis.domain.ThesisReport;

public interface ThesisReportRepositoryPort {
    ThesisReport save(ThesisReport thesisReport);
    Optional<ThesisReport> findById(Integer idInformeTesis);
    List<ThesisReport> findByPlanTesis(Integer idPlanTesis);
}
