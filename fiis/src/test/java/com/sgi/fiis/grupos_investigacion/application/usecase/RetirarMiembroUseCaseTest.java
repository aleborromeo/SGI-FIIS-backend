package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import com.sgi.fiis.grupos_investigacion.domain.port.MembresiaRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RetirarMiembroUseCaseTest {

    @Mock
    private MembresiaRepositoryPort membresiaRepository;

    @InjectMocks
    private RetirarMiembroUseCase useCase;

    @Test
    void execute_deberiaLanzarExcepcion_cuandoMembresiaActivaNoExiste() {
        given(membresiaRepository.findActivaByUsuarioEnGrupo(5, 1)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(1, 5))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void execute_deberiaRetirarMiembro_conFechaFinYEstadoFalso() {
        Membresia membresia = Membresia.builder()
                .id(10).idGrupo(1).idUsuario(5)
                .esActivo(true).fechaInicio(LocalDateTime.now()).build();

        given(membresiaRepository.findActivaByUsuarioEnGrupo(5, 1)).willReturn(Optional.of(membresia));
        given(membresiaRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Membresia result = useCase.execute(1, 5);

        assertThat(result.isEsActivo()).isFalse();
        assertThat(result.getFechaFin()).isNotNull();
    }
}
