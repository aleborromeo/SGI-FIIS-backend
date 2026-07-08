package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MembershipRepositoryAdapter Unit Tests")
class MembershipRepositoryAdapterTest {

    @Mock
    private SpringDataMembershipRepository jpaRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private MembershipRepositoryAdapter adapter;

    private MembershipEntity getTestMembershipEntity() {
        MembershipEntity entity = new MembershipEntity();
        entity.setId(1);
        entity.setGroupId(2);
        entity.setUserId(3);
        entity.setActive(true);
        entity.setStartDate(LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0));
        entity.setEndDate(LocalDateTime.of(2026, Month.DECEMBER, 31, 23, 59));
        return entity;
    }

    private Membership getTestMembership() {
        return Membership.builder()
                .id(1)
                .groupId(2)
                .userId(3)
                .active(true)
                .startDate(LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0))
                .endDate(LocalDateTime.of(2026, Month.DECEMBER, 31, 23, 59))
                .build();
    }

    @Test
    @DisplayName("Should successfully save a membership")
    void testSave() {
        Membership domain = getTestMembership();
        MembershipEntity entity = getTestMembershipEntity();

        when(jpaRepository.save(any(MembershipEntity.class))).thenReturn(entity);

        Membership result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(domain.getId(), result.getId());
        assertEquals(domain.getGroupId(), result.getGroupId());
        assertEquals(domain.getUserId(), result.getUserId());
        assertEquals(domain.getStartDate(), result.getStartDate());
        verify(jpaRepository).save(any(MembershipEntity.class));
    }

    @Test
    @DisplayName("Should find a membership by ID")
    void testFindById() {
        MembershipEntity entity = getTestMembershipEntity();
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        Optional<Membership> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals(2, result.get().getGroupId());
        verify(jpaRepository).findById(1);
    }

    @Test
    @DisplayName("Should find active membership by user and group")
    void testFindActiveByUserInGroup() {
        MembershipEntity entity = getTestMembershipEntity();
        when(jpaRepository.findByUserIdAndGroupIdAndActiveTrue(3, 2)).thenReturn(Optional.of(entity));

        Optional<Membership> result = adapter.findActiveByUserInGroup(3, 2);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(jpaRepository).findByUserIdAndGroupIdAndActiveTrue(3, 2);
    }

    @Test
    @DisplayName("Should check if active membership exists by user ID")
    void testExistsActiveByUser() {
        when(jpaRepository.existsByUserIdAndActiveTrue(3)).thenReturn(true);

        assertTrue(adapter.existsActiveByUser(3));
        verify(jpaRepository).existsByUserIdAndActiveTrue(3);
    }

    @Test
    @DisplayName("Should find active memberships by group and map row data including timestamps")
    @SuppressWarnings("unchecked")
    void testFindActiveByGroup() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_membresia")).thenReturn(10);
        when(rs.getInt("id_grupo")).thenReturn(2);
        when(rs.getInt("id_usuario")).thenReturn(3);
        when(rs.getBoolean("es_activo")).thenReturn(true);
        when(rs.getObject("fecha_inicio", LocalDateTime.class)).thenReturn(LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0));
        when(rs.getObject("fecha_fin", LocalDateTime.class)).thenReturn(LocalDateTime.of(2026, Month.DECEMBER, 31, 23, 59));
        when(rs.getString("user_first_names")).thenReturn("Juan");
        when(rs.getString("user_last_names")).thenReturn("Perez");
        when(rs.getString("user_email")).thenReturn("juan.perez@unas.edu.pe");

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(2))).thenAnswer(invocation -> {
            RowMapper<Membership> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        List<Membership> result = adapter.findActiveByGroup(2);

        assertNotNull(result);
        assertEquals(1, result.size());
        Membership m = result.get(0);
        assertEquals(10, m.getId());
        assertEquals(2, m.getGroupId());
        assertEquals(3, m.getUserId());
        assertTrue(m.isActive());
        assertEquals(LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0), m.getStartDate());
        assertEquals(LocalDateTime.of(2026, Month.DECEMBER, 31, 23, 59), m.getEndDate());
        assertEquals("Juan", m.getUserFirstNames());
        assertEquals("Perez", m.getUserLastNames());
        assertEquals("juan.perez@unas.edu.pe", m.getUserEmail());
    }

    @Test
    @DisplayName("Should map active memberships with null timestamps correctly")
    @SuppressWarnings("unchecked")
    void testFindActiveByGroupNullTimestamps() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_membresia")).thenReturn(10);
        when(rs.getInt("id_grupo")).thenReturn(2);
        when(rs.getInt("id_usuario")).thenReturn(3);
        when(rs.getBoolean("es_activo")).thenReturn(true);
        when(rs.getObject("fecha_inicio", LocalDateTime.class)).thenReturn(null);
        when(rs.getObject("fecha_fin", LocalDateTime.class)).thenReturn(null);
        when(rs.getString("user_first_names")).thenReturn("Juan");
        when(rs.getString("user_last_names")).thenReturn("Perez");
        when(rs.getString("user_email")).thenReturn("juan.perez@unas.edu.pe");

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(2))).thenAnswer(invocation -> {
            RowMapper<Membership> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        List<Membership> result = adapter.findActiveByGroup(2);

        assertNotNull(result);
        assertEquals(1, result.size());
        Membership m = result.get(0);
        assertNull(m.getStartDate());
        assertNull(m.getEndDate());
    }
}
