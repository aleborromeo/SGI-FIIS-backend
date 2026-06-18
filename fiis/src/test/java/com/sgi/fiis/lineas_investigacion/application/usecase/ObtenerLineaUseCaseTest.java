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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerLineaUseCaseTest {

    @Mock
    private LineaInvestigacionRepositoryPort lineaRepository;

    @InjectMocks
    private ObtenerLineaUseCase useCase;

    @Test
    void execute_deberiaRetornarLinea_cuandoExiste() {
        LineaInvestigacion linea = LineaInvestigacion.builder().id(1).nombreLinea("IA").build();
        given(lineaRepository.findById(1)).willReturn(Optional.of(linea));

        LineaInvestigacion result = useCase.execute(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getNombreLinea()).isEqualTo("IA");
    }

    @Test
    void execute_deberiaLanzarExcepcion_cuandoNoExiste() {
        given(lineaRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
