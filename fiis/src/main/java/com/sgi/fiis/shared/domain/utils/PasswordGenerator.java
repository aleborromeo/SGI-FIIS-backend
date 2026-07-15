package com.sgi.fiis.shared.domain.utils;

import java.security.SecureRandom;

/**
 * Utility class to generate secure temporary passwords.
 */
public final class PasswordGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordGenerator() {
        // Prevent instantiation
    }

    public static String generateSecurePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        String all = upper + lower + digits + symbols;
        
        StringBuilder sb = new StringBuilder();
        
        // Ensure at least one of each required type
        sb.append(upper.charAt(SECURE_RANDOM.nextInt(upper.length())));
        sb.append(lower.charAt(SECURE_RANDOM.nextInt(lower.length())));
        sb.append(digits.charAt(SECURE_RANDOM.nextInt(digits.length())));
        sb.append(symbols.charAt(SECURE_RANDOM.nextInt(symbols.length())));
        
        // Fill rest up to 10 characters
        for (int i = 0; i < 6; i++) {
            sb.append(all.charAt(SECURE_RANDOM.nextInt(all.length())));
        }
        
        // Shuffle the characters
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = SECURE_RANDOM.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
