package com.sgi.fiis.proyectos.application.usecases;

import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import com.sgi.fiis.proyectos.application.ports.in.CreateProjectUseCase;
import com.sgi.fiis.proyectos.application.ports.out.CreateProcedurePort;
import com.sgi.fiis.proyectos.application.ports.out.SaveProjectPort;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectMember;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CreateProjectInteractor implements CreateProjectUseCase {

    private final SaveProjectPort saveProjectPort;
    private final SaveCallPort saveCallPort;
    private final CreateProcedurePort createProcedurePort;
    private Clock clock;

    public CreateProjectInteractor(SaveProjectPort saveProjectPort,
            SaveCallPort saveCallPort,
            CreateProcedurePort createProcedurePort) {
        this.saveProjectPort = saveProjectPort;
        this.saveCallPort = saveCallPort;
        this.createProcedurePort = createProcedurePort;
        this.clock = Clock.systemUTC();
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    @Transactional
    @Auditable(action = "CREATE_PROJECT")
    public ProjectResponse execute(CreateProjectRequest request) {
        // 1. Validate Research Group exists and is active
        if (!saveProjectPort.isGroupActive(request.getResearchGroupId())) {
            throw new BusinessRuleValidationException("proyectos.error.group-not-active");
        }

        // 2. Validate responsible teacher is an active member of the research group
        // (RN-02)
        if (!saveProjectPort.isUserMemberOfGroup(request.getResponsibleId().longValue(),
                request.getResearchGroupId())) {
            throw new BusinessRuleValidationException("proyectos.error.responsible-not-member");
        }

        // 3. Validate Research Line exists and is active (RN-11)
        if (!saveProjectPort.isLineActive(request.getResearchLineId())) {
            throw new BusinessRuleValidationException("proyectos.error.line-not-active");
        }

        // 4. Fetch metadata: Group Code and Line Name
        String groupCode = saveProjectPort.getGroupCode(request.getResearchGroupId())
                .orElseThrow(() -> new BusinessRuleValidationException("proyectos.error.group-code-not-found"));
        String lineName = saveProjectPort.getLineName(request.getResearchLineId())
                .orElseThrow(() -> new BusinessRuleValidationException("proyectos.error.line-name-not-found"));

        // 5. If linked to a call, fetch call and validate it (RF-33 & RF-34)
        if (request.getCallId() != null) {
            ResearchCall call = saveCallPort.findById(request.getCallId())
                    .orElseThrow(() -> new BusinessRuleValidationException(
                            "proyectos.error.call-not-found", request.getCallId()));

            // Validate that call is open and current date is within range
            call.validateCanSubmitProject(LocalDate.now(clock));
        }

        // 6. Generate unique formatted project code: PRJ-YYYY-[UUID-8]
        String generatedCode = "PRJ-" + LocalDate.now(clock).getYear() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

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
                ProjectStatus.POSTULATED);

        // 8. Validate domain invariants (budget > 0, dates order, and GINSOFT line
        // consistency RN-12)
        project.validateInvariants();

        // 9. Save project
        Project savedProject = saveProjectPort.save(project);

        // 10. Trigger procedure workflow (RF-40 & RF-41)
        createProcedurePort.createPostulationProcedure(savedProject);

        // 11. Save project team members if provided (RF-36)
        if (request.getMembers() != null && !request.getMembers().isEmpty()) {
            List<ProjectMember> members = request.getMembers().stream()
                    .map(m -> new ProjectMember(null, savedProject.getId(), m.getUserId(),
                            m.getRole() != null ? m.getRole() : "INVESTIGADOR"))
                    .toList();
            saveProjectPort.saveMembers(savedProject.getId(), members);
        }

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
    @Transactional
    @Auditable(action = "UPDATE_PROJECT_STATUS")
    public ProjectResponse updateStatus(Integer id, String status) {
        Project project = saveProjectPort.findById(id)
                .orElseThrow(() -> new BusinessRuleValidationException("proyectos.error.project-not-found", id));

        ProjectStatus newStatus;
        try {
            newStatus = mapStatusFromString(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleValidationException("proyectos.error.invalid-status-value", status);
        }

        project.setStatus(newStatus);
        Project updatedProject = saveProjectPort.save(project);
        return mapToResponse(updatedProject);
    }

    private ProjectStatus mapStatusFromString(String status) {
        if ("POSTULADO".equalsIgnoreCase(status))
            return ProjectStatus.POSTULATED;
        if ("OBSERVADO".equalsIgnoreCase(status))
            return ProjectStatus.OBSERVED;
        if ("APROBADO".equalsIgnoreCase(status))
            return ProjectStatus.APPROVED;
        if ("RECHAZADO".equalsIgnoreCase(status))
            return ProjectStatus.REJECTED;
        if ("EN_EJECUCION".equalsIgnoreCase(status))
            return ProjectStatus.IN_PROGRESS;
        if ("FINALIZADO".equalsIgnoreCase(status))
            return ProjectStatus.COMPLETED;
        return ProjectStatus.valueOf(status.toUpperCase());
    }

    @Override
    public ProjectResponse getProjectById(Integer id) {
        return saveProjectPort.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new BusinessRuleValidationException("proyectos.error.project-not-found", id));
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

        List<com.sgi.fiis.proyectos.application.dto.MemberResponse> members = null;
        if (project.getId() != null) {
            members = saveProjectPort.findMembersByProjectId(project.getId()).stream()
                    .map(m -> new com.sgi.fiis.proyectos.application.dto.MemberResponse(m.getId(), m.getUserId(),
                            m.getRole()))
                    .toList();
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
                dbStatus,
                members);
    }
}
