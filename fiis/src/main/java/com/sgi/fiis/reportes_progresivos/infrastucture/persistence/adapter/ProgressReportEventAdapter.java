package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter;

import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportEventPort;
import org.springframework.stereotype.Component;

/**
 * Temporary implementation of ProgressReportEventPort.
 * Once the tramites module service is available, it can be invoked here.
 */
@Component
public class ProgressReportEventAdapter implements ProgressReportEventPort {

    @Override
    public void publishReportProcedure(Long reportId, Long projectId,
                                       Long requesterId, Long groupId) {
        // Once the tramites module service is available, it can be invoked here.
    }
}
