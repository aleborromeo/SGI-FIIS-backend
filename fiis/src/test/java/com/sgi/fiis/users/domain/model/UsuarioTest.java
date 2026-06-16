package com.sgi.fiis.users.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Usuario Domain Unit Tests")
class UsuarioTest {

    @Test
    @DisplayName("Should generate correct institutional email for normal names")
    void testGenerarCorreoInstitucionalNormal() {
        Usuario usuario = Usuario.builder()
                .nombres("Juan Carlos")
                .apellidos("Perez Gomez")
                .build();

        usuario.generarCorreoInstitucional();

        assertEquals("juan.perez@unas.edu.pe", usuario.getCorreoInstitucional());
    }

    @Test
    @DisplayName("Should normalize accents and ñ when generating institutional email")
    void testGenerarCorreoInstitucionalNormalizacion() {
        Usuario usuario = Usuario.builder()
                .nombres("María José")
                .apellidos("Nuñez Díaz")
                .build();

        usuario.generarCorreoInstitucional();

        assertEquals("maria.nunez@unas.edu.pe", usuario.getCorreoInstitucional());
    }

    @Test
    @DisplayName("Should not overwrite existing institutional email")
    void testGenerarCorreoInstitucionalNoSobreEscribe() {
        Usuario usuario = Usuario.builder()
                .nombres("Pedro")
                .apellidos("Alba")
                .correoInstitucional("pedro.alba.personal@gmail.com")
                .build();

        usuario.generarCorreoInstitucional();

        assertEquals("pedro.alba.personal@gmail.com", usuario.getCorreoInstitucional());
    }

    @Test
    @DisplayName("Should active and deactivate user correctly")
    void testActivarYDesactivar() {
        Usuario usuario = Usuario.builder()
                .activo(false)
                .build();

        usuario.activar();
        assertTrue(usuario.isActivo());
        assertNotNull(usuario.getFechaActualizacion());

        usuario.desactivar();
        assertFalse(usuario.isActivo());
    }

    @Test
    @DisplayName("Should handle change password flows")
    void testCambioPasswordFlow() {
        Usuario usuario = Usuario.builder()
                .mustChangePassword(false)
                .build();

        usuario.marcarCambioPasswordObligatorio();
        assertTrue(usuario.isMustChangePassword());

        usuario.confirmarCambioPassword();
        assertFalse(usuario.isMustChangePassword());
    }
}
