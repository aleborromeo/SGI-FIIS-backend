package com.sgi.fiis.auth.domain.port;

import com.sgi.fiis.users.domain.model.User;

/**
 * Domain port for handling users authenticated via OAuth.
 * Finds an existing user by email or creates a new one with provider data.
 */
public interface OAuthUserHandlerPort {
    User findOrCreateFromOAuth(String email, String name, String provider);
}
