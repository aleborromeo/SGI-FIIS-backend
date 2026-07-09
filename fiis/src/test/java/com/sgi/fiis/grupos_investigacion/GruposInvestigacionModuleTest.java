package com.sgi.fiis.grupos_investigacion;

import com.sgi.fiis.grupos_investigacion.application.usecase.*;
import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import com.sgi.fiis.grupos_investigacion.domain.port.MembresiaRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GruposInvestigacionModuleTest {

    private GrupoInvestigacionRepositoryPort grupoRepository;
    private MembresiaRepositoryPort membresiaRepository;
    private CrearGrupoUseCase crearGrupoUseCase;
    private ListarGruposUseCase listarGruposUseCase;
    private ObtenerGrupoUseCase obtenerGrupoUseCase;
    private AsignarCoordinadorUseCase asignarCoordinadorUseCase;
    private AsignarMiembroUseCase asignarMiembroUseCase;
    private RetirarMiembroUseCase retirarMiembroUseCase;
    private ListarMiembrosUseCase listarMiembrosUseCase;

    @BeforeEach
    void setup() {
        grupoRepository = Mockito.mock(GrupoInvestigacionRepositoryPort.class);
        membresiaRepository = Mockito.mock(MembresiaRepositoryPort.class);
        crearGrupoUseCase = new CrearGrupoUseCase(grupoRepository);
        listarGruposUseCase = new ListarGruposUseCase(grupoRepository);
        obtenerGrupoUseCase = new ObtenerGrupoUseCase(grupoRepository);
        asignarCoordinadorUseCase = new AsignarCoordinadorUseCase(grupoRepository);
        asignarMiembroUseCase = new AsignarMiembroUseCase(grupoRepository, membresiaRepository);
        retirarMiembroUseCase = new RetirarMiembroUseCase(membresiaRepository);
        listarMiembrosUseCase = new ListarMiembrosUseCase(grupoRepository, membresiaRepository);
    }

    @Test
    void shouldCrearGrupo() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder()
                .codigoGrupo("GIN-001")
                .nombreGrupo("Grupo Test")
                .build();
        GrupoInvestigacion saved = GrupoInvestigacion.builder()
                .id(1)
                .codigoGrupo("GIN-001")
                .nombreGrupo("Grupo Test")
                .esActivo(true)
                .build();

        when(grupoRepository.existsByCodigo("GIN-001")).thenReturn(false);
        when(grupoRepository.save(any(GrupoInvestigacion.class))).thenReturn(saved);

        GrupoInvestigacion result = crearGrupoUseCase.execute(grupo);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertTrue(result.isEsActivo());
    }

    @Test
    void shouldThrowWhenCodigoDuplicado() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().codigoGrupo("GIN-001").build();
        when(grupoRepository.existsByCodigo("GIN-001")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> crearGrupoUseCase.execute(grupo));
    }

    @Test
    void shouldObtenerGrupo() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).build();
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupo));
        GrupoInvestigacion result = obtenerGrupoUseCase.execute(1);
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenGrupoNotFound() {
        when(grupoRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> obtenerGrupoUseCase.execute(99));
    }

    @Test
    void shouldListarGrupos() {
        when(grupoRepository.findAll()).thenReturn(Collections.singletonList(GrupoInvestigacion.builder().id(1).build()));
        List<GrupoInvestigacion> result = listarGruposUseCase.execute();
        assertEquals(1, result.size());
    }

    @Test
    void shouldAsignarCoordinadorSuccessfully() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).build();
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupo));
        when(grupoRepository.existeUsuarioActivo(5)).thenReturn(true);
        when(grupoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GrupoInvestigacion result = asignarCoordinadorUseCase.execute(1, 5);
        assertEquals(5, result.getIdCoordinadorActual());
    }

    @Test
    void shouldThrowWhenAsignarCoordinadorUserNotActive() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).build();
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupo));
        when(grupoRepository.existeUsuarioActivo(5)).thenReturn(false);

        assertThrows(BusinessException.class, () -> asignarCoordinadorUseCase.execute(1, 5));
    }

    @Test
    void shouldAsignarMiembroSuccessfully() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).build();
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupo));
        when(grupoRepository.existeUsuarioActivo(5)).thenReturn(true);
        when(membresiaRepository.existsActivaByUsuario(5)).thenReturn(false);
        when(membresiaRepository.save(any(Membresia.class))).thenAnswer(inv -> inv.getArgument(0));

        Membresia result = asignarMiembroUseCase.execute(1, 5);
        assertNotNull(result);
        assertEquals(1, result.getIdGrupo());
        assertEquals(5, result.getIdUsuario());
        assertTrue(result.isEsActivo());
    }

    @Test
    void shouldThrowWhenAsignarMiembroGrupoNotFound() {
        when(grupoRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> asignarMiembroUseCase.execute(99, 5));
    }

    @Test
    void shouldThrowWhenAsignarMiembroUserNotActive() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).build();
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupo));
        when(grupoRepository.existeUsuarioActivo(5)).thenReturn(false);

        assertThrows(BusinessException.class, () -> asignarMiembroUseCase.execute(1, 5));
    }

    @Test
    void shouldThrowWhenAsignarMiembroAlreadyInActiveGroup() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).build();
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupo));
        when(grupoRepository.existeUsuarioActivo(5)).thenReturn(true);
        when(membresiaRepository.existsActivaByUsuario(5)).thenReturn(true);

        assertThrows(BusinessException.class, () -> asignarMiembroUseCase.execute(1, 5));
    }

    @Test
    void shouldRetirarMiembroSuccessfully() {
        Membresia active = Membresia.builder().id(1).idGrupo(2).idUsuario(3).esActivo(true).build();
        when(membresiaRepository.findActivaByUsuarioEnGrupo(3, 2)).thenReturn(Optional.of(active));
        when(membresiaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Membresia result = retirarMiembroUseCase.execute(2, 3);
        assertFalse(result.isEsActivo());
        assertNotNull(result.getFechaFin());
    }

    @Test
    void shouldThrowWhenRetirarMiembroNotFound() {
        when(membresiaRepository.findActivaByUsuarioEnGrupo(3, 2)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> retirarMiembroUseCase.execute(2, 3));
    }

    @Test
    void shouldListarMiembrosSuccessfully() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).build();
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupo));
        when(membresiaRepository.findActivasByGrupo(1)).thenReturn(Collections.singletonList(Membresia.builder().id(1).build()));

        List<Membresia> result = listarMiembrosUseCase.execute(1);
        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenListarMiembrosGrupoNotFound() {
        when(grupoRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> listarMiembrosUseCase.execute(99));
    }
}
