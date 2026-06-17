package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.ObservacionRequestDTO;
import com.sgi.fiis.observaciones.application.dto.ObservacionResponseDTO;
import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.model.ObservacionEstado;
import com.sgi.fiis.observaciones.domain.model.TipoObservacion;
import com.sgi.fiis.observaciones.domain.port.ObservacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistraObservacionUseCaseTest {

    @Mock
    private ObservacionRepository observacionRepository;

    private RegistraObservacionUseCase registraObservacionUseCase;

    @BeforeEach
    void setUp() {
        registraObservacionUseCase = new RegistraObservacionUseCase(observacionRepository);
    }

    @Test
    void execute_WhenRequestIsValid_ShouldSaveAndReturnResponse() {
        // Arrange
        ObservacionRequestDTO request = ObservacionRequestDTO.builder()
                .idTramite(1)
                .idRevisor(10)
                .tipoObservacion("TECNICA")
                .descripcion("Falta la firma en el documento de propuesta técnica")
                .rolRevisor("COORDINADOR_GRUPO")
                .build();

        LocalDateTime fechaFija = LocalDateTime.of(2026, 6, 17, 12, 0);
        Observacion observacionGuardada = Observacion.builder()
                .id(100)
                .idTramite(1)
                .idRevisor(10)
                .tipoObservacion(TipoObservacion.TECNICA)
                .descripcion("Falta la firma en el documento de propuesta técnica")
                .estado(ObservacionEstado.PENDIENTE)
                .rolRevisor("COORDINADOR_GRUPO")
                .fechaRegistro(fechaFija)
                .fechaActualizacion(fechaFija)
                .build();

        when(observacionRepository.save(any(Observacion.class))).thenReturn(observacionGuardada);

        // Act
        ObservacionResponseDTO response = registraObservacionUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(100, response.getId());
        assertEquals(1, response.getIdTramite());
        assertEquals(10, response.getIdRevisor());
        assertEquals("TECNICA", response.getTipoObservacion());
        assertEquals("Falta la firma en el documento de propuesta técnica", response.getDescripcion());
        assertEquals("PENDIENTE", response.getEstado());
        assertEquals("COORDINADOR_GRUPO", response.getRolRevisor());
        assertEquals(fechaFija, response.getFechaRegistro());
        assertEquals(fechaFija, response.getFechaActualizacion());

        verify(observacionRepository, times(1)).save(any(Observacion.class));
    }

    @Test
    void execute_WhenInvalidTipoObservacion_ShouldThrowException() {
        // Arrange
        ObservacionRequestDTO request = ObservacionRequestDTO.builder()
                .idTramite(1)
                .idRevisor(10)
                .tipoObservacion("INVALIDA")
                .descripcion("Falta la firma")
                .rolRevisor("COORDINADOR_GRUPO")
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> registraObservacionUseCase.execute(request));
        verify(observacionRepository, never()).save(any(Observacion.class));
    }
}
