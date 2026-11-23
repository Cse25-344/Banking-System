
package com.example.bankingsystemgui;

import java.sql.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AuthDAO {

    // Create the login table
    public void createTable() throws SQLException {

        String sql = "CREATE TABLE IF NOT EXISTS app_users (" +
                "username TEXT PRIMARY KEY, " +
                "customer_id TEXT, " +
                "role TEXT NOT NULL, " +
                "salt TEXT NOT NULL, " +
                "password_hash TEXT NOT NULL)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
        }
    }

    // Check if an administrator already exists
    public boolean hasAdmin() throws SQLException {

        String sql = "SELECT COUNT(*) FROM app_users " +
                "WHERE role = 'ADMIN'";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            return result.next() && result.getInt(1) > 0;
        }
    }

    // Register an administrator
    public void createAdmin(String username, String password)
            throws Exception {

        if (hasAdmin()) {
            throw new IllegalArgumentException(
                    "Administrator already exists.");
        }

        saveUser(username, password, "ADMIN", null);
    }

    // Register a customer login
    public void createCustomerLogin(
            String username, String password, String customerId)
            throws Exception {

        // Customer must already be registered in the bank
        String checkSql =
                "SELECT customer_id FROM customers WHERE customer_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(checkSql)) {

            statement.setString(1, customerId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new IllegalArgumentException(
                            "Customer ID not found.");
                }
            }
        }

        saveUser(username, password, "CUSTOMER", customerId);
    }

    // Save login credentials safely
    private void saveUser(
            String username,
            String password,
            String role,
            String customerId) throws Exception {

        username = username.trim().toLowerCase();

        if (!username.matches("[a-z0-9_]{3,30}")) {
            throw new IllegalArgumentException(
                    "Username must be 3-30 letters, numbers or underscores.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters.");
        }

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        byte[] hash = hashPassword(password, salt);

        String sql = "INSERT INTO app_users " +
                "(username, customer_id, role, salt, password_hash) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, customerId);
            statement.setString(3, role);

            statement.setString(4,
                    Base64.getEncoder().encodeToString(salt));

            statement.setString(5,
                    Base64.getEncoder().encodeToString(hash));

            statement.executeUpdate();
        }
    }

    // Verify login and return the user's details
    public LoginUser login(String username, String password)
            throws Exception {

        String sql = "SELECT customer_id, role, salt, password_hash " +
                "FROM app_users WHERE username = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1,
                    username.trim().toLowerCase());

            try (ResultSet result = statement.executeQuery()) {

                if (!result.next()) {
                    return null;
                }

                byte[] salt = Base64.getDecoder().decode(
                        result.getString("salt"));

                byte[] savedHash = Base64.getDecoder().decode(
                        result.getString("password_hash"));

                byte[] enteredHash =
                        hashPassword(password, salt);

                if (!MessageDigest.isEqual(savedHash, enteredHash)) {
                    return null;
                }

                return new LoginUser(
                        result.getString("role"),
                        result.getString("customer_id"));
            }
        }
    }

    // Hash passwords instead of saving plain text
    private byte[] hashPassword(String password, byte[] salt)
            throws Exception {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, 210000, 256);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } finally {
            spec.clearPassword();
        }
    }

    // Holds the authenticated user's role and customer ID
    public static class LoginUser {

        private final String role;
        private final String customerId;

        public LoginUser(String role, String customerId) {
            this.role = role;
            this.customerId = customerId;
        }

        public String getRole() {
            return role;
        }

        public String getCustomerId() {
            return customerId;
        }
    }
}
