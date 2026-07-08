package com.sgi.fiis.proyectos.infrastructure.persistence;

import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectMember;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupJpaRepository;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import com.sgi.fiis.convocatorias.infrastructure.persistence.ResearchCallJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.GroupMembershipJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.GroupMembershipEntity;

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
        line.setLineName("Line");
        entity.setResearchLine(line);
        
        UserEntity user = new UserEntity();
        user.setId(3L);
        entity.setResponsible(user);
        
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(2);
        group.setGroupCode("GRP");
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
    void testSave_ThrowsExceptionsForMissingEntities() {
        Project project = Project.builder()
                .researchLineId(99)
                .responsibleId(3L)
                .researchGroupId(2)
                .build();
        
        when(lineRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> adapter.save(project));

        when(lineRepository.findById(99)).thenReturn(Optional.of(new ResearchLineEntity()));
        when(groupRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> adapter.save(project));

        when(groupRepository.findById(2)).thenReturn(Optional.of(new ResearchGroupEntity()));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> adapter.save(project));

        when(userRepository.findById(3L)).thenReturn(Optional.of(new UserEntity()));
        project.setCallId(99);
        when(callRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> adapter.save(project));
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

    @Test
    void testGetGroupCode() {
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setGroupCode("CODE");
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        when(groupRepository.findById(2)).thenReturn(Optional.empty());
        
        assertEquals("CODE", adapter.getGroupCode(1).orElse(null));
        assertFalse(adapter.getGroupCode(2).isPresent());
    }

    @Test
    void testGetLineName() {
        ResearchLineEntity line = new ResearchLineEntity();
        line.setLineName("LNAME");
        when(lineRepository.findById(1)).thenReturn(Optional.of(line));
        
        assertEquals("LNAME", adapter.getLineName(1).orElse(null));
        assertFalse(adapter.getLineName(2).isPresent());
    }

    @Test
    void testIsUserMemberOfGroup() {
        GroupMembershipEntity membership = new GroupMembershipEntity();
        membership.setActive(true);
        when(membershipRepository.findByUserIdAndGroupId(1L, 1)).thenReturn(Optional.of(membership));
        when(membershipRepository.findByUserIdAndGroupId(2L, 1)).thenReturn(Optional.empty());

        assertTrue(adapter.isUserMemberOfGroup(1L, 1));
        assertFalse(adapter.isUserMemberOfGroup(2L, 1));
    }

    @Test
    void testIsGroupActive() {
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setActive(true);
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        assertTrue(adapter.isGroupActive(1));
        assertFalse(adapter.isGroupActive(2));
    }

    @Test
    void testIsLineActive() {
        ResearchLineEntity line = new ResearchLineEntity();
        line.setActive(true);
        when(lineRepository.findById(1)).thenReturn(Optional.of(line));

        assertTrue(adapter.isLineActive(1));
        assertFalse(adapter.isLineActive(2));
    }

    @Test
    void testSaveMembers() {
        ProjectEntity project = new ProjectEntity();
        when(jpaRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new UserEntity()));
        
        List<ProjectMember> members = List.of(new ProjectMember(null, 1, 3, "INV"));
        adapter.saveMembers(1, members);
        
        verify(projectMemberRepository).deleteByProjectId(1);
        verify(projectMemberRepository).save(any(ProjectMemberEntity.class));
    }

    @Test
    void testSaveMembers_ThrowsException() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());
        List<ProjectMember> members = List.of();
        assertThrows(IllegalArgumentException.class, () -> adapter.saveMembers(99, members));

        when(jpaRepository.findById(1)).thenReturn(Optional.of(new ProjectEntity()));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        List<ProjectMember> invalidMembers = List.of(new ProjectMember(null, 1, 99, "INV"));
        assertThrows(IllegalArgumentException.class, () -> adapter.saveMembers(1, invalidMembers));
    }

    @Test
    void testFindMembersByProjectId() {
        ProjectMemberEntity pme = new ProjectMemberEntity();
        pme.setId(1);
        pme.setRole("INV");
        UserEntity u = new UserEntity();
        u.setId(3L);
        pme.setUser(u);
        
        when(projectMemberRepository.findByProjectId(1)).thenReturn(List.of(pme));
        
        List<ProjectMember> res = adapter.findMembersByProjectId(1);
        assertEquals(1, res.size());
        assertEquals("INV", res.get(0).getRole());
        assertEquals(3, res.get(0).getUserId());
    }
}
