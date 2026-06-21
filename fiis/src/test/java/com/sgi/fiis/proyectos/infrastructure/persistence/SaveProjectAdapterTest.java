package com.sgi.fiis.proyectos.infrastructure.persistence;

import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupJpaRepository;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import com.sgi.fiis.convocatorias.infrastructure.persistence.ResearchCallJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.GroupMembershipJpaRepository;

import java.math.BigDecimal;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import com.sgi.fiis.convocatorias.infrastructure.persistence.ResearchCallEntity;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SaveProjectAdapterTest {

    private ProjectJpaRepository jpaRepository;
    private ResearchLineJpaRepository lineRepository;
    private ResearchGroupJpaRepository groupRepository;
    private SpringDataUserRepository userRepository;
    private ResearchCallJpaRepository callRepository;
    private GroupMembershipJpaRepository membershipRepository;
    private ProjectMemberJpaRepository projectMemberRepository;
    private SaveProjectAdapter adapter;

    private ProjectEntity createValidEntity(int id, String status) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(id);
        entity.setTitle("Test");
        entity.setStatus(status);
        entity.setBudget(new BigDecimal("100"));
        
        ResearchLineEntity line = new ResearchLineEntity();
        line.setId(1);
        line.setName("Line");
        entity.setResearchLine(line);
        
        UserEntity user = new UserEntity();
        user.setId(3L);
        entity.setResponsible(user);
        
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(2);
        group.setCode("GRP");
        entity.setGroup(group);
        
        ResearchCallEntity call = new ResearchCallEntity();
        call.setId(4);
        entity.setResearchCall(call);
        
        return entity;
    }

    @BeforeEach
    void setup() {
        jpaRepository = Mockito.mock(ProjectJpaRepository.class);
        lineRepository = Mockito.mock(ResearchLineJpaRepository.class);
        groupRepository = Mockito.mock(ResearchGroupJpaRepository.class);
        userRepository = Mockito.mock(SpringDataUserRepository.class);
        callRepository = Mockito.mock(ResearchCallJpaRepository.class);
        membershipRepository = Mockito.mock(GroupMembershipJpaRepository.class);
        projectMemberRepository = Mockito.mock(ProjectMemberJpaRepository.class);
        
        adapter = new SaveProjectAdapter(jpaRepository, lineRepository, groupRepository, userRepository, callRepository, membershipRepository, projectMemberRepository);
    }

    @Test
    void testSave() {
        Project project = Project.builder()
                .title("Test")
                .budget(new BigDecimal("100"))
                .status(ProjectStatus.POSTULATED)
                .researchLineId(1)
                .responsibleId(3L)
                .researchGroupId(2)
                .callId(4)
                .build();

        ProjectEntity entity = createValidEntity(1, "POSTULADO");

        when(lineRepository.findById(1)).thenReturn(Optional.of(new ResearchLineEntity()));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new UserEntity()));
        when(groupRepository.findById(2)).thenReturn(Optional.of(new ResearchGroupEntity()));
        when(callRepository.findById(4)).thenReturn(Optional.of(new ResearchCallEntity()));
        when(jpaRepository.save(any(ProjectEntity.class))).thenReturn(entity);

        Project saved = adapter.save(project);
        assertEquals(1, saved.getId());
        assertEquals(ProjectStatus.POSTULATED, saved.getStatus());
    }

    @Test
    void testFindById() {
        ProjectEntity entity = createValidEntity(1, "POSTULADO");

        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        Optional<Project> result = adapter.findById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testFindByResponsibleId() {
        ProjectEntity entity = createValidEntity(1, "OBSERVADO");
        when(jpaRepository.findByResponsibleId(3L)).thenReturn(Collections.singletonList(entity));

        List<Project> result = adapter.findByResponsibleId(3L);
        assertEquals(1, result.size());
        assertEquals(ProjectStatus.OBSERVED, result.get(0).getStatus());
    }

    @Test
    void testFindByGroupId() {
        ProjectEntity entity = createValidEntity(1, "APROBADO");
        when(jpaRepository.findByGroupId(2)).thenReturn(Collections.singletonList(entity));

        List<Project> result = adapter.findByGroupId(2);
        assertEquals(1, result.size());
        assertEquals(ProjectStatus.APPROVED, result.get(0).getStatus());
    }

    @Test
    void testFindAll() {
        ProjectEntity entity = createValidEntity(1, "EN_EJECUCION");
        when(jpaRepository.findAll()).thenReturn(Collections.singletonList(entity));

        List<Project> result = adapter.findAll();
        assertEquals(1, result.size());
        assertEquals(ProjectStatus.IN_PROGRESS, result.get(0).getStatus());
    }

    @Test
    void testDomainStatusMapping() {
        ProjectEntity entity = createValidEntity(1, "FINALIZADO");
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));
        assertEquals(ProjectStatus.COMPLETED, adapter.findById(1).get().getStatus());
        
        ProjectEntity entity2 = createValidEntity(2, "RECHAZADO");
        when(jpaRepository.findById(2)).thenReturn(Optional.of(entity2));
        assertEquals(ProjectStatus.REJECTED, adapter.findById(2).get().getStatus());
    }
}
