package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
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
class AsignarCoordinadorUseCaseTest {

    @Mock
    private GrupoInvestigacionRepositoryPort grupoRepository;

    @InjectMocks
    private AsignarCoordinadorUseCase useCase;

    @Test
    void execute_deberiaLanzarExcepcion_cuandoGrupoNoExiste() {
        given(grupoRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void execute_deberiaLanzarExcepcion_cuandoUsuarioNoActivo() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).codigoGrupo("GI-001").build();
        given(grupoRepository.findById(1)).willReturn(Optional.of(grupo));
        given(grupoRepository.existeUsuarioActivo(5)).willReturn(false);

        assertThatThrownBy(() -> useCase.execute(1, 5))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("5");
    }

    @Test
    void execute_deberiaAsignarCoordinador_cuandoDatosValidos() {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder()
                .id(1).codigoGrupo("GI-001").nombreGrupo("Grupo A").esActivo(true).build();
        GrupoInvestigacion actualizado = GrupoInvestigacion.builder()
                .id(1).codigoGrupo("GI-001").nombreGrupo("Grupo A")
                .idCoordinadorActual(3).coordinadorNombres("Juan").coordinadorApellidos("Perez")
                .esActivo(true).build();

        given(grupoRepository.findById(1)).willReturn(Optional.of(grupo));
        given(grupoRepository.existeUsuarioActivo(3)).willReturn(true);
        given(grupoRepository.save(any())).willReturn(actualizado);

        GrupoInvestigacion result = useCase.execute(1, 3);

        assertThat(result.getIdCoordinadorActual()).isEqualTo(3);
        assertThat(result.getCoordinadorNombres()).isEqualTo("Juan");
    }
}
