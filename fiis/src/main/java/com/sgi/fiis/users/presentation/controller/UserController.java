package com.sgi.fiis.users.presentation.controller;

import com.sgi.fiis.users.application.dto.UserRequestDto;
import com.sgi.fiis.users.application.dto.UserResponseDto;
import com.sgi.fiis.users.application.dto.UserUpdateDto;
import com.sgi.fiis.users.application.usecase.*;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.presentation.mapper.UserMapper;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ToggleUserStatusUseCase toggleUserStatusUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UserMapper mapper;

    public UserController(CreateUserUseCase createUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          ToggleUserStatusUseCase toggleUserStatusUseCase,
                          ResetPasswordUseCase resetPasswordUseCase,
                          ListUsersUseCase listUsersUseCase,
                          GetUserUseCase getUserUseCase,
                          UserMapper mapper) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.toggleUserStatusUseCase = toggleUserStatusUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.getUserUseCase = getUserUseCase;
        this.mapper = mapper;
    }

    /** RF-07: Register user */
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto dto) {
        User user = mapper.toDomain(dto);
        User created = createUserUseCase.execute(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDto(created));
    }

    /** RF-08: Edit user */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id,
                                                     @Valid @RequestBody UserUpdateDto dto) {
        User updated = updateUserUseCase.execute(
                id, dto.getFirstNames(), dto.getLastNames(),
                dto.getInstitutionalEmail(), dto.getPhone(), dto.getRoleCode()
        );
        return ResponseEntity.ok(mapper.toResponseDto(updated));
    }

    /** RF-14: List and search users */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> list(
            @RequestParam(required = false) String query) {
        List<UserResponseDto> users = listUsersUseCase.execute(query).stream()
                .map(mapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    /** Get user by ID */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> get(@PathVariable Long id) {
        User user = getUserUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponseDto(user));
    }

    /** RF-11: Activate or deactivate user */
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> toggleStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        boolean activate = body.getOrDefault("active", true);
        Long authenticatedUserId = 0L;
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            authenticatedUserId = customUserDetails.getId();
        }
        User updated = toggleUserStatusUseCase.execute(id, activate, authenticatedUserId);
        return ResponseEntity.ok(mapper.toResponseDto(updated));
    }

    /** RF-12: Reset password */
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id) {
        resetPasswordUseCase.execute(id);
        return ResponseEntity.ok(Map.of("message", "Contraseña reiniciada exitosamente"));
    }
}
