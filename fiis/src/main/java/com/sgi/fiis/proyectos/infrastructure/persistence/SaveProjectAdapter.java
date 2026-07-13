package com.sgi.fiis.proyectos.infrastructure.persistence;

import com.sgi.fiis.convocatorias.infrastructure.persistence.ResearchCallEntity;
import com.sgi.fiis.convocatorias.infrastructure.persistence.ResearchCallJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.GroupMembershipJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupJpaRepository;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineJpaRepository;
import com.sgi.fiis.proyectos.application.ports.out.SaveProjectPort;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectMember;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import com.sgi.fiis.shared.infrastructure.persistence.JsonbHelper;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class SaveProjectAdapter implements SaveProjectPort {

    private static final String STATUS_BORRADOR = "BORRADOR";
    private static final String STATUS_POSTULADO = "POSTULADO";
    private static final String STATUS_OBSERVADO = "OBSERVADO";
    private static final String STATUS_APROBADO = "APROBADO";
    private static final String STATUS_RECHAZADO = "RECHAZADO";
    private static final String STATUS_EN_EJECUCION = "EN_EJECUCION";
    private static final String STATUS_FINALIZADO = "FINALIZADO";

    private final ProjectJpaRepository projectRepository;
    private final ResearchLineJpaRepository lineRepository;
    private final ResearchGroupJpaRepository groupRepository;
    private final SpringDataUserRepository userRepository;
    private final ResearchCallJpaRepository callRepository;
    private final GroupMembershipJpaRepository membershipRepository;
    private final ProjectMemberJpaRepository projectMemberRepository;

    public SaveProjectAdapter(ProjectJpaRepository projectRepository,
                               ResearchLineJpaRepository lineRepository,
                               ResearchGroupJpaRepository groupRepository,
                               SpringDataUserRepository userRepository,
                              ResearchCallJpaRepository callRepository,
                              GroupMembershipJpaRepository membershipRepository,
                              ProjectMemberJpaRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.lineRepository = lineRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.callRepository = callRepository;
        this.membershipRepository = membershipRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = toEntity(project);
        ProjectEntity saved = projectRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Project> findByResponsibleId(Long responsibleId) {
        return projectRepository.findByResponsibleId(responsibleId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Project> findById(Integer id) {
        return projectRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Project> findByGroupId(Integer groupId) {
        return projectRepository.findByGroupId(groupId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<String> getGroupCode(Integer groupId) {
        return groupRepository.findById(groupId)
                .map(g -> g.getCode());
    }

    @Override
    public Optional<String> getLineName(Integer lineId) {
        return lineRepository.findById(lineId)
                .map(l -> l.getName());
    }

    @Override
    public boolean isUserMemberOfGroup(Long userId, Integer groupId) {
        // Query database to see if there is an active membership for user in research group
        return membershipRepository.findByUserIdAndGroupId(userId, groupId)
                .map(m -> m.getActive())
                .orElse(false);
    }

    @Override
    public boolean isGroupActive(Integer groupId) {
        return groupRepository.findById(groupId)
                .map(g -> g.isActive())
                .orElse(false);
    }

    @Override
    public void saveMembers(Integer projectId, List<ProjectMember> members) {
        projectMemberRepository.deleteByProjectId(projectId);
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        for (ProjectMember member : members) {
            UserEntity user = userRepository.findById(member.getUserId().longValue())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + member.getUserId()));

            ProjectMemberEntity entity = ProjectMemberEntity.builder()
                    .project(projectEntity)
                    .user(user)
                    .role(member.getRole() != null ? member.getRole() : "INVESTIGADOR")
                    .build();
            projectMemberRepository.save(entity);
        }
    }

    @Override
    public List<ProjectMember> findMembersByProjectId(Integer projectId) {
        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(e -> new ProjectMember(e.getId(), projectId, e.getUser().getId().intValue(), e.getRole()))
                .toList();
    }

    @Override
    public boolean isLineActive(Integer lineId) {
        return lineRepository.findById(lineId)
                .map(l -> l.isActive())
                .orElse(false);
    }

    @Override
    public List<Project> findByResponsibleIdAndStatus(Long responsibleId, ProjectStatus status) {
        String dbStatus = switch (status) {
            case DRAFT -> STATUS_BORRADOR;
            case POSTULATED -> STATUS_POSTULADO;
            case OBSERVED -> STATUS_OBSERVADO;
            case APPROVED -> STATUS_APROBADO;
            case REJECTED -> STATUS_RECHAZADO;
            case IN_PROGRESS -> STATUS_EN_EJECUCION;
            case COMPLETED -> STATUS_FINALIZADO;
        };
        return projectRepository.findByResponsibleIdAndStatus(responsibleId, dbStatus).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Integer projectId) {
        projectMemberRepository.deleteByProjectId(projectId);
        projectRepository.deleteById(projectId);
    }

    private ProjectEntity toEntity(Project domain) {
        ResearchLineEntity line = lineRepository.findById(domain.getResearchLineId())
                .orElseThrow(() -> new IllegalArgumentException("Research line not found with ID: " + domain.getResearchLineId()));

        ResearchGroupEntity group = groupRepository.findById(domain.getResearchGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Research group not found with ID: " + domain.getResearchGroupId()));

        UserEntity responsible = userRepository.findById(domain.getResponsibleId())
                .orElseThrow(() -> new IllegalArgumentException("Responsible user not found with ID: " + domain.getResponsibleId()));

        ResearchCallEntity call = null;
        if (domain.getCallId() != null) {
            call = callRepository.findById(domain.getCallId())
                    .orElseThrow(() -> new IllegalArgumentException("Research call not found with ID: " + domain.getCallId()));
        }

        String dbStatus = switch (domain.getStatus()) {
            case DRAFT -> STATUS_BORRADOR;
            case OBSERVED -> STATUS_OBSERVADO;
            case APPROVED -> STATUS_APROBADO;
            case REJECTED -> STATUS_RECHAZADO;
            case IN_PROGRESS -> STATUS_EN_EJECUCION;
            case COMPLETED -> STATUS_FINALIZADO;
            default -> STATUS_POSTULADO;
        };

        return ProjectEntity.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .title(domain.getTitle())
                .summary(domain.getSummary())
                .generalObjective(domain.getGeneralObjective())
                .titleJson(JsonbHelper.toJson(Map.of("es", domain.getTitle() != null ? domain.getTitle() : "")))
                .summaryJson(JsonbHelper.toJson(Map.of("es", domain.getSummary() != null ? domain.getSummary() : "")))
                .generalObjectiveJson(JsonbHelper.toJson(Map.of("es", domain.getGeneralObjective() != null ? domain.getGeneralObjective() : "")))
                .executionPlaceJson(JsonbHelper.toJson(Map.of("es", domain.getExecutionPlace() != null ? domain.getExecutionPlace() : "")))
                .researchLine(line)
                .group(group)
                .budget(domain.getBudget())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .executionPlace(domain.getExecutionPlace())
                .responsible(responsible)
                .researchCall(call)
                .documentId(domain.getDocumentId())
                .status(dbStatus)
                .build();
    }

    private Project toDomain(ProjectEntity entity) {
        ProjectStatus domainStatus = ProjectStatus.POSTULATED;
        if (STATUS_BORRADOR.equalsIgnoreCase(entity.getStatus())) {
            domainStatus = ProjectStatus.DRAFT;
        } else if (STATUS_OBSERVADO.equalsIgnoreCase(entity.getStatus())) {
            domainStatus = ProjectStatus.OBSERVED;
        } else if (STATUS_APROBADO.equalsIgnoreCase(entity.getStatus())) {
            domainStatus = ProjectStatus.APPROVED;
        } else if (STATUS_RECHAZADO.equalsIgnoreCase(entity.getStatus())) {
            domainStatus = ProjectStatus.REJECTED;
        } else if (STATUS_EN_EJECUCION.equalsIgnoreCase(entity.getStatus())) {
            domainStatus = ProjectStatus.IN_PROGRESS;
        } else if (STATUS_FINALIZADO.equalsIgnoreCase(entity.getStatus())) {
            domainStatus = ProjectStatus.COMPLETED;
        }

        return new Project(
                entity.getId(),
                entity.getCode(),
                JsonbHelper.getText(entity.getTitleJson(), "es"),
                JsonbHelper.getText(entity.getSummaryJson(), "es"),
                JsonbHelper.getText(entity.getGeneralObjectiveJson(), "es"),
                entity.getResearchLine().getId(),
                entity.getResearchLine().getName(),
                entity.getBudget(),
                entity.getStartDate(),
                entity.getEndDate(),
                JsonbHelper.getText(entity.getExecutionPlaceJson(), "es"),
                entity.getResponsible().getId(),
                entity.getGroup().getId(),
                entity.getGroup().getCode(),
                entity.getResearchCall() != null ? entity.getResearchCall().getId() : null,
                entity.getDocumentId(),
                domainStatus
        );
    }
}
