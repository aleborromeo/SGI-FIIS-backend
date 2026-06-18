package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.domain.port.LineaInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarLineaUseCaseTest {

    @Mock
    private LineaInvestigacionRepositoryPort lineaRepository;

    @InjectMocks
    private RegistrarLineaUseCase useCase;

    @Test
    void execute_deberiaLanzarExcepcion_cuandoNombreYaExiste() {
        LineaInvestigacion input = LineaInvestigacion.builder().nombreLinea("IA Aplicada").build();
        given(lineaRepository.existsByNombre("IA Aplicada")).willReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("IA Aplicada");

        then(lineaRepository).should(never()).save(any());
    }

    @Test
    void execute_deberiaGuardarLinea_conEstadoActivoYFechas() {
        LineaInvestigacion input = LineaInvestigacion.builder().nombreLinea("Robótica").build();
        LineaInvestigacion saved = LineaInvestigacion.builder()
                .id(1)
                .nombreLinea("Robótica")
                .esActiva(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        given(lineaRepository.existsByNombre("Robótica")).willReturn(false);
        given(lineaRepository.save(any())).willReturn(saved);

        LineaInvestigacion result = useCase.execute(input);

        assertThat(result.isEsActiva()).isTrue();
        assertThat(result.getFechaCreacion()).isNotNull();
        assertThat(result.getFechaActualizacion()).isNotNull();
        then(lineaRepository).should().save(any(LineaInvestigacion.class));
    }
}
