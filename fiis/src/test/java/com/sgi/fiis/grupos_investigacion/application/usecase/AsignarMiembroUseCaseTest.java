package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import com.sgi.fiis.grupos_investigacion.domain.port.MembresiaRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
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
class AsignarMiembroUseCaseTest {

    @Mock
    private GrupoInvestigacionRepositoryPort grupoRepository;

    @Mock
    private MembresiaRepositoryPort membresiaRepository;

    @InjectMocks
    private AsignarMiembroUseCase useCase;

    @Test
    void execute_deberiaLanzarExcepcion_cuandoGrupoNoExiste() {
        given(grupoRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99, 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void execute_deberiaLanzarExcepcion_cuandoUsuarioNoActivo() {
        given(grupoRepository.findById(1)).willReturn(Optional.of(
                GrupoInvestigacion.builder().id(1).build()));
        given(grupoRepository.existeUsuarioActivo(5)).willReturn(false);

        assertThatThrownBy(() -> useCase.execute(1, 5))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("5");
    }

    @Test
    void execute_deberiaLanzarExcepcion_cuandoUsuarioYaTieneMembresia_RF21() {
        given(grupoRepository.findById(1)).willReturn(Optional.of(
                GrupoInvestigacion.builder().id(1).build()));
        given(grupoRepository.existeUsuarioActivo(2)).willReturn(true);
        given(membresiaRepository.existsActivaByUsuario(2)).willReturn(true);

        assertThatThrownBy(() -> useCase.execute(1, 2))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("RF-21");
    }

    @Test
    void execute_deberiaCrearMembresia_cuandoDatosValidos() {
        Membresia saved = Membresia.builder()
                .id(10).idGrupo(1).idUsuario(2).esActivo(true)
                .fechaInicio(LocalDateTime.now()).build();

        given(grupoRepository.findById(1)).willReturn(Optional.of(
                GrupoInvestigacion.builder().id(1).build()));
        given(grupoRepository.existeUsuarioActivo(2)).willReturn(true);
        given(membresiaRepository.existsActivaByUsuario(2)).willReturn(false);
        given(membresiaRepository.save(any())).willReturn(saved);

        Membresia result = useCase.execute(1, 2);

        assertThat(result.isEsActivo()).isTrue();
        assertThat(result.getIdGrupo()).isEqualTo(1);
        assertThat(result.getIdUsuario()).isEqualTo(2);
        assertThat(result.getFechaInicio()).isNotNull();
    }
}
