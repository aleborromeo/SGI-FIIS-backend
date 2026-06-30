package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.domain.port.OAuthUserHandlerPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.users.domain.model.User;
import org.springframework.stereotype.Service;

/**
 * Use case: Login via OAuth2 (Microsoft).
 * Finds or creates the user from OAuth provider data,
 * and generates a system JWT.
 */
@Service
public class OAuthLoginUseCase {

    private final OAuthUserHandlerPort oAuthUserHandler;
    private final TokenProviderPort tokenProvider;

    public OAuthLoginUseCase(OAuthUserHandlerPort oAuthUserHandler,
                             TokenProviderPort tokenProvider) {
        this.oAuthUserHandler = oAuthUserHandler;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Executes the OAuth flow: finds/creates user and generates JWT.
     *
     * @param email    user email from OAuth provider
     * @param name     user full name
     * @param provider provider name (e.g. "microsoft")
     * @return LoginResponseDto with JWT token and user details
     */
    public LoginResponseDto execute(String email, String name, String provider) {
        User user = oAuthUserHandler.findOrCreateFromOAuth(email, name, provider);

        String token = tokenProvider.generateToken(
                user.getInstitutionalEmail(),
                user.getRoleCode()
        );

        return LoginResponseDto.builder()
                .token(token)
                .type("Bearer")
                .email(user.getInstitutionalEmail())
                .firstNames(user.getFirstNames())
                .lastNames(user.getLastNames())
                .roleCode(user.getRoleCode())
                .mustChangePassword(false) // OAuth users do not need to change password
                .build();
    }
}
