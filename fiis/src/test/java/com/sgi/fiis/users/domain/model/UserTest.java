package com.sgi.fiis.users.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Unit Tests")
class UserTest {

    @Test
    @DisplayName("Should generate correct institutional email for normal names")
    void testGenerateInstitutionalEmailNormal() {
        User user = User.builder()
                .firstNames("Juan Carlos")
                .lastNames("Perez Gomez")
                .build();

        user.generateInstitutionalEmail();

        assertEquals("juan.perez@unas.edu.pe", user.getInstitutionalEmail());
    }

    @Test
    @DisplayName("Should normalize accents and ñ when generating institutional email")
    void testGenerateInstitutionalEmailNormalization() {
        User user = User.builder()
                .firstNames("María José")
                .lastNames("Nuñez Díaz")
                .build();

        user.generateInstitutionalEmail();

        assertEquals("maria.nunez@unas.edu.pe", user.getInstitutionalEmail());
    }

    @Test
    @DisplayName("Should not overwrite existing institutional email")
    void testGenerateInstitutionalEmailDoesNotOverwrite() {
        User user = User.builder()
                .firstNames("Pedro")
                .lastNames("Alba")
                .institutionalEmail("pedro.alba.personal@gmail.com")
                .build();

        user.generateInstitutionalEmail();

        assertEquals("pedro.alba.personal@gmail.com", user.getInstitutionalEmail());
    }

    @Test
    @DisplayName("Should activate and deactivate user correctly")
    void testActivateAndDeactivate() {
        User user = User.builder()
                .active(false)
                .build();

        user.activate();
        assertTrue(user.isActive());
        assertNotNull(user.getUpdatedAt());

        user.deactivate();
        assertFalse(user.isActive());
    }

    @Test
    @DisplayName("Should handle change password flows")
    void testPasswordChangeFlow() {
        User user = User.builder()
                .mustChangePassword(false)
                .build();

        user.markPasswordChangeRequired();
        assertTrue(user.isMustChangePassword());

        user.confirmPasswordChange();
        assertFalse(user.isMustChangePassword());
    }
}
