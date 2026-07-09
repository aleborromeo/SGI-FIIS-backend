package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResearchGroupRepositoryAdapter Unit Tests")
class ResearchGroupRepositoryAdapterTest {

    @Mock
    private SpringDataResearchGroupRepository jpaRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ResearchGroupRepositoryAdapter adapter;

    private ResearchGroupEntity getTestGroupEntity() {
        ResearchGroupEntity entity = new ResearchGroupEntity();
        entity.setId(1);
        entity.setCode("GI-001");
        entity.setName("Grupo de Inteligencia Artificial");
        com.sgi.fiis.users.infrastructure.persistence.UserEntity coordinator = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
        coordinator.setId(10L);
        entity.setCurrentCoordinator(coordinator);
        entity.setActive(true);
        return entity;
    }

    private ResearchGroup getTestGroup() {
        return ResearchGroup.builder()
                .id(1)
                .groupCode("GI-001")
                .groupName("Grupo de Inteligencia Artificial")
                .currentCoordinatorId(10)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully save a ResearchGroup and enrich with coordinator")
    @SuppressWarnings("unchecked")
    void testSave() {
        ResearchGroup domain = getTestGroup();
        ResearchGroupEntity entity = getTestGroupEntity();

        when(jpaRepository.save(any(ResearchGroupEntity.class))).thenReturn(entity);
        
        // Mock enrichWithCoordinator behavior
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenAnswer(invocation -> {
            RowMapper<ResearchGroup> mapper = invocation.getArgument(1);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getString("nombres")).thenReturn("Maria");
            when(rs.getString("apellidos")).thenReturn("Lopez");
            ResearchGroup enriched = mapper.mapRow(rs, 0);
            return List.of(enriched);
        });

        ResearchGroup result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(domain.getGroupName(), result.getGroupName());
        assertEquals("Maria", result.getCoordinatorFirstNames());
        assertEquals("Lopez", result.getCoordinatorLastNames());
        verify(jpaRepository).save(any(ResearchGroupEntity.class));
    }

    @Test
    @DisplayName("Should save a ResearchGroup without coordinator details if coordinator ID is null")
    void testSaveWithoutCoordinator() {
        ResearchGroup domain = ResearchGroup.builder()
                .id(1)
                .groupCode("GI-001")
                .groupName("Grupo de Inteligencia Artificial")
                .currentCoordinatorId(null)
                .active(true)
                .build();

        ResearchGroupEntity entity = getTestGroupEntity();
        entity.setCurrentCoordinator(null);

        when(jpaRepository.save(any(ResearchGroupEntity.class))).thenReturn(entity);

        ResearchGroup result = adapter.save(domain);

        assertNotNull(result);
        assertNull(result.getCurrentCoordinatorId());
        assertNull(result.getCoordinatorFirstNames());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Should find a ResearchGroup by ID and enrich coordinator details")
    @SuppressWarnings("unchecked")
    void testFindById() {
        ResearchGroupEntity entity = getTestGroupEntity();
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenAnswer(invocation -> {
            RowMapper<ResearchGroup> mapper = invocation.getArgument(1);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getString("nombres")).thenReturn("Maria");
            when(rs.getString("apellidos")).thenReturn("Lopez");
            ResearchGroup enriched = mapper.mapRow(rs, 0);
            return List.of(enriched);
        });

        Optional<ResearchGroup> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Maria", result.get().getCoordinatorFirstNames());
        assertEquals("Lopez", result.get().getCoordinatorLastNames());
    }

    @Test
    @DisplayName("Should return empty when ResearchGroup by ID is not found")
    void testFindByIdNotFound() {
        when(jpaRepository.findById(1)).thenReturn(Optional.empty());

        Optional<ResearchGroup> result = adapter.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find all ResearchGroups and map them correctly")
    @SuppressWarnings("unchecked")
    void testFindAll() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenAnswer(invocation -> {
            RowMapper<ResearchGroup> mapper = invocation.getArgument(1);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getInt("id_grupo")).thenReturn(1);
            when(rs.getString("codigo_grupo")).thenReturn("GI-001");
            when(rs.getString("nombre_grupo")).thenReturn("Grupo de Inteligencia Artificial");
            when(rs.getObject("id_coordinador_actual")).thenReturn(10);
            when(rs.getInt("id_coordinador_actual")).thenReturn(10);
            when(rs.getBoolean("es_activo")).thenReturn(true);
            when(rs.getString("coordinator_first_names")).thenReturn("Maria");
            when(rs.getString("coordinator_last_names")).thenReturn("Lopez");
            return List.of(mapper.mapRow(rs, 0));
        });

        List<ResearchGroup> result = adapter.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        ResearchGroup g = result.get(0);
        assertEquals(1, g.getId());
        assertEquals("GI-001", g.getGroupCode());
        assertEquals("Grupo de Inteligencia Artificial", g.getGroupName());
        assertEquals(10, g.getCurrentCoordinatorId());
        assertTrue(g.isActive());
        assertEquals("Maria", g.getCoordinatorFirstNames());
        assertEquals("Lopez", g.getCoordinatorLastNames());
    }

    @Test
    @DisplayName("Should check if group exists by code")
    void testExistsByCode() {
        when(jpaRepository.existsByCode("GI-001")).thenReturn(true);

        assertTrue(adapter.existsByCode("GI-001"));
    }

    @Test
    @DisplayName("Should check if active user exists")
    void testExistsActiveUser() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(5))).thenReturn(1);

        assertTrue(adapter.existsActiveUser(5));
    }

    @Test
    @DisplayName("Should return false when active user check count is 0")
    void testExistsActiveUserCountZero() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(5))).thenReturn(0);

        assertFalse(adapter.existsActiveUser(5));
    }

    @Test
    @DisplayName("Should return false when active user check returns null")
    void testExistsActiveUserCountNull() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(5))).thenReturn(null);

        assertFalse(adapter.existsActiveUser(5));
    }

    @Test
    @DisplayName("Should return true when active user with role exists")
    void testExistsActiveUserWithRole_True() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(5), eq("ADMIN"), eq("ADMIN"))).thenReturn(1);
        assertTrue(adapter.existsActiveUserWithRole(5, "ADMIN"));
    }

    @Test
    @DisplayName("Should return false when active user with role does not exist or count is null")
    void testExistsActiveUserWithRole_False() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(5), eq("ADMIN"), eq("ADMIN"))).thenReturn(0);
        assertFalse(adapter.existsActiveUserWithRole(5, "ADMIN"));

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(5), eq("ADMIN"), eq("ADMIN"))).thenReturn(null);
        assertFalse(adapter.existsActiveUserWithRole(5, "ADMIN"));
    }

    @Test
    @DisplayName("Should find groups by line ID")
    @SuppressWarnings("unchecked")
    void testFindGroupsByLineId() {
        ResearchGroupEntity entity = getTestGroupEntity();
        when(jpaRepository.findActiveByLineId(4)).thenReturn(List.of(entity));

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenAnswer(invocation -> {
            RowMapper<ResearchGroup> mapper = invocation.getArgument(1);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getString("nombres")).thenReturn("Maria");
            when(rs.getString("apellidos")).thenReturn("Lopez");
            ResearchGroup enriched = mapper.mapRow(rs, 0);
            return List.of(enriched);
        });

        List<ResearchGroup> result = adapter.findGroupsByLineId(4);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("GI-001", result.get(0).getGroupCode());
        assertEquals("Maria", result.get(0).getCoordinatorFirstNames());
    }
}

