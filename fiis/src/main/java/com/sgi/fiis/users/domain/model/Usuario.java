package com.sgi.fiis.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad de dominio pura para Usuario.
 * Contiene la lógica de negocio del usuario (Clean Architecture).
 * Sin anotaciones de JPA ni Spring.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String correoInstitucional;
    private String telefono;
    private String passwordHash;
    private boolean activo;
    private boolean mustChangePassword;
    private String rolCodigo;
    private String rolDescripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String oauthProvider;

    // ========== Lógica de negocio ==========

    /**
     * Activa al usuario (RF-11).
     */
    public void activar() {
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Desactiva al usuario sin eliminar su registro (RF-11).
     */
    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Genera el correo institucional automáticamente (RF-10).
     * Formato: primer_nombre.primer_apellido@unas.edu.pe
     */
    public void generarCorreoInstitucional() {
        if (this.correoInstitucional != null && !this.correoInstitucional.isBlank()) {
            return; // Ya tiene correo asignado manualmente
        }

        String primerNombre = this.nombres.trim().split("\\s+")[0].toLowerCase();
        String primerApellido = this.apellidos.trim().split("\\s+")[0].toLowerCase();

        // Normalizar caracteres especiales (ñ, tildes)
        primerNombre = normalizarTexto(primerNombre);
        primerApellido = normalizarTexto(primerApellido);

        this.correoInstitucional = primerNombre + "." + primerApellido + "@unas.edu.pe";
    }

    /**
     * Marca que el usuario debe cambiar su contraseña (RF-06).
     */
    public void marcarCambioPasswordObligatorio() {
        this.mustChangePassword = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Confirma que el usuario ya cambió su contraseña.
     */
    public void confirmarCambioPassword() {
        this.mustChangePassword = false;
        this.fechaActualizacion = LocalDateTime.now();
    }

    private String normalizarTexto(String texto) {
        return texto
                .replace("á", "a").replace("é", "e")
                .replace("í", "i").replace("ó", "o")
                .replace("ú", "u").replace("ñ", "n");
    }
}
