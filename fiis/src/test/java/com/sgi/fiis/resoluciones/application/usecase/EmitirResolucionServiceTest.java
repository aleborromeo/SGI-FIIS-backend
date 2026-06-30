package com.sgi.fiis.resoluciones.application.usecase;

import com.sgi.fiis.resoluciones.domain.model.Resolucion;
import com.sgi.fiis.resoluciones.domain.port.in.EmitirResolucionCommand;
import com.sgi.fiis.resoluciones.domain.port.out.DocumentoStoragePort;
import com.sgi.fiis.resoluciones.domain.port.out.ResolucionRepositoryPort;
import com.sgi.fiis.resoluciones.domain.port.out.TramiteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmitirResolucionServiceTest {

    @Mock
    private ResolucionRepositoryPort resolucionRepositoryPort;

    @Mock
    private TramiteRepositoryPort tramiteRepositoryPort;

    @Mock
    private DocumentoStoragePort documentoStoragePort;

    @InjectMocks
    private EmitirResolucionService emitirResolucionService;

    private EmitirResolucionCommand command;

    @BeforeEach
    void setUp() {
        command = new EmitirResolucionCommand(
                "RES-2023-001",
                LocalDate.now(),
                "Aprobación de tesis",
                1L,
                new byte[]{1, 2, 3},
                "resolucion.pdf",
                "application/pdf"
        );
    }

    @Test
    void emitir_CuandoTodoEsValido_DebeGuardarYRetornarResolucion() {
        // Arrange
        when(tramiteRepositoryPort.existeTramite(1L)).thenReturn(true);
        when(resolucionRepositoryPort.existePorNumero("RES-2023-001")).thenReturn(false);
        when(documentoStoragePort.guardarDocumento(any(), any(), any())).thenReturn(100L);
        
        Resolucion resolucionSimulada = new Resolucion(
                1L, "RES-2023-001", LocalDate.now(), "Aprobación de tesis", 1L, 100L, LocalDateTime.now()
        );
        when(resolucionRepositoryPort.guardar(any(Resolucion.class))).thenReturn(resolucionSimulada);

        // Act
        Resolucion resultado = emitirResolucionService.emitir(command);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.idResolucion());
        assertEquals("RES-2023-001", resultado.numeroResolucion());
        
        verify(tramiteRepositoryPort).existeTramite(1L);
        verify(resolucionRepositoryPort).existePorNumero("RES-2023-001");
        verify(documentoStoragePort).guardarDocumento(any(), any(), any());
        verify(resolucionRepositoryPort).guardar(any(Resolucion.class));
        verify(tramiteRepositoryPort).actualizarEstadoAAprobadoConResolucion(1L);
    }

    @Test
    void emitir_CuandoTramiteNoExiste_DebeLanzarExcepcion() {
        // Arrange
        when(tramiteRepositoryPort.existeTramite(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emitirResolucionService.emitir(command);
        });
        
        assertEquals("El trámite especificado no existe.", exception.getMessage());
        
        // Verificar que no se llaman a los demás métodos
        verify(resolucionRepositoryPort, never()).existePorNumero(any());
        verify(documentoStoragePort, never()).guardarDocumento(any(), any(), any());
        verify(resolucionRepositoryPort, never()).guardar(any());
    }

    @Test
    void emitir_CuandoResolucionYaExiste_DebeLanzarExcepcion() {
        // Arrange
        when(tramiteRepositoryPort.existeTramite(1L)).thenReturn(true);
        when(resolucionRepositoryPort.existePorNumero("RES-2023-001")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emitirResolucionService.emitir(command);
        });
        
        assertEquals("El número de resolución ya se encuentra registrado.", exception.getMessage());
        
        // Verificar que no se guardan documentos ni la resolución
        verify(documentoStoragePort, never()).guardarDocumento(any(), any(), any());
        verify(resolucionRepositoryPort, never()).guardar(any());
    }
}
