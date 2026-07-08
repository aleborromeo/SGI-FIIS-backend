package com.sgi.fiis.lineas_investigacion;

import com.sgi.fiis.lineas_investigacion.application.usecase.CambiarEstadoLineaUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListarLineasPorGrupoUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListarLineasUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.ObtenerLineaUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.RegistrarLineaUseCase;
import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.domain.port.LineaInvestigacionRepositoryPort;
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

class LineasInvestigacionModuleTest {

    private LineaInvestigacionRepositoryPort repository;
    private RegistrarLineaUseCase registrarLineaUseCase;
    private ObtenerLineaUseCase obtenerLineaUseCase;
    private ListarLineasUseCase listarLineasUseCase;
    private ListarLineasPorGrupoUseCase listarLineasPorGrupoUseCase;
    private CambiarEstadoLineaUseCase cambiarEstadoLineaUseCase;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(LineaInvestigacionRepositoryPort.class);
        registrarLineaUseCase = new RegistrarLineaUseCase(repository);
        obtenerLineaUseCase = new ObtenerLineaUseCase(repository);
        listarLineasUseCase = new ListarLineasUseCase(repository);
        listarLineasPorGrupoUseCase = new ListarLineasPorGrupoUseCase(repository);
        cambiarEstadoLineaUseCase = new CambiarEstadoLineaUseCase(repository);
    }

    @Test
    void shouldCreateLineaInvestigacionSuccessfully() {
        LineaInvestigacion linea = LineaInvestigacion.builder()
                .nombreLinea("Inteligencia Artificial")
                .build();

        when(repository.existsByNombre("Inteligencia Artificial")).thenReturn(false);
        LineaInvestigacion savedLinea = LineaInvestigacion.builder()
                .id(1)
                .nombreLinea("Inteligencia Artificial")
                .esActiva(true)
                .build();
        when(repository.save(any(LineaInvestigacion.class))).thenReturn(savedLinea);

        LineaInvestigacion result = registrarLineaUseCase.execute(linea);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Inteligencia Artificial", result.getNombreLinea());
        assertTrue(result.isEsActiva());
    }

    @Test
    void shouldFailWhenNombreLineaAlreadyExists() {
        LineaInvestigacion linea = LineaInvestigacion.builder()
                .nombreLinea("Duplicada")
                .build();
        when(repository.existsByNombre("Duplicada")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> registrarLineaUseCase.execute(linea));
    }

    @Test
    void shouldGetLineaByIdSuccessfully() {
        LineaInvestigacion linea = LineaInvestigacion.builder()
                .id(1)
                .nombreLinea("Ciberseguridad")
                .esActiva(true)
                .build();
        when(repository.findById(1)).thenReturn(Optional.of(linea));

        LineaInvestigacion result = obtenerLineaUseCase.execute(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Ciberseguridad", result.getNombreLinea());
    }

    @Test
    void shouldFailWhenLineaNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> obtenerLineaUseCase.execute(99));
    }

    @Test
    void shouldListAllLineasSuccessfully() {
        LineaInvestigacion linea1 = LineaInvestigacion.builder().id(1).nombreLinea("Linea 1").build();
        LineaInvestigacion linea2 = LineaInvestigacion.builder().id(2).nombreLinea("Linea 2").build();
        when(repository.findAll()).thenReturn(List.of(linea1, linea2));

        List<LineaInvestigacion> results = listarLineasUseCase.execute(false);

        assertEquals(2, results.size());
    }

    @Test
    void shouldListOnlyActivasLineasSuccessfully() {
        LineaInvestigacion linea1 = LineaInvestigacion.builder().id(1).nombreLinea("Activa 1").esActiva(true).build();
        LineaInvestigacion linea2 = LineaInvestigacion.builder().id(2).nombreLinea("Activa 2").esActiva(true).build();
        when(repository.findAllActivas()).thenReturn(List.of(linea1, linea2));

        List<LineaInvestigacion> results = listarLineasUseCase.execute(true);

        assertEquals(2, results.size());
    }

    @Test
    void shouldListLineasByGrupoSuccessfully() {
        LineaInvestigacion linea = LineaInvestigacion.builder().id(1).nombreLinea("Linea Grupo 1").build();
        when(repository.findActivasByGrupo(1)).thenReturn(Collections.singletonList(linea));

        List<LineaInvestigacion> results = listarLineasPorGrupoUseCase.execute(1);

        assertEquals(1, results.size());
        assertEquals("Linea Grupo 1", results.get(0).getNombreLinea());
    }

    @Test
    void shouldActivarLineaSuccessfully() {
        LineaInvestigacion linea = LineaInvestigacion.builder()
                .id(1)
                .nombreLinea("Linea")
                .esActiva(false)
                .build();
        when(repository.findById(1)).thenReturn(Optional.of(linea));
        when(repository.save(any(LineaInvestigacion.class))).thenReturn(linea);

        LineaInvestigacion result = cambiarEstadoLineaUseCase.execute(1, true);

        assertTrue(result.isEsActiva());
    }

    @Test
    void shouldDesactivarLineaSuccessfully() {
        LineaInvestigacion linea = LineaInvestigacion.builder()
                .id(1)
                .nombreLinea("Linea")
                .esActiva(true)
                .build();
        when(repository.findById(1)).thenReturn(Optional.of(linea));
        when(repository.save(any(LineaInvestigacion.class))).thenReturn(linea);

        LineaInvestigacion result = cambiarEstadoLineaUseCase.execute(1, false);

        assertFalse(result.isEsActiva());
    }

    @Test
    void domainModel_ActivarShouldSetEsActivaTrue() {
        LineaInvestigacion linea = LineaInvestigacion.builder().esActiva(false).build();
        linea.activar();
        assertTrue(linea.isEsActiva());
    }

    @Test
    void domainModel_DesactivarShouldSetEsActivaFalse() {
        LineaInvestigacion linea = LineaInvestigacion.builder().esActiva(true).build();
        linea.desactivar();
        assertFalse(linea.isEsActiva());
    }
}
