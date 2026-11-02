package com.campuspulse.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for hashing and verifying passwords using SHA-256.
 * Adheres to college-level security standards (no plaintext passwords).
 */
public class PasswordUtil {

    /**
     * Hashes a plaintext password string using SHA-256.
     * @param password Plaintext password
     * @return 64-character hexadecimal SHA-256 hash string
     */
    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies if a plaintext password matches the stored hash.
     * @param plaintext Candidate password
     * @param storedHash Expected hash from database
     * @return true if matches, false otherwise
     */
    public static boolean verify(String plaintext, String storedHash) {
        if (plaintext == null || storedHash == null) {
            return false;
        }
        String calculated = hashPassword(plaintext);
        return calculated.equalsIgnoreCase(storedHash);
    }
}
