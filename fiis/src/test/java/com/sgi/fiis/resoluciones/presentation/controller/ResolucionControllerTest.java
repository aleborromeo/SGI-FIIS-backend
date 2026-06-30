package com.sgi.fiis.resoluciones.presentation.controller;

import com.sgi.fiis.resoluciones.domain.model.Resolucion;
import com.sgi.fiis.resoluciones.domain.port.in.EmitirResolucionCommand;
import com.sgi.fiis.resoluciones.domain.port.in.EmitirResolucionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ResolucionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmitirResolucionUseCase emitirResolucionUseCase;

    @InjectMocks
    private ResolucionController resolucionController;

    private Resolucion resolucionSimulada;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resolucionController).build();

        resolucionSimulada = new Resolucion(
                1L,
                "RES-2023-001",
                LocalDate.of(2023, 10, 1),
                "Aprobación de tesis",
                10L,
                100L,
                LocalDateTime.of(2023, 10, 1, 10, 0)
        );
    }

    @Test
    void emitirResolucion_DebeRetornar201_CuandoEsValido() throws Exception {
        when(emitirResolucionUseCase.emitir(any(EmitirResolucionCommand.class))).thenReturn(resolucionSimulada);

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Contenido PDF de prueba".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/resoluciones")
                        .file(archivo)
                        .param("numeroResolucion", "RES-2023-001")
                        .param("fechaEmision", "2023-10-01")
                        .param("asunto", "Aprobación de tesis")
                        .param("idTramite", "10")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idResolucion").value(1))
                .andExpect(jsonPath("$.numeroResolucion").value("RES-2023-001"))
                .andExpect(jsonPath("$.asunto").value("Aprobación de tesis"))
                .andExpect(jsonPath("$.idTramite").value(10));
    }
}
