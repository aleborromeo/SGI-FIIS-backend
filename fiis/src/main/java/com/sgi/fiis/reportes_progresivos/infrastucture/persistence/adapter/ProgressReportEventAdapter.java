package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter;

import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportEventPort;
import com.sgi.fiis.tramites.application.dto.ProcedureRequestDto;
import com.sgi.fiis.tramites.application.usecase.CreateProcedureUseCase;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import org.springframework.stereotype.Component;

@Component
public class ProgressReportEventAdapter implements ProgressReportEventPort {

    private final CreateProcedureUseCase createProcedureUseCase;

    public ProgressReportEventAdapter(CreateProcedureUseCase createProcedureUseCase) {
        this.createProcedureUseCase = createProcedureUseCase;
    }

    @Override
    public void publishReportProcedure(Long reportId, Long projectId,
                                       Long requesterId, Long groupId) {
        ProcedureRequestDto dto = ProcedureRequestDto.builder()
                .procedureType(ProcedureType.REPORT_AVANCE)
                .applicantId(requesterId)
                .groupId(groupId)
                .projectReferenceId(projectId)
                .reportReferenceId(reportId)
                .build();

        createProcedureUseCase.execute(dto);
    }
}
