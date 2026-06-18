package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.ObservacionResponseDTO;
import com.sgi.fiis.observaciones.domain.exception.ObservacionNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultaObservacionUseCase Unit Tests")
class ConsultaObservacionUseCaseTest {

    @Mock
    private ObservacionRepository observacionRepository;

    private ConsultaObservacionUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultaObservacionUseCase(observacionRepository);
    }

    @Test
    @DisplayName("Should return observation details when observation exists")
    void shouldReturnDetailsWhenObservationExists() {
        Integer idObservacion = 100;
        LocalDateTime ahora = LocalDateTime.now();
        Observacion obs = Observacion.builder()
                .id(idObservacion)
                .idTramite(1)
                .idRevisor(10)
                .tipoObservacion(TipoObservacion.TECNICA)
                .descripcion("Falta firma")
                .estado(ObservacionEstado.PENDIENTE)
                .rolRevisor("COORDINADOR_GRUPO")
                .fechaRegistro(ahora)
                .fechaActualizacion(ahora)
                .build();

        when(observacionRepository.findById(idObservacion)).thenReturn(Optional.of(obs));

        ObservacionResponseDTO response = useCase.execute(idObservacion);

        assertNotNull(response);
        assertEquals(100, response.getId());
        assertEquals(1, response.getIdTramite());
        assertEquals("TECNICA", response.getTipoObservacion());
        assertEquals("PENDIENTE", response.getEstado());

        verify(observacionRepository, times(1)).findById(idObservacion);
    }

    @Test
    @DisplayName("Should throw exception when observation does not exist")
    void shouldThrowExceptionWhenObservationDoesNotExist() {
        Integer idObservacion = 999;
        when(observacionRepository.findById(idObservacion)).thenReturn(Optional.empty());

        assertThrows(ObservacionNotFoundException.class, () -> useCase.execute(idObservacion));

        verify(observacionRepository, times(1)).findById(idObservacion);
    }
}
