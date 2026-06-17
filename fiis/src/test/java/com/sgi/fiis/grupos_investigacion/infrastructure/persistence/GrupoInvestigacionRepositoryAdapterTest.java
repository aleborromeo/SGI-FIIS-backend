package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GrupoInvestigacionRepositoryAdapterTest {

    private SpringDataGrupoRepository jpaRepository;
    private JdbcTemplate jdbcTemplate;
    private GrupoInvestigacionRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = Mockito.mock(SpringDataGrupoRepository.class);
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        adapter = new GrupoInvestigacionRepositoryAdapter(jpaRepository, jdbcTemplate);
    }

    @Test
    void shouldSaveWithoutCoordinator() {
        GrupoInvestigacion domain = GrupoInvestigacion.builder()
                .id(1)
                .codigoGrupo("GIN-001")
                .nombreGrupo("Group Test")
                .esActivo(true)
                .build();
        GrupoInvestigacionEntity entity = new GrupoInvestigacionEntity();
        entity.setId(1);
        entity.setCodigoGrupo("GIN-001");
        entity.setNombreGrupo("Group Test");
        entity.setEsActivo(true);

        when(jpaRepository.save(any(GrupoInvestigacionEntity.class))).thenReturn(entity);

        GrupoInvestigacion result = adapter.save(domain);
        assertNotNull(result);
        assertNull(result.getIdCoordinadorActual());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void shouldSaveWithCoordinator() {
        GrupoInvestigacion domain = GrupoInvestigacion.builder()
                .id(1)
                .codigoGrupo("GIN-001")
                .nombreGrupo("Group Test")
                .idCoordinadorActual(10)
                .esActivo(true)
                .build();
        GrupoInvestigacionEntity entity = new GrupoInvestigacionEntity();
        entity.setId(1);
        entity.setCodigoGrupo("GIN-001");
        entity.setNombreGrupo("Group Test");
        entity.setIdCoordinadorActual(10);
        entity.setEsActivo(true);

        when(jpaRepository.save(any(GrupoInvestigacionEntity.class))).thenReturn(entity);
        // Mock enrichWithCoordinator
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(10)))
                .thenAnswer(invocation -> {
                    RowMapper<GrupoInvestigacion> rm = invocation.getArgument(1);
                    java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
                    when(rs.getString("nombres")).thenReturn("John");
                    when(rs.getString("apellidos")).thenReturn("Doe");
                    GrupoInvestigacion g = rm.mapRow(rs, 1);
                    return Collections.singletonList(g);
                });

        GrupoInvestigacion result = adapter.save(domain);
        assertNotNull(result);
        assertEquals("John", result.getCoordinadorNombres());
        assertEquals("Doe", result.getCoordinadorApellidos());
    }

    @Test
    void shouldFindById() {
        GrupoInvestigacionEntity entity = new GrupoInvestigacionEntity();
        entity.setId(1);
        entity.setIdCoordinadorActual(10);

        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(10)))
                .thenReturn(Collections.emptyList());

        Optional<GrupoInvestigacion> result = adapter.findById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void shouldFindAll() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenAnswer(invocation -> {
                    RowMapper<GrupoInvestigacion> rm = invocation.getArgument(1);
                    java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
                    when(rs.getInt("id_grupo")).thenReturn(1);
                    when(rs.getString("codigo_grupo")).thenReturn("GIN-001");
                    when(rs.getString("nombre_grupo")).thenReturn("Name");
                    when(rs.getObject("id_coordinador_actual")).thenReturn(5);
                    when(rs.getInt("id_coordinador_actual")).thenReturn(5);
                    when(rs.getBoolean("es_activo")).thenReturn(true);
                    when(rs.getString("coordinador_nombres")).thenReturn("John");
                    when(rs.getString("coordinador_apellidos")).thenReturn("Doe");
                    return Arrays.asList(rm.mapRow(rs, 1));
                });

        List<GrupoInvestigacion> result = adapter.findAll();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getCoordinadorNombres());
    }

    @Test
    void shouldExistsByCodigo() {
        when(jpaRepository.existsByCodigoGrupo("GIN-001")).thenReturn(true);
        assertTrue(adapter.existsByCodigo("GIN-001"));
    }

    @Test
    void shouldCheckExisteUsuarioActivo() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(5))).thenReturn(1);
        assertTrue(adapter.existeUsuarioActivo(5));

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(6))).thenReturn(0);
        assertFalse(adapter.existeUsuarioActivo(6));
    }
}
