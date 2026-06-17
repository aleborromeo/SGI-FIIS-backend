package com.sgi.fiis.auth.infrastructure.security;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.application.usecase.OAuthLoginUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handler que se ejecuta tras una autenticación OAuth2 exitosa con Microsoft.
 * Extrae los datos del usuario OIDC, ejecuta el caso de uso OAuthLogin,
 * y redirige al frontend con el JWT generado.
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuthLoginUseCase oAuthLoginUseCase;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(OAuthLoginUseCase oAuthLoginUseCase) {
        this.oAuthLoginUseCase = oAuthLoginUseCase;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String email = oidcUser.getEmail();
        if (email == null || email.trim().isEmpty()) {
            email = oidcUser.getPreferredUsername();
        }
        if (email == null || email.trim().isEmpty()) {
            email = oidcUser.getClaimAsString("upn");
        }
        if (email == null || email.trim().isEmpty()) {
            email = oidcUser.getClaimAsString("email");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("No se pudo obtener el correo institucional del usuario desde Microsoft Entra ID.");
        }

        String name = oidcUser.getFullName();
        if (name == null || name.trim().isEmpty()) {
            name = oidcUser.getClaimAsString("name");
        }
        if (name == null || name.trim().isEmpty()) {
            name = email.split("@")[0];
        }

        LoginResponseDto result = oAuthLoginUseCase.execute(email, name, "microsoft");

        // Redirigir al frontend con los datos del token
        // En producción se recomienda usar cookies HttpOnly en lugar de query params
        String redirectUrl = frontendUrl + "/auth/callback"
                + "?token=" + URLEncoder.encode(result.getToken(), StandardCharsets.UTF_8)
                + "&correo=" + URLEncoder.encode(result.getCorreo(), StandardCharsets.UTF_8)
                + "&nombres=" + URLEncoder.encode(result.getNombres(), StandardCharsets.UTF_8)
                + "&apellidos=" + URLEncoder.encode(result.getApellidos(), StandardCharsets.UTF_8)
                + "&rolCodigo=" + URLEncoder.encode(result.getRolCodigo(), StandardCharsets.UTF_8);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
