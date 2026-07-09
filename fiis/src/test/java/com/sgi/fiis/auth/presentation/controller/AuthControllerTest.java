package com.sgi.fiis.auth.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgi.fiis.auth.application.dto.*;
import com.sgi.fiis.auth.application.usecase.*;
import com.sgi.fiis.users.application.dto.UserResponseDto;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import com.sgi.fiis.users.presentation.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;

import java.util.Locale;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;
    private LoginUseCase loginUseCase;
    private RegisterUseCase registerUseCase;
    private VerifyRegistrationUseCase verifyRegistrationUseCase;
    private ResendCodeUseCase resendCodeUseCase;
    private ChangePasswordUseCase changePasswordUseCase;
    private UserRepositoryPort userRepository;
    private UserMapper userMapper;
    private MessageSource messageSource;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        loginUseCase = mock(LoginUseCase.class);
        registerUseCase = mock(RegisterUseCase.class);
        verifyRegistrationUseCase = mock(VerifyRegistrationUseCase.class);
        resendCodeUseCase = mock(ResendCodeUseCase.class);
        changePasswordUseCase = mock(ChangePasswordUseCase.class);
        userRepository = mock(UserRepositoryPort.class);
        userMapper = mock(UserMapper.class);
        messageSource = mock(MessageSource.class);

        AuthController controller = new AuthController(
                loginUseCase, registerUseCase, verifyRegistrationUseCase, resendCodeUseCase,
                changePasswordUseCase, userRepository, userMapper, messageSource
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(Authentication.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        Authentication auth = mock(Authentication.class);
                        when(auth.getName()).thenReturn("test@test.com");
                        return auth;
                    }
                })
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_Success() throws Exception {
        LoginRequestDto req = new LoginRequestDto("test@test.com", "pass");
        LoginResponseDto res = LoginResponseDto.builder()
                .token("token")
                .email("test@test.com")
                .roleCode("ADMIN")
                .firstNames("TestUser")
                .build();
        when(loginUseCase.execute("test@test.com", "pass")).thenReturn(res);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    void login_InvalidRequest() throws Exception {
        LoginRequestDto req = new LoginRequestDto("", "");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequestDto req = RegisterRequestDto.builder()
                .dni("12345678")
                .firstNames("First")
                .lastNames("Last")
                .institutionalEmail("test@unas.edu.pe")
                .phone("123456789")
                .password("Pass123!")
                .confirmPassword("Pass123!")
                .roleCode("DOCENTE")
                .build();
        when(messageSource.getMessage(eq("auth.register.success"), eq(null), any(Locale.class))).thenReturn("Msg");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Msg"));
    }
    
    @Test
    void register_InvalidRequest() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("invalid", "123", "", "", "", "", "", "");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resendCode_Success() throws Exception {
        ResendCodeRequestDto req = new ResendCodeRequestDto("test@test.com");
        when(messageSource.getMessage(eq("auth.resend-code.success"), eq(null), any(Locale.class))).thenReturn("Msg");

        mockMvc.perform(post("/api/v1/auth/resend-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Msg"));
    }

    @Test
    void verifyRegistration_Success() throws Exception {
        VerifyRegistrationRequestDto req = new VerifyRegistrationRequestDto("test@test.com", "123456");
        LoginResponseDto res = LoginResponseDto.builder()
                .token("token")
                .email("test@test.com")
                .roleCode("ADMIN")
                .firstNames("TestUser")
                .build();
        when(verifyRegistrationUseCase.execute("test@test.com", "123456")).thenReturn(res);

        mockMvc.perform(post("/api/v1/auth/verify-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    void changePassword_Success() throws Exception {
        ChangePasswordDto req = new ChangePasswordDto("oldPass", "NewPass123!");
        when(messageSource.getMessage(eq("auth.change-password.success"), eq(null), any(Locale.class))).thenReturn("Msg");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@test.com");

        mockMvc.perform(post("/api/v1/auth/change-password")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Msg"));
    }

    @Test
    void getProfile_Success() throws Exception {
        User user = mock(User.class);
        UserResponseDto dto = UserResponseDto.builder()
                .id(1L)
                .institutionalEmail("test@test.com")
                .firstNames("Name")
                .roleCode("ADMIN")
                .build();
        
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(dto);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@test.com");

        mockMvc.perform(get("/api/v1/auth/profile")
                .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.institutionalEmail").value("test@test.com"));
    }

    @Test
    void getProfile_UserNotFound() throws Exception {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@test.com");

        try {
            mockMvc.perform(get("/api/v1/auth/profile").principal(auth));
            org.junit.jupiter.api.Assertions.fail("Expected exception");
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.assertTrue(e.getCause() instanceof RuntimeException);
            org.junit.jupiter.api.Assertions.assertEquals("Usuario no encontrado", e.getCause().getMessage());
        }
    }
}
