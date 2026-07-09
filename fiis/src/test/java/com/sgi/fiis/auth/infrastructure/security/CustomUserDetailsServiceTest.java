package com.sgi.fiis.auth.infrastructure.security;

import com.sgi.fiis.users.infrastructure.persistence.RoleEntity;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Unit Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private SpringDataUserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    @DisplayName("Should load user successfully by username")
    void testLoadUserByUsernameSuccess() {
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setCode("ADMIN");
        role.setDescription("Administrador");

        UserEntity entity = new UserEntity();
        entity.setId(42L);
        entity.setInstitutionalEmail("admin@unas.edu.pe");
        entity.setPasswordHash("hashed-password");
        entity.setActive(true);
        entity.setRole(role);

        when(userRepository.findByInstitutionalEmail("admin@unas.edu.pe")).thenReturn(Optional.of(entity));

        UserDetails userDetails = service.loadUserByUsername("admin@unas.edu.pe");

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        CustomUserDetails customDetails = (CustomUserDetails) userDetails;
        assertEquals(42L, customDetails.getId());
        assertEquals("admin@unas.edu.pe", customDetails.getUsername());
        assertEquals("hashed-password", customDetails.getPassword());
        assertTrue(customDetails.isEnabled());
        assertTrue(customDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(userRepository).findByInstitutionalEmail("admin@unas.edu.pe");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByInstitutionalEmail("notfound@unas.edu.pe")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                service.loadUserByUsername("notfound@unas.edu.pe"));

        verify(userRepository).findByInstitutionalEmail("notfound@unas.edu.pe");
    }
}
