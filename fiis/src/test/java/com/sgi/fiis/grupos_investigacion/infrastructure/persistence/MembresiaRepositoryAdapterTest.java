package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MembresiaRepositoryAdapterTest {

    private SpringDataMembresiaRepository jpaRepository;
    private JdbcTemplate jdbcTemplate;
    private MembresiaRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = Mockito.mock(SpringDataMembresiaRepository.class);
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        adapter = new MembresiaRepositoryAdapter(jpaRepository, jdbcTemplate);
    }

    @Test
    void shouldSave() {
        Membresia domain = Membresia.builder().id(1).idGrupo(2).idUsuario(3).esActivo(true).build();
        MembresiaEntity entity = new MembresiaEntity();
        entity.setId(1);
        entity.setIdGrupo(2);
        entity.setIdUsuario(3);
        entity.setEsActivo(true);

        when(jpaRepository.save(any(MembresiaEntity.class))).thenReturn(entity);

        Membresia result = adapter.save(domain);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void shouldFindById() {
        MembresiaEntity entity = new MembresiaEntity();
        entity.setId(1);
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        Optional<Membresia> result = adapter.findById(1);
        assertTrue(result.isPresent());
    }

    @Test
    void shouldFindActivaByUsuarioEnGrupo() {
        MembresiaEntity entity = new MembresiaEntity();
        entity.setId(1);
        when(jpaRepository.findByIdUsuarioAndIdGrupoAndEsActivoTrue(3, 2)).thenReturn(Optional.of(entity));

        Optional<Membresia> result = adapter.findActivaByUsuarioEnGrupo(3, 2);
        assertTrue(result.isPresent());
    }

    @Test
    void shouldFindActivasByGrupo() {
        LocalDateTime now = LocalDateTime.now();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(2)))
                .thenAnswer(invocation -> {
                    RowMapper<Membresia> rm = invocation.getArgument(1);
                    java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
                    when(rs.getInt("id_membresia")).thenReturn(1);
                    when(rs.getInt("id_grupo")).thenReturn(2);
                    when(rs.getInt("id_usuario")).thenReturn(3);
                    when(rs.getBoolean("es_activo")).thenReturn(true);
                    when(rs.getObject("fecha_inicio", LocalDateTime.class)).thenReturn(now);
                    when(rs.getObject("fecha_fin", LocalDateTime.class)).thenReturn(null);
                    when(rs.getString("usuario_nombres")).thenReturn("John");
                    when(rs.getString("usuario_apellidos")).thenReturn("Doe");
                    when(rs.getString("usuario_correo")).thenReturn("john@doe.com");
                    return Arrays.asList(rm.mapRow(rs, 1));
                });

        List<Membresia> result = adapter.findActivasByGrupo(2);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getUsuarioNombres());
        assertEquals(now, result.get(0).getFechaInicio());
    }

    @Test
    void shouldExistsActivaByUsuario() {
        when(jpaRepository.existsByIdUsuarioAndEsActivoTrue(3)).thenReturn(true);
        assertTrue(adapter.existsActivaByUsuario(3));
    }
}
