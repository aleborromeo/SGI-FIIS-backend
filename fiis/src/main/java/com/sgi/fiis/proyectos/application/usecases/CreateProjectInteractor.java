package com.sgi.fiis.proyectos.application.usecases;

import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.MemberRequest;
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

    // Trigger rebuild to resolve compilation in JDT LS
    private static final String PROJECT_NOT_FOUND = "proyectos.error.project-not-found";

    private final SaveProjectPort saveProjectPort;
    private final SaveCallPort saveCallPort;
    private final CreateProcedurePort createProcedurePort;
    private Clock clock = Clock.systemDefaultZone();

    public CreateProjectInteractor(SaveProjectPort saveProjectPort,
            SaveCallPort saveCallPort,
            CreateProcedurePort createProcedurePort) {
        this.saveProjectPort = saveProjectPort;
        this.saveCallPort = saveCallPort;
        this.createProcedurePort = createProcedurePort;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    @Transactional
    @Auditable(action = "CREATE_PROJECT")
    public ProjectResponse execute(CreateProjectRequest request) {
        if (!request.isDraft()) {
            validateRequiredFields(request);
        }
        validateGroupAndLine(request);

        String groupCode = saveProjectPort.getGroupCode(request.getResearchGroupId())
                .orElseThrow(() -> new BusinessRuleValidationException("proyectos.error.group-code-not-found"));
        String lineName = saveProjectPort.getLineName(request.getResearchLineId())
                .orElseThrow(() -> new BusinessRuleValidationException("proyectos.error.line-name-not-found"));

        if (request.isDraft()) {
            return createDraftProject(request, groupCode, lineName);
        }
        return submitProject(request, groupCode, lineName);
    }

    private void validateGroupAndLine(CreateProjectRequest request) {
        if (request.getResearchGroupId() != null && !saveProjectPort.isGroupActive(request.getResearchGroupId())) {
            throw new BusinessRuleValidationException("proyectos.error.group-not-active");
        }
        if (request.getResponsibleId() != null && request.getResearchGroupId() != null
                && !saveProjectPort.isUserMemberOfGroup(request.getResponsibleId().longValue(),
                request.getResearchGroupId())) {
            throw new BusinessRuleValidationException("proyectos.error.responsible-not-member");
        }
        if (request.getResearchLineId() != null && !saveProjectPort.isLineActive(request.getResearchLineId())) {
            throw new BusinessRuleValidationException("proyectos.error.line-not-active");
        }
    }

    private void validateRequiredFields(CreateProjectRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessRuleValidationException("El título es obligatorio.");
        }
        if (request.getSummary() == null || request.getSummary().isBlank()) {
            throw new BusinessRuleValidationException("El resumen es obligatorio.");
        }
        if (request.getGeneralObjective() == null || request.getGeneralObjective().isBlank()) {
            throw new BusinessRuleValidationException("El objetivo general es obligatorio.");
        }
        if (request.getResearchLineId() == null) {
            throw new BusinessRuleValidationException("La línea de investigación es obligatoria.");
        }
        if (request.getBudget() == null) {
            throw new BusinessRuleValidationException("El presupuesto es obligatorio.");
        }
        if (request.getStartDate() == null) {
            throw new BusinessRuleValidationException("La fecha de inicio es obligatoria.");
        }
        if (request.getEndDate() == null) {
            throw new BusinessRuleValidationException("La fecha de fin es obligatoria.");
        }
        if (request.getExecutionPlace() == null || request.getExecutionPlace().isBlank()) {
            throw new BusinessRuleValidationException("El lugar de ejecución es obligatorio.");
        }
        if (request.getResearchGroupId() == null) {
            throw new BusinessRuleValidationException("El grupo de investigación es obligatorio.");
        }
        if (request.getCallId() == null) {
            throw new BusinessRuleValidationException("La convocatoria es obligatoria.");
        }
    }

    private ProjectResponse createDraftProject(CreateProjectRequest request, String groupCode, String lineName) {
        String tempCode = "BOR-" + LocalDate.now(clock).getYear() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ResearchCall call = null;
        if (request.getCallId() != null) {
            call = saveCallPort.findById(request.getCallId()).orElse(null);
        }

        Project project = new Project(
                null, tempCode, request.getTitle(), request.getSummary(),
                request.getGeneralObjective(), request.getResearchLineId(), lineName,
                request.getBudget(), request.getStartDate(), request.getEndDate(),
                request.getExecutionPlace(), request.getResponsibleId().longValue(),
                request.getResearchGroupId(), groupCode,
                call != null ? call.getId() : null,
                request.getDocumentId(), ProjectStatus.DRAFT);

        Project savedProject = saveProjectPort.save(project);
        saveMembersIfNeeded(request.getMembers(), savedProject.getId());
        return mapToResponse(savedProject);
    }

    private ProjectResponse submitProject(CreateProjectRequest request, String groupCode, String lineName) {
        ResearchCall call = getAndValidateCall(request.getCallId());
        request.setCallId(call.getId());

        String generatedCode = "PRJ-" + LocalDate.now(clock).getYear() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Project project = new Project(
                null, generatedCode, request.getTitle(), request.getSummary(),
                request.getGeneralObjective(), request.getResearchLineId(), lineName,
                request.getBudget(), request.getStartDate(), request.getEndDate(),
                request.getExecutionPlace(), request.getResponsibleId().longValue(),
                request.getResearchGroupId(), groupCode,
                request.getCallId(), request.getDocumentId(), ProjectStatus.POSTULATED);

        project.validateInvariants();
        Project savedProject = saveProjectPort.save(project);
        createProcedurePort.createPostulationProcedure(savedProject);
        saveMembersIfNeeded(request.getMembers(), savedProject.getId());
        return mapToResponse(savedProject);
    }

    private void saveMembersIfNeeded(List<MemberRequest> members, Integer projectId) {
        if (members == null || members.isEmpty()) {
            return;
        }
        List<ProjectMember> projectMembers = members.stream()
                .map(m -> new ProjectMember(null, projectId, m.getUserId(),
                        m.getRole() != null ? m.getRole() : "INVESTIGADOR"))
                .toList();
        saveProjectPort.saveMembers(projectId, projectMembers);
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
                .orElseThrow(() -> new BusinessRuleValidationException(PROJECT_NOT_FOUND, id));

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
        if ("BORRADOR".equalsIgnoreCase(status))
            return ProjectStatus.DRAFT;
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
                .orElseThrow(() -> new BusinessRuleValidationException(PROJECT_NOT_FOUND, id));
    }

    @Override
    public List<ProjectResponse> getDraftsByResponsible(Long responsibleId) {
        return saveProjectPort.findByResponsibleIdAndStatus(responsibleId, ProjectStatus.DRAFT).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    @Auditable(action = "DELETE_DRAFT")
    public void deleteDraft(Integer projectId, Long userId) {
        Project project = saveProjectPort.findById(projectId)
                .orElseThrow(() -> new BusinessRuleValidationException(PROJECT_NOT_FOUND, projectId));
        if (project.getStatus() != ProjectStatus.DRAFT) {
            throw new BusinessRuleValidationException("Solo se pueden eliminar proyectos en estado BORRADOR");
        }
        if (!project.getResponsibleId().equals(userId)) {
            throw new BusinessRuleValidationException("No tiene permisos para eliminar este borrador");
        }
        saveProjectPort.deleteById(projectId);
    }

    private ProjectResponse mapToResponse(Project project) {
        String dbStatus = switch (project.getStatus()) {
            case DRAFT -> "BORRADOR";
            case OBSERVED -> "OBSERVADO";
            case APPROVED -> "APROBADO";
            case REJECTED -> "RECHAZADO";
            case IN_PROGRESS -> "EN_EJECUCION";
            case COMPLETED -> "FINALIZADO";
            default -> "POSTULADO";
        };

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

    private ResearchCall getAndValidateCall(Integer callId) {
        ResearchCall call = null;
        if (callId != null) {
            call = saveCallPort.findById(callId)
                    .orElseThrow(() -> new BusinessRuleValidationException("proyectos.error.call-not-found", callId));
        } else {
            List<ResearchCall> openCalls = saveCallPort.findByStatus(CallStatus.OPEN);
            if (openCalls.isEmpty()) {
                throw new BusinessRuleValidationException("No existe ninguna convocatoria en estado ABIERTA");
            }
            LocalDate today = LocalDate.now(clock);
            for (ResearchCall c : openCalls) {
                try {
                    c.validateCanSubmitProject(today);
                    call = c;
                    break;
                } catch (BusinessRuleValidationException e) {
                    // Check next
                }
            }
            if (call == null) {
                throw new BusinessRuleValidationException("No existe ninguna convocatoria abierta dentro del rango de fechas permitido");
            }
        }
        call.validateCanSubmitProject(LocalDate.now(clock));
        return call;
    }
}
