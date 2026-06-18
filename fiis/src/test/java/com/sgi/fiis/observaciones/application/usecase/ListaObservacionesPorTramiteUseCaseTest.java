package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.ObservacionResponseDTO;
import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.model.ObservacionEstado;
import com.sgi.fiis.observaciones.domain.model.TipoObservacion;
import com.sgi.fiis.observaciones.domain.port.ObservacionRepository;
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
@DisplayName("ListaObservacionesPorTramiteUseCase Unit Tests")
class ListaObservacionesPorTramiteUseCaseTest {

    @Mock
    private ObservacionRepository observacionRepository;

    private ListaObservacionesPorTramiteUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListaObservacionesPorTramiteUseCase(observacionRepository);
    }

    @Test
    @DisplayName("Should list observations for a given tramite")
    void shouldListObservationsByTramite() {
        Integer idTramite = 1;
        LocalDateTime ahora = LocalDateTime.now();
        Observacion obs = Observacion.builder()
                .id(100)
                .idTramite(idTramite)
                .idRevisor(10)
                .tipoObservacion(TipoObservacion.TECNICA)
                .descripcion("Falta firma")
                .estado(ObservacionEstado.PENDIENTE)
                .rolRevisor("COORDINADOR_GRUPO")
                .fechaRegistro(ahora)
                .fechaActualizacion(ahora)
                .build();

        when(observacionRepository.findByIdTramite(idTramite)).thenReturn(Collections.singletonList(obs));

        List<ObservacionResponseDTO> result = useCase.execute(idTramite);

        assertNotNull(result);
        assertEquals(1, result.size());
        ObservacionResponseDTO response = result.get(0);
        assertEquals(100, response.getId());
        assertEquals(1, response.getIdTramite());
        assertEquals("TECNICA", response.getTipoObservacion());
        assertEquals("PENDIENTE", response.getEstado());
        assertEquals("COORDINADOR_GRUPO", response.getRolRevisor());

        verify(observacionRepository, times(1)).findByIdTramite(idTramite);
    }
}
