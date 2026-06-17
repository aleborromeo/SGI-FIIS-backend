package com.sgi.fiis.users.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para respuestas de usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDto {
    private Long id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String correoInstitucional;
    private String telefono;
    private boolean activo;
    private boolean mustChangePassword;
    private String rolCodigo;
    private String rolDescripcion;
    private String fechaCreacion;
    private String fechaActualizacion;
}
