package com.sgi.fiis.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String firstName;
    private String lastName;
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

    // ========== Business Logic ==========

    /**
     * Activates the user.
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now(java.time.ZoneId.systemDefault());
    }

    /**
     * Deactivates the user without deleting their record.
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now(java.time.ZoneId.systemDefault());
    }

    /**
     * Automatically generates the institutional email.
     * Format: first_name.first_lastname@unas.edu.pe
     */
    public void generateInstitutionalEmail() {
        if (this.institutionalEmail != null && !this.institutionalEmail.isBlank()) {
            return; // Email already assigned manually
        }

        String firstFirstName = this.firstName.trim().split("\\s+")[0].toLowerCase();
        String firstLastName = this.lastName.trim().split("\\s+")[0].toLowerCase();

        // Normalize special characters (ñ, accents)
        firstFirstName = normalizeText(firstFirstName);
        firstLastName = normalizeText(firstLastName);

        this.institutionalEmail = firstFirstName + "." + firstLastName + "@unas.edu.pe";
    }

    /**
     * Marks that the user must change their password.
     */
    public void markChangePasswordRequired() {
        this.mustChangePassword = true;
        this.updatedAt = LocalDateTime.now(java.time.ZoneId.systemDefault());
    }

    /**
     * Confirms that the user has changed their password.
     */
    public void confirmPasswordChange() {
        this.mustChangePassword = false;
        this.updatedAt = LocalDateTime.now(java.time.ZoneId.systemDefault());
    }

    private String normalizeText(String text) {
        return text
                .replace("á", "a").replace("é", "e")
                .replace("í", "i").replace("ó", "o")
                .replace("ú", "u").replace("ñ", "n");
    }
}
