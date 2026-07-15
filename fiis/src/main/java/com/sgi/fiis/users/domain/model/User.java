package com.sgi.fiis.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Pure domain entity for User.
 * Contains user business logic (Clean Architecture).
 * No JPA or Spring annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String dni;
    private String firstNames;
    private String lastNames;
    private String institutionalEmail;
    private String phone;
    private String passwordHash;
    private boolean active;
    private boolean mustChangePassword;
    private String roleCode;
    private String roleDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String oauthProvider;
    private String temporaryPassword;

    // ========== Business Logic ==========

    /**
     * Activates the user (RF-11).
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    /**
     * Deactivates the user without deleting their record (RF-11).
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    /**
     * Generates the institutional email automatically (RF-10).
     * Format: first_name.first_lastname@unas.edu.pe
     */
    public void generateInstitutionalEmail() {
        if (this.institutionalEmail != null && !this.institutionalEmail.isBlank()) {
            return; // Already manually assigned
        }

        String firstFirstName = this.firstNames.trim().split("\\s+")[0].toLowerCase();
        String firstLastName = this.lastNames.trim().split("\\s+")[0].toLowerCase();

        // Normalize special characters (ñ, accents)
        firstFirstName = normalizeText(firstFirstName);
        firstLastName = normalizeText(firstLastName);

        this.institutionalEmail = firstFirstName + "." + firstLastName + "@unas.edu.pe";
    }

    /**
     * Marks that the user must change their password (RF-06).
     */
    public void markPasswordChangeRequired() {
        this.mustChangePassword = true;
        this.updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    /**
     * Confirms that the user has changed their password.
     */
    public void confirmPasswordChange() {
        this.mustChangePassword = false;
        this.updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    private String normalizeText(String text) {
        return text
                .replace("á", "a").replace("é", "e")
                .replace("í", "i").replace("ó", "o")
                .replace("ú", "u").replace("ñ", "n");
    }
}
