package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LineaInvestigacionRepositoryAdapterTest {

    private SpringDataLineaRepository springDataRepository;
    private LineaInvestigacionRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        springDataRepository = mock(SpringDataLineaRepository.class);
        adapter = new LineaInvestigacionRepositoryAdapter(springDataRepository);
    }

    @Test
    void saveShouldReturnSavedLinea() {
        LineaInvestigacion domain = LineaInvestigacion.builder().nombreLinea("Test").build();
        LineaInvestigacionEntity entity = new LineaInvestigacionEntity();
        entity.setId(1);
        entity.setNombreLinea("Test");
        entity.setEsActiva(true);
        when(springDataRepository.save(any(LineaInvestigacionEntity.class))).thenReturn(entity);

        LineaInvestigacion result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test", result.getNombreLinea());
    }

    @Test
    void findByIdShouldReturnLineaWhenFound() {
        LineaInvestigacionEntity entity = new LineaInvestigacionEntity();
        entity.setId(1);
        entity.setNombreLinea("Test");
        when(springDataRepository.findById(1)).thenReturn(Optional.of(entity));

        Optional<LineaInvestigacion> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void findAllShouldReturnAllLineas() {
        LineaInvestigacionEntity entity = new LineaInvestigacionEntity();
        entity.setId(1);
        when(springDataRepository.findAll()).thenReturn(Collections.singletonList(entity));

        List<LineaInvestigacion> results = adapter.findAll();

        assertEquals(1, results.size());
    }

    @Test
    void existsByNombreShouldReturnTrueWhenExists() {
        when(springDataRepository.existsByNombreLinea("Test")).thenReturn(true);
        assertTrue(adapter.existsByNombre("Test"));
    }
}
