package com.sgi.fiis.proyectos.infrastructure.adapters;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.proyectos.application.ports.out.CreateProcedurePort;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.tramites.infrastructure.persistence.*;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class ProjectProcedureAdapter implements CreateProcedurePort {

    private final ProjectJpaRepository projectRepository;
    private final SpringDataProcedureRepository procedureRepository;
    private final ProcedureMovementJpaRepository movementRepository;

    public ProjectProcedureAdapter(ProjectJpaRepository projectRepository,
                                   SpringDataProcedureRepository procedureRepository,
                                   ProcedureMovementJpaRepository movementRepository) {
        this.projectRepository = projectRepository;
        this.procedureRepository = procedureRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    @Transactional
    public void createPostulationProcedure(Project project) {
        ProjectEntity projectEntity = projectRepository.findById(project.getId())
                .orElseThrow(() -> new BusinessRuleValidationException("Project not found with ID: " + project.getId()));

        UserEntity applicant = projectEntity.getResponsible();
        ResearchGroupEntity group = projectEntity.getGroup();

        // 1. Generate unique procedure code (max 30 chars). Format: TRM-YYYY-[UUID-8]
        String generatedCode = "TRM-" + LocalDateTime.now(ZoneId.of("UTC")).getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Create the postulation procedure
        ProcedureEntity procedure = ProcedureEntity.builder()
                .code(generatedCode)
                .procedureType("PROYECTO")
                .applicant(applicant)
                .group(group)
                .status("PENDING_COORDINATOR")
                .reviewerRole("COORDINADOR_GRUPO")
                .sentAt(LocalDateTime.now(ZoneId.of("UTC")))
                .updatedAt(LocalDateTime.now(ZoneId.of("UTC")))
                .projectReference(projectEntity)
                .build();

        ProcedureEntity savedProcedure = procedureRepository.save(procedure);

        // 3. Log initial movement in movimientos_tramite
        ProcedureMovementEntity movement = ProcedureMovementEntity.builder()
                .procedure(savedProcedure)
                .actionUser(applicant)
                .action("CREAR")
                .previousState("REGISTERED")
                .newState("PENDING_COORDINATOR")
                .comment("PostulaciÃƒÂ³n de proyecto de investigaciÃƒÂ³n registrada automÃƒÂ¡ticamente.")
                .movementAt(LocalDateTime.now(ZoneId.of("UTC")))
                .build();

        movementRepository.save(movement);
    }
}
