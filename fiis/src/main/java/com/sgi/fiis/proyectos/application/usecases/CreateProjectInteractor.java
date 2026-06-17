package com.sgi.fiis.proyectos.application.usecases;

import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import com.sgi.fiis.proyectos.application.ports.in.CreateProjectUseCase;
import com.sgi.fiis.proyectos.application.ports.out.CreateProcedurePort;
import com.sgi.fiis.proyectos.application.ports.out.SaveProjectPort;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
public class CreateProjectInteractor implements CreateProjectUseCase {

    private final SaveProjectPort saveProjectPort;
    private final SaveCallPort saveCallPort;
    private final CreateProcedurePort createProcedurePort;

    public CreateProjectInteractor(SaveProjectPort saveProjectPort,
                                   SaveCallPort saveCallPort,
                                   CreateProcedurePort createProcedurePort) {
        this.saveProjectPort = saveProjectPort;
        this.saveCallPort = saveCallPort;
        this.createProcedurePort = createProcedurePort;
    }

    @Override
    @Transactional
    @Auditable(action = "CREATE_PROJECT")
    public ProjectResponse execute(CreateProjectRequest request) {
        // 1. Validate Research Group exists and is active
        if (!saveProjectPort.isGroupActive(request.getResearchGroupId())) {
            throw new BusinessRuleValidationException("Research group is not active or does not exist.");
        }

        // 2. Validate responsible teacher is an active member of the research group (RN-02)
        if (!saveProjectPort.isUserMemberOfGroup(request.getResponsibleId().longValue(), request.getResearchGroupId())) {
            throw new BusinessRuleValidationException("Responsible teacher is not an active member of the selected research group.");
        }

        // 3. Validate Research Line exists and is active (RN-11)
        if (!saveProjectPort.isLineActive(request.getResearchLineId())) {
            throw new BusinessRuleValidationException("Research line is not active or does not exist.");
        }

        // 4. Fetch metadata: Group Code and Line Name
        String groupCode = saveProjectPort.getGroupCode(request.getResearchGroupId())
                .orElseThrow(() -> new BusinessRuleValidationException("Group code not found."));
        String lineName = saveProjectPort.getLineName(request.getResearchLineId())
                .orElseThrow(() -> new BusinessRuleValidationException("Research line name not found."));

        // 5. If linked to a call, fetch call and validate it (RF-33 & RF-34)
        if (request.getCallId() != null) {
            ResearchCall call = saveCallPort.findById(request.getCallId())
                    .orElseThrow(() -> new BusinessRuleValidationException("Research call not found with ID: " + request.getCallId()));
            
            // Validate that call is open and current date is within range
            call.validateCanSubmitProject(LocalDate.now());
        }

        // 6. Generate unique formatted project code: PRJ-YYYY-[UUID-8]
        String generatedCode = "PRJ-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 7. Create domain model
        Project project = new Project(
                null,
                generatedCode,
                request.getTitle(),
                request.getSummary(),
                request.getGeneralObjective(),
                request.getResearchLineId(),
                lineName,
                request.getBudget(),
                request.getStartDate(),
                request.getEndDate(),
                request.getExecutionPlace(),
                request.getResponsibleId().longValue(),
                request.getResearchGroupId(),
                groupCode,
                request.getCallId(),
                request.getDocumentId(),
                ProjectStatus.POSTULATED
        );

        // 8. Validate domain invariants (budget > 0, dates order, and GINSOFT line consistency RN-12)
        project.validateInvariants();

        // 9. Save project
        Project savedProject = saveProjectPort.save(project);

        // 10. Trigger procedure workflow (RF-40 & RF-41)
        createProcedurePort.createPostulationProcedure(savedProject);

        return mapToResponse(savedProject);
    }

    @Override
    public List<ProjectResponse> getProjectsByResponsible(Long responsibleId) {
        return saveProjectPort.findByResponsibleId(responsibleId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProjectResponse> getProjectsByGroup(Integer groupId) {
        return saveProjectPort.findByGroupId(groupId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProjectResponse> getAllProjects() {
        return saveProjectPort.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProjectResponse getProjectById(Integer id) {
        return saveProjectPort.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new BusinessRuleValidationException("Project not found with ID: " + id));
    }

    private ProjectResponse mapToResponse(Project project) {
        String dbStatus = "POSTULADO";
        if (project.getStatus() == ProjectStatus.OBSERVED) {
            dbStatus = "OBSERVADO";
        } else if (project.getStatus() == ProjectStatus.APPROVED) {
            dbStatus = "APROBADO";
        } else if (project.getStatus() == ProjectStatus.REJECTED) {
            dbStatus = "RECHAZADO";
        } else if (project.getStatus() == ProjectStatus.IN_PROGRESS) {
            dbStatus = "EN_EJECUCION";
        } else if (project.getStatus() == ProjectStatus.COMPLETED) {
            dbStatus = "FINALIZADO";
        }

        return new ProjectResponse(
                project.getId(),
                project.getCode(),
                project.getTitle(),
                project.getSummary(),
                project.getGeneralObjective(),
                project.getResearchLineId(),
                project.getResearchLineName(),
                project.getBudget(),
                project.getStartDate(),
                project.getEndDate(),
                project.getExecutionPlace(),
                project.getResponsibleId(),
                project.getResearchGroupId(),
                project.getResearchGroupCode(),
                project.getCallId(),
                project.getDocumentId(),
                dbStatus
        );
    }
}
