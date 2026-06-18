package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.domain.port.LineaInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoLineaUseCaseTest {

    @Mock
    private LineaInvestigacionRepositoryPort lineaRepository;

    @InjectMocks
    private CambiarEstadoLineaUseCase useCase;

    @Test
    void execute_deberiaActivarLinea() {
        LineaInvestigacion linea = LineaInvestigacion.builder().id(1).nombreLinea("IA").esActiva(false).build();
        given(lineaRepository.findById(1)).willReturn(Optional.of(linea));
        given(lineaRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        LineaInvestigacion result = useCase.execute(1, true);

        assertThat(result.isEsActiva()).isTrue();
        assertThat(result.getFechaActualizacion()).isNotNull();
    }

    @Test
    void execute_deberiaDesactivarLinea() {
        LineaInvestigacion linea = LineaInvestigacion.builder().id(1).nombreLinea("IA").esActiva(true).build();
        given(lineaRepository.findById(1)).willReturn(Optional.of(linea));
        given(lineaRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        LineaInvestigacion result = useCase.execute(1, false);

        assertThat(result.isEsActiva()).isFalse();
        assertThat(result.getFechaActualizacion()).isNotNull();
    }

    @Test
    void execute_deberiaLanzarExcepcion_cuandoLineaNoExiste() {
        given(lineaRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99, true))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
