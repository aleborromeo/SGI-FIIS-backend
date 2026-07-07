package com.sgi.fiis.proyectos.infrastructure.adapters;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.proyectos.application.ports.out.CreateProcedurePort;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.tramites.infrastructure.persistence.ProcedureEntity;
import com.sgi.fiis.tramites.infrastructure.persistence.ProcedureMovementEntity;
import com.sgi.fiis.tramites.infrastructure.persistence.ProcedureMovementJpaRepository;
import com.sgi.fiis.tramites.infrastructure.persistence.SpringDataProcedureRepository;
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

        String generatedCode = "TRM-" + LocalDateTime.now(ZoneId.of("UTC")).getYear()
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ProcedureEntity procedure = new ProcedureEntity();
        procedure.setCodigoTramite(generatedCode);
        procedure.setTipoTramite("PROYECTO");
        procedure.setIdSolicitante(applicant != null ? applicant.getId() : null);
        procedure.setIdGrupo(group != null ? group.getId().longValue() : null);
        procedure.setEstadoActual("PENDING_COORDINATOR");
        procedure.setRolRevisorActual("COORDINADOR_GRUPO");
        procedure.setFechaEnvio(LocalDateTime.now(ZoneId.of("UTC")));
        procedure.setFechaActualizacion(LocalDateTime.now(ZoneId.of("UTC")));
        procedure.setIdReferenciaProyecto(projectEntity.getId() != null
                ? projectEntity.getId().longValue() : null);

        ProcedureEntity savedProcedure = procedureRepository.save(procedure);

        ProcedureMovementEntity movement = new ProcedureMovementEntity();
        movement.setIdTramite(savedProcedure.getId());
        movement.setIdUsuarioAccion(applicant != null ? applicant.getId() : null);
        movement.setAccion("CREAR");
        movement.setEstadoAnterior("REGISTERED");
        movement.setEstadoNuevo("PENDING_COORDINATOR");
        movement.setObservacion("Postulación de proyecto de investigación registrada automáticamente.");
        movement.setFechaMovimiento(LocalDateTime.now(ZoneId.of("UTC")));

        movementRepository.save(movement);
    }
}
