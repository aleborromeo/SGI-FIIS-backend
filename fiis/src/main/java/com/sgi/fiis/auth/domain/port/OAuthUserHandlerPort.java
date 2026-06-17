package com.sgi.fiis.auth.domain.port;

import com.sgi.fiis.users.domain.model.Usuario;

/**
 * Puerto del dominio para manejo de usuarios autenticados vía OAuth.
 * Busca un usuario existente por correo o crea uno nuevo con datos del proveedor.
 */
public interface OAuthUserHandlerPort {
    Usuario findOrCreateFromOAuth(String email, String name, String provider);
}
