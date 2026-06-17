package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.SubsanacionRequestDTO;
import com.sgi.fiis.observaciones.application.dto.SubsanacionResponseDTO;
import com.sgi.fiis.observaciones.domain.exception.ObservacionNotFoundException;
import com.sgi.fiis.observaciones.domain.exception.SubsanacionInvalidaException;
import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.model.ObservacionEstado;
import com.sgi.fiis.observaciones.domain.model.Subsanacion;
import com.sgi.fiis.observaciones.domain.model.TipoObservacion;
import com.sgi.fiis.observaciones.domain.port.ObservacionRepository;
import com.sgi.fiis.observaciones.domain.port.SubsanacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistraSubsanacionUseCaseTest {

    @Mock
    private ObservacionRepository observacionRepository;

    @Mock
    private SubsanacionRepository subsanacionRepository;

    private RegistraSubsanacionUseCase registraSubsanacionUseCase;

    @BeforeEach
    void setUp() {
        registraSubsanacionUseCase = new RegistraSubsanacionUseCase(observacionRepository, subsanacionRepository);
    }

    @Test
    void execute_WhenObservacionIsPendiente_ShouldMarkSubsanadaAndSaveSubsanacion() {
        // Arrange
        Integer idObservacion = 100;
        SubsanacionRequestDTO request = SubsanacionRequestDTO.builder()
                .idSolicitante(5)
                .descripcion("He subido el documento firmado correctamente.")
                .idDocumentoAdjunto(45)
                .build();

        LocalDateTime fechaFija = LocalDateTime.of(2026, 6, 17, 12, 0);
        Observacion observacionOriginal = Observacion.builder()
                .id(idObservacion)
                .idTramite(1)
                .idRevisor(10)
                .tipoObservacion(TipoObservacion.TECNICA)
                .descripcion("Falta la firma en el documento de propuesta técnica")
                .estado(ObservacionEstado.PENDIENTE)
                .rolRevisor("COORDINADOR_GRUPO")
                .fechaRegistro(fechaFija)
                .fechaActualizacion(fechaFija)
                .build();

        Subsanacion subsanacionGuardada = Subsanacion.builder()
                .id(200)
                .idObservacion(idObservacion)
                .idSolicitante(5)
                .descripcion("He subido el documento firmado correctamente.")
                .idDocumentoAdjunto(45)
                .fechaRegistro(fechaFija)
                .fechaActualizacion(fechaFija)
                .build();

        when(observacionRepository.findById(idObservacion)).thenReturn(Optional.of(observacionOriginal));
        when(observacionRepository.save(any(Observacion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subsanacionRepository.save(any(Subsanacion.class))).thenReturn(subsanacionGuardada);

        // Act
        SubsanacionResponseDTO response = registraSubsanacionUseCase.execute(idObservacion, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getId());
        assertEquals(idObservacion, response.getIdObservacion());
        assertEquals(5, response.getIdSolicitante());
        assertEquals("He subido el documento firmado correctamente.", response.getDescripcion());
        assertEquals(45, response.getIdDocumentoAdjunto());
        assertEquals(fechaFija, response.getFechaRegistro());
        assertEquals(fechaFija, response.getFechaActualizacion());

        // Verify that observacion was marked as SUBSANADA and updated
        assertEquals(ObservacionEstado.SUBSANADA, observacionOriginal.getEstado());
        verify(observacionRepository, times(1)).save(observacionOriginal);
        verify(subsanacionRepository, times(1)).save(any(Subsanacion.class));
    }

    @Test
    void execute_WhenObservacionNotFound_ShouldThrowObservacionNotFoundException() {
        // Arrange
        Integer idObservacion = 999;
        SubsanacionRequestDTO request = SubsanacionRequestDTO.builder()
                .idSolicitante(5)
                .descripcion("Test")
                .build();

        when(observacionRepository.findById(idObservacion)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObservacionNotFoundException.class, () -> registraSubsanacionUseCase.execute(idObservacion, request));
        verify(observacionRepository, never()).save(any(Observacion.class));
        verify(subsanacionRepository, never()).save(any(Subsanacion.class));
    }

    @Test
    void execute_WhenObservacionAlreadySubsanada_ShouldThrowSubsanacionInvalidaException() {
        // Arrange
        Integer idObservacion = 100;
        SubsanacionRequestDTO request = SubsanacionRequestDTO.builder()
                .idSolicitante(5)
                .descripcion("Test")
                .build();

        LocalDateTime fechaFija = LocalDateTime.of(2026, 6, 17, 12, 0);
        Observacion observacionSubsanada = Observacion.builder()
                .id(idObservacion)
                .idTramite(1)
                .idRevisor(10)
                .tipoObservacion(TipoObservacion.TECNICA)
                .descripcion("Falta la firma")
                .estado(ObservacionEstado.SUBSANADA)
                .rolRevisor("COORDINADOR_GRUPO")
                .fechaRegistro(fechaFija)
                .fechaActualizacion(fechaFija)
                .build();

        when(observacionRepository.findById(idObservacion)).thenReturn(Optional.of(observacionSubsanada));

        // Act & Assert
        assertThrows(SubsanacionInvalidaException.class, () -> registraSubsanacionUseCase.execute(idObservacion, request));
        verify(observacionRepository, never()).save(any(Observacion.class));
        verify(subsanacionRepository, never()).save(any(Subsanacion.class));
    }
}
