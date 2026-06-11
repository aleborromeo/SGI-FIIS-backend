package com.sgi.fiis.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio pura para Rol.
 * Sin anotaciones de JPA ni Spring.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    private Long id;
    private String codigoRol;
    private String descripcion;
}
