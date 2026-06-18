package com.sgi.fiis.auth.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handler que se ejecuta tras un fallo en la autenticación OAuth2 con Microsoft.
 * Registra el error en consola y redirige al frontend con detalles del error.
 */
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2FailureHandler.class);

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.error("Error de autenticación OAuth2 con Microsoft: {}", exception.getMessage(), exception);

        // Redirigir al frontend con el mensaje de error para que la interfaz pueda mostrarlo
        String redirectUrl = frontendUrl + "/login?error=" + URLEncoder.encode(exception.getLocalizedMessage(), StandardCharsets.UTF_8);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
