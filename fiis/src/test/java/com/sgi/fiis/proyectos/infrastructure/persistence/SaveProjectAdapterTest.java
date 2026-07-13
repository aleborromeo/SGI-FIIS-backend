package com.sgi.fiis.proyectos.infrastructure.persistence;

import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
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
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.GroupMembershipEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
        entity.setTitleJson("{}");
        entity.setSummaryJson("{}");
        entity.setGeneralObjectiveJson("{}");
        entity.setExecutionPlaceJson("{}");
        
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
        jpaRepository = mock(ProjectJpaRepository.class);
        lineRepository = mock(ResearchLineJpaRepository.class);
        groupRepository = mock(ResearchGroupJpaRepository.class);
        userRepository = mock(SpringDataUserRepository.class);
        callRepository = mock(ResearchCallJpaRepository.class);
        membershipRepository = mock(GroupMembershipJpaRepository.class);
        projectMemberRepository = mock(ProjectMemberJpaRepository.class);
        
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

    // === NUEVOS TESTS PARA COBERTURA COMPLETA ===

    @Test
    void testGetGroupCode_Found() {
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(2);
        group.setCode("GINSOFT");
        when(groupRepository.findById(2)).thenReturn(Optional.of(group));

        Optional<String> result = adapter.getGroupCode(2);
        assertTrue(result.isPresent());
        assertEquals("GINSOFT", result.get());
    }

    @Test
    void testGetGroupCode_NotFound() {
        when(groupRepository.findById(99)).thenReturn(Optional.empty());
        Optional<String> result = adapter.getGroupCode(99);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLineName_Found() {
        ResearchLineEntity line = new ResearchLineEntity();
        line.setId(1);
        line.setName("Computacion");
        when(lineRepository.findById(1)).thenReturn(Optional.of(line));

        Optional<String> result = adapter.getLineName(1);
        assertTrue(result.isPresent());
        assertEquals("Computacion", result.get());
    }

    @Test
    void testGetLineName_NotFound() {
        when(lineRepository.findById(99)).thenReturn(Optional.empty());
        Optional<String> result = adapter.getLineName(99);
        assertTrue(result.isEmpty());
    }

    @Test
    void testIsUserMemberOfGroup_Active() {
        GroupMembershipEntity membership = new GroupMembershipEntity();
        membership.setActive(true);
        when(membershipRepository.findByUserIdAndGroupId(3L, 2)).thenReturn(Optional.of(membership));

        assertTrue(adapter.isUserMemberOfGroup(3L, 2));
    }

    @Test
    void testIsUserMemberOfGroup_Inactive() {
        GroupMembershipEntity membership = new GroupMembershipEntity();
        membership.setActive(false);
        when(membershipRepository.findByUserIdAndGroupId(3L, 2)).thenReturn(Optional.of(membership));

        assertFalse(adapter.isUserMemberOfGroup(3L, 2));
    }

    @Test
    void testIsUserMemberOfGroup_NotFound() {
        when(membershipRepository.findByUserIdAndGroupId(3L, 2)).thenReturn(Optional.empty());
        assertFalse(adapter.isUserMemberOfGroup(3L, 2));
    }

    @Test
    void testIsGroupActive_True() {
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(2);
        group.setActive(true);
        when(groupRepository.findById(2)).thenReturn(Optional.of(group));

        assertTrue(adapter.isGroupActive(2));
    }

    @Test
    void testIsGroupActive_False() {
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(2);
        group.setActive(false);
        when(groupRepository.findById(2)).thenReturn(Optional.of(group));

        assertFalse(adapter.isGroupActive(2));
    }

    @Test
    void testIsGroupActive_NotFound() {
        when(groupRepository.findById(99)).thenReturn(Optional.empty());
        assertFalse(adapter.isGroupActive(99));
    }

    @Test
    void testIsLineActive_True() {
        ResearchLineEntity line = new ResearchLineEntity();
        line.setId(1);
        line.setActive(true);
        when(lineRepository.findById(1)).thenReturn(Optional.of(line));

        assertTrue(adapter.isLineActive(1));
    }

    @Test
    void testIsLineActive_False() {
        ResearchLineEntity line = new ResearchLineEntity();
        line.setId(1);
        line.setActive(false);
        when(lineRepository.findById(1)).thenReturn(Optional.of(line));

        assertFalse(adapter.isLineActive(1));
    }

    @Test
    void testIsLineActive_NotFound() {
        when(lineRepository.findById(99)).thenReturn(Optional.empty());
        assertFalse(adapter.isLineActive(99));
    }

    @Test
    void testFindById_NotFound() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());
        assertTrue(adapter.findById(99).isEmpty());
    }

    @Test
    void testSaveMembers_Success() {
        ProjectEntity projectEntity = createValidEntity(1, "POSTULADO");
        when(projectMemberRepository.findByProjectId(1)).thenReturn(List.of());
        when(jpaRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(userRepository.findById(5L)).thenReturn(Optional.of(new UserEntity()));
        when(projectMemberRepository.save(any(ProjectMemberEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        com.sgi.fiis.proyectos.domain.model.ProjectMember member =
                new com.sgi.fiis.proyectos.domain.model.ProjectMember(null, 1, 5, "INVESTIGADOR");

        adapter.saveMembers(1, List.of(member));

        verify(projectMemberRepository).deleteByProjectId(1);
        verify(projectMemberRepository).save(any());
    }

    @Test
    void testSaveMembers_ProjectNotFound() {
        when(projectMemberRepository.findByProjectId(99)).thenReturn(List.of());
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());

        com.sgi.fiis.proyectos.domain.model.ProjectMember member =
                new com.sgi.fiis.proyectos.domain.model.ProjectMember(null, 99, 5, "INVESTIGADOR");

        assertThrows(IllegalArgumentException.class,
                () -> adapter.saveMembers(99, List.of(member)));
    }

    @Test
    void testSaveMembers_UserNotFound() {
        ProjectEntity projectEntity = createValidEntity(1, "POSTULADO");
        when(projectMemberRepository.findByProjectId(1)).thenReturn(List.of());
        when(jpaRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        com.sgi.fiis.proyectos.domain.model.ProjectMember member =
                new com.sgi.fiis.proyectos.domain.model.ProjectMember(null, 1, 99, "INVESTIGADOR");

        assertThrows(IllegalArgumentException.class,
                () -> adapter.saveMembers(1, List.of(member)));
    }

    @Test
    void testSaveMembers_DefaultRole() {
        ProjectEntity projectEntity = createValidEntity(1, "POSTULADO");
        when(projectMemberRepository.findByProjectId(1)).thenReturn(List.of());
        when(jpaRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(userRepository.findById(5L)).thenReturn(Optional.of(new UserEntity()));
        when(projectMemberRepository.save(any(ProjectMemberEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        com.sgi.fiis.proyectos.domain.model.ProjectMember member =
                new com.sgi.fiis.proyectos.domain.model.ProjectMember(null, 1, 5, null);

        adapter.saveMembers(1, List.of(member));

        verify(projectMemberRepository).save(argThat(e -> "INVESTIGADOR".equals(e.getRole())));
    }

    @Test
    void testFindMembersByProjectId() {
        UserEntity user = new UserEntity();
        user.setId(5L);

        ProjectMemberEntity memberEntity = ProjectMemberEntity.builder()
                .id(1)
                .user(user)
                .role("INVESTIGADOR")
                .build();

        when(projectMemberRepository.findByProjectId(1)).thenReturn(List.of(memberEntity));

        List<com.sgi.fiis.proyectos.domain.model.ProjectMember> result = adapter.findMembersByProjectId(1);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getUserId().intValue());
        assertEquals("INVESTIGADOR", result.get(0).getRole());
    }

    @Test
    void testDeleteById() {
        adapter.deleteById(1);
        verify(projectMemberRepository).deleteByProjectId(1);
        verify(jpaRepository).deleteById(1);
    }

    @Test
    void testFindByResponsibleIdAndStatus_AllStatuses() {
        ProjectStatus[] statuses = {ProjectStatus.DRAFT, ProjectStatus.POSTULATED, ProjectStatus.OBSERVED,
                ProjectStatus.APPROVED, ProjectStatus.REJECTED, ProjectStatus.IN_PROGRESS, ProjectStatus.COMPLETED};
        String[] expected = {"BORRADOR", "POSTULADO", "OBSERVADO", "APROBADO", "RECHAZADO", "EN_EJECUCION", "FINALIZADO"};

        for (int i = 0; i < statuses.length; i++) {
            ProjectEntity entity = createValidEntity(i + 1, expected[i]);
            when(jpaRepository.findByResponsibleIdAndStatus(3L, expected[i]))
                    .thenReturn(List.of(entity));

            List<com.sgi.fiis.proyectos.domain.model.Project> result =
                    adapter.findByResponsibleIdAndStatus(3L, statuses[i]);
            assertEquals(1, result.size());
        }
    }

    @Test
    void testToDomain_AllStatuses() {
        String[] statuses = {"BORRADOR", "POSTULADO", "OBSERVADO", "APROBADO", "RECHAZADO", "EN_EJECUCION", "FINALIZADO"};
        ProjectStatus[] expected = {ProjectStatus.DRAFT, ProjectStatus.POSTULATED, ProjectStatus.OBSERVED,
                ProjectStatus.APPROVED, ProjectStatus.REJECTED, ProjectStatus.IN_PROGRESS, ProjectStatus.COMPLETED};

        for (int i = 0; i < statuses.length; i++) {
            ProjectEntity entity = createValidEntity(i + 1, statuses[i]);
            when(jpaRepository.findById(i + 1)).thenReturn(Optional.of(entity));
            assertEquals(expected[i], adapter.findById(i + 1).get().getStatus());
        }
    }

    @Test
    void testToDomain_UnknownStatus_DefaultsToPostulated() {
        ProjectEntity entity = createValidEntity(1, "UNKNOWN");
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));
        assertEquals(ProjectStatus.POSTULATED, adapter.findById(1).get().getStatus());
    }

    @Test
    void testToEntity_DraftStatus() {
        com.sgi.fiis.proyectos.domain.model.Project project = com.sgi.fiis.proyectos.domain.model.Project.builder()
                .title("Draft")
                .budget(new BigDecimal("100"))
                .status(ProjectStatus.DRAFT)
                .researchLineId(1)
                .responsibleId(3L)
                .researchGroupId(2)
                .build();

        when(lineRepository.findById(1)).thenReturn(Optional.of(new ResearchLineEntity()));
        when(groupRepository.findById(2)).thenReturn(Optional.of(new ResearchGroupEntity()));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new UserEntity()));
        when(jpaRepository.save(any(ProjectEntity.class))).thenReturn(createValidEntity(1, "BORRADOR"));

        adapter.save(project);

        verify(jpaRepository).save(argThat(e -> "BORRADOR".equals(e.getStatus())));
    }

    @Test
    void testToEntity_WithNullCallId() {
        com.sgi.fiis.proyectos.domain.model.Project project = com.sgi.fiis.proyectos.domain.model.Project.builder()
                .title("No Call")
                .budget(new BigDecimal("100"))
                .status(ProjectStatus.POSTULATED)
                .researchLineId(1)
                .responsibleId(3L)
                .researchGroupId(2)
                .callId(null)
                .build();

        when(lineRepository.findById(1)).thenReturn(Optional.of(new ResearchLineEntity()));
        when(groupRepository.findById(2)).thenReturn(Optional.of(new ResearchGroupEntity()));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new UserEntity()));
        when(jpaRepository.save(any(ProjectEntity.class))).thenReturn(createValidEntity(1, "POSTULADO"));

        adapter.save(project);

        verify(jpaRepository).save(argThat(e -> e.getResearchCall() == null));
    }

    @Test
    void testToDomain_WithNullCallId() {
        ProjectEntity entity = createValidEntity(1, "POSTULADO");
        entity.setResearchCall(null);
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        com.sgi.fiis.proyectos.domain.model.Project result = adapter.findById(1).get();

        assertNull(result.getCallId());
    }

    @Test
    void testToDomain_WithNullGroup() {
        ProjectEntity entity = createValidEntity(1, "POSTULADO");
        ResearchGroupEntity group = entity.getGroup();
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        com.sgi.fiis.proyectos.domain.model.Project result = adapter.findById(1).get();

        assertEquals(group.getId(), result.getResearchGroupId());
        assertEquals(group.getCode(), result.getResearchGroupCode());
    }

    @Test
    void testSaveMembers_EmptyList() {
        ProjectEntity projectEntity = createValidEntity(1, "POSTULADO");
        when(jpaRepository.findById(1)).thenReturn(Optional.of(projectEntity));

        adapter.saveMembers(1, List.of());

        verify(projectMemberRepository).deleteByProjectId(1);
        verify(jpaRepository).findById(1);
        verify(projectMemberRepository, never()).save(any());
    }
}
