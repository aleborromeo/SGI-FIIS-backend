package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.domain.port.LineaInvestigacionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ListarLineasUseCaseTest {

    @Mock
    private LineaInvestigacionRepositoryPort lineaRepository;

    @InjectMocks
    private ListarLineasUseCase useCase;

    @Test
    void execute_deberiaDevolverSoloActivas_cuandoFlagEsTrue() {
        List<LineaInvestigacion> activas = List.of(
                LineaInvestigacion.builder().id(1).nombreLinea("IA").esActiva(true).build()
        );
        given(lineaRepository.findAllActivas()).willReturn(activas);

        List<LineaInvestigacion> result = useCase.execute(true);

        assertThat(result).hasSize(1).allMatch(LineaInvestigacion::isEsActiva);
        then(lineaRepository).should().findAllActivas();
        then(lineaRepository).should(never()).findAll();
    }

    @Test
    void execute_deberiaDevolverTodas_cuandoFlagEsFalse() {
        List<LineaInvestigacion> todas = List.of(
                LineaInvestigacion.builder().id(1).nombreLinea("IA").esActiva(true).build(),
                LineaInvestigacion.builder().id(2).nombreLinea("Antigua").esActiva(false).build()
        );
        given(lineaRepository.findAll()).willReturn(todas);

        List<LineaInvestigacion> result = useCase.execute(false);

        assertThat(result).hasSize(2);
        then(lineaRepository).should().findAll();
        then(lineaRepository).should(never()).findAllActivas();
    }
}
