package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CrearGrupoUseCaseTest {

    @Mock
    private GrupoInvestigacionRepositoryPort grupoRepository;

    @InjectMocks
    private CrearGrupoUseCase useCase;

    @Test
    void execute_deberiaLanzarExcepcion_cuandoCodigoYaExiste() {
        GrupoInvestigacion input = GrupoInvestigacion.builder()
                .codigoGrupo("GI-001").nombreGrupo("Grupo Test").build();
        given(grupoRepository.existsByCodigo("GI-001")).willReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("GI-001");

        then(grupoRepository).should(never()).save(any());
    }

    @Test
    void execute_deberiaGuardarGrupo_conEstadoActivo() {
        GrupoInvestigacion input = GrupoInvestigacion.builder()
                .codigoGrupo("GI-002").nombreGrupo("Nuevo Grupo").build();
        GrupoInvestigacion saved = GrupoInvestigacion.builder()
                .id(1).codigoGrupo("GI-002").nombreGrupo("Nuevo Grupo").esActivo(true).build();

        given(grupoRepository.existsByCodigo("GI-002")).willReturn(false);
        given(grupoRepository.save(any())).willReturn(saved);

        GrupoInvestigacion result = useCase.execute(input);

        assertThat(result.isEsActivo()).isTrue();
        assertThat(result.getId()).isEqualTo(1);
        then(grupoRepository).should().save(any(GrupoInvestigacion.class));
    }
}
