package com.sgi.fiis.tramites.presentation.controller;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.tramites.application.dto.*;
import com.sgi.fiis.tramites.application.usecase.*;
import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.TipoTramite;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TramiteController - Capa de presentación")
class TramiteControllerTest {

    @Mock private CrearTramiteUseCase crearTramiteUseCase;
    @Mock private AprobarTramiteUseCase aprobarTramiteUseCase;
    @Mock private ObservarTramiteUseCase observarTramiteUseCase;
    @Mock private SubsanarTramiteUseCase subsanarTramiteUseCase;
    @Mock private RechazarTramiteUseCase rechazarTramiteUseCase;
    @Mock private RegistrarResolucionUseCase registrarResolucionUseCase;
    @Mock private ConsultarTrazabilidadUseCase consultarTrazabilidadUseCase;

    @InjectMocks
    private TramiteController controller;

    private CustomUserDetails coordinador;
    private CustomUserDetails estudiante;

    @BeforeEach
    void setUp() {
        coordinador = new CustomUserDetails(10L, "coord@unas.edu.pe", "pwd", true,
                List.of(new SimpleGrantedAuthority("ROLE_COORDINADOR_GRUPO")));
        estudiante = new CustomUserDetails(5L, "est@unas.edu.pe", "pwd", true,
                List.of(new SimpleGrantedAuthority("ROLE_ESTUDIANTE")));
    }

    @Test
    @DisplayName("crear: asigna idSolicitante desde el JWT, no desde el body")
    void crear_asignaIdSolicitanteDesdeJwt_retorna201() {
        TramiteRequestDto dto = TramiteRequestDto.builder()
                .tipoTramite(TipoTramite.PLAN_TESIS)
                .idReferenciaTesis(1L)
                .build();
        TramiteResponseDto esperado = TramiteResponseDto.builder()
                .id(1L)
                .estadoActual(EstadoTramite.PENDIENTE_COORDINADOR)
                .build();
        when(crearTramiteUseCase.execute(any())).thenReturn(esperado);

        ResponseEntity<TramiteResponseDto> response = controller.crear(dto, estudiante);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(5L, dto.getIdSolicitante()); // seguridad: viene del JWT, no del cuerpo HTTP
        assertSame(esperado, response.getBody());
        verify(crearTramiteUseCase).execute(dto);
    }

    @Test
    @DisplayName("aprobar: extrae RoleEnum del JWT y delega correctamente al use case")
    void aprobar_extraeRolDelJwt_retorna200() {
        TramiteResponseDto esperado = TramiteResponseDto.builder().id(1L).build();
        when(aprobarTramiteUseCase.execute(eq(1L), eq(RoleEnum.COORDINADOR_GRUPO), eq(10L)))
                .thenReturn(esperado);

        ResponseEntity<TramiteResponseDto> response = controller.aprobar(1L, coordinador);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(esperado, response.getBody());
    }

    @Test
    @DisplayName("aprobar: usuario sin rol lanza BusinessException antes de llamar al use case")
    void aprobar_usuarioSinRol_lanzaBusinessException() {
        CustomUserDetails sinRol = new CustomUserDetails(1L, "x@x.com", "pwd", true, List.of());

        assertThrows(BusinessException.class, () -> controller.aprobar(1L, sinRol));
        verifyNoInteractions(aprobarTramiteUseCase);
    }

    @Test
    @DisplayName("observar: pasa el texto de observación al use case")
    void observar_pasaTextoAlUseCase_retorna200() {
        ObservarTramiteRequestDto body = new ObservarTramiteRequestDto("Falta firma del asesor");
        TramiteResponseDto esperado = TramiteResponseDto.builder().id(2L).build();
        when(observarTramiteUseCase.execute(
                eq(2L), eq(RoleEnum.COORDINADOR_GRUPO), eq(10L), eq("Falta firma del asesor")))
                .thenReturn(esperado);

        ResponseEntity<TramiteResponseDto> response = controller.observar(2L, body, coordinador);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(esperado, response.getBody());
    }

    @Test
    @DisplayName("subsanar: pasa el detalle de subsanación y el id del solicitante desde JWT")
    void subsanar_pasaDetalleYIdSolicitante_retorna200() {
        SubsanarTramiteRequestDto body = new SubsanarTramiteRequestDto("Adjuntada firma escaneada");
        TramiteResponseDto esperado = TramiteResponseDto.builder().id(3L).build();
        when(subsanarTramiteUseCase.execute(eq(3L), eq(5L), eq("Adjuntada firma escaneada")))
                .thenReturn(esperado);

        ResponseEntity<TramiteResponseDto> response = controller.subsanar(3L, body, estudiante);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("rechazar: extrae RoleEnum del JWT y retorna 200")
    void rechazar_extraeRolDelJwt_retorna200() {
        TramiteResponseDto esperado = TramiteResponseDto.builder().id(4L).build();
        when(rechazarTramiteUseCase.execute(eq(4L), eq(RoleEnum.COORDINADOR_GRUPO), eq(10L)))
                .thenReturn(esperado);

        ResponseEntity<TramiteResponseDto> response = controller.rechazar(4L, coordinador);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("registrarResolucion: pasa solo el id del usuario desde JWT")
    void registrarResolucion_pasaIdUsuarioDesdeJwt_retorna200() {
        TramiteResponseDto esperado = TramiteResponseDto.builder().id(5L).build();
        when(registrarResolucionUseCase.execute(eq(5L), eq(10L))).thenReturn(esperado);

        ResponseEntity<TramiteResponseDto> response = controller.registrarResolucion(5L, coordinador);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("consultarTrazabilidad: retorna la lista de movimientos sin autenticación de rol")
    void consultarTrazabilidad_retornaMovimientos_sinRolRequerido() {
        List<MovimientoResponseDto> movimientos = List.of(
                MovimientoResponseDto.builder().accion("PRESENTADO_POR_SOLICITANTE").build(),
                MovimientoResponseDto.builder().accion("APROBADO_COORDINADOR").build()
        );
        when(consultarTrazabilidadUseCase.execute(6L)).thenReturn(movimientos);

        ResponseEntity<List<MovimientoResponseDto>> response = controller.consultarTrazabilidad(6L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }
}
