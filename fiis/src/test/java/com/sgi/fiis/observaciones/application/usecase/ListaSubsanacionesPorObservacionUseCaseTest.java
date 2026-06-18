package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.SubsanacionResponseDTO;
import com.sgi.fiis.observaciones.domain.model.Subsanacion;
import com.sgi.fiis.observaciones.domain.port.SubsanacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListaSubsanacionesPorObservacionUseCase Unit Tests")
class ListaSubsanacionesPorObservacionUseCaseTest {

    @Mock
    private SubsanacionRepository subsanacionRepository;

    private ListaSubsanacionesPorObservacionUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListaSubsanacionesPorObservacionUseCase(subsanacionRepository);
    }

    @Test
    @DisplayName("Should list subsanaciones for a given observacion")
    void shouldListSubsanacionesByObservacion() {
        Integer idObservacion = 100;
        LocalDateTime ahora = LocalDateTime.now();
        Subsanacion sub = Subsanacion.builder()
                .id(200)
                .idObservacion(idObservacion)
                .idSolicitante(5)
                .descripcion("He subido el documento")
                .idDocumentoAdjunto(45)
                .fechaRegistro(ahora)
                .fechaActualizacion(ahora)
                .build();

        when(subsanacionRepository.findByIdObservacion(idObservacion)).thenReturn(Collections.singletonList(sub));

        List<SubsanacionResponseDTO> result = useCase.execute(idObservacion);

        assertNotNull(result);
        assertEquals(1, result.size());
        SubsanacionResponseDTO response = result.get(0);
        assertEquals(200, response.getId());
        assertEquals(idObservacion, response.getIdObservacion());
        assertEquals(5, response.getIdSolicitante());
        assertEquals("He subido el documento", response.getDescripcion());
        assertEquals(45, response.getIdDocumentoAdjunto());

        verify(subsanacionRepository, times(1)).findByIdObservacion(idObservacion);
    }
}
