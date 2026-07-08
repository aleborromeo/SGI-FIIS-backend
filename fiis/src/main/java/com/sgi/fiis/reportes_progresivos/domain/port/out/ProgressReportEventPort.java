package com.sgi.fiis.reportes_progresivos.domain.port.out;

/** Output port -- publishes a tramite when a progress report is created (RF-73, RF-40). */
public interface ProgressReportEventPort {

    void publishReportProcedure(Long reportId, Long projectId,
                                Long requesterId, Long groupId);
}
