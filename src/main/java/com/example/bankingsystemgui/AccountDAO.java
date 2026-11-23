
package com.example.bankingsystemgui;

import banking.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    // Save a new bank account to SQLite
    public void saveAccount(
            String accountNumber,
            String customerId,
            String accountType,
            BigDecimal balance) throws SQLException {

        String sql = "INSERT INTO accounts " +
                "(account_number, customer_id, account_type, balance) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);
            statement.setString(2, customerId);
            statement.setString(3, accountType);
            statement.setString(4, balance.toString());

            statement.executeUpdate();

            System.out.println("Account saved successfully!");
        }
    }

    // Check whether an account number already exists
    public boolean accountExists(String accountNumber)
            throws SQLException {

        String sql = "SELECT account_number FROM accounts " +
                "WHERE account_number = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    // Load saved accounts into the Banking System
    public void loadAccounts(BankingService service)
            throws SQLException {

        String sql = "SELECT * FROM accounts";

        try (Connection connection =
                     DatabaseConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                String accountNumber =
                        result.getString("account_number");

                String customerId =
                        result.getString("customer_id");

                String accountType =
                        result.getString("account_type");

                BigDecimal savedBalance = new BigDecimal(
                        result.getString("balance")
                );

                // Find the customer who owns the account
                Customer owner = service.getCustomer(customerId);

                if (owner == null) {
                    throw new SQLException(
                            "Customer not loaded for account: "
                                    + accountNumber);
                }

                Account account;

                // Restore Savings Account
                if (accountType.equals("Savings Account")) {

                    account = new SavingsAccount(
                            accountNumber, owner);

                    account.restoreBalance(savedBalance);

                    // Restore Investment Account
                } else if (accountType.equals("Investment Account")) {

                    account = InvestmentAccount.restoreAccount(
                            accountNumber,
                            owner,
                            savedBalance
                    );

                    // Restore Cheque Account
                } else if (accountType.equals("Cheque Account")) {

                    account = new ChequeAccount(
                            accountNumber, owner);

                    account.restoreBalance(savedBalance);

                } else {
                    throw new SQLException(
                            "Unknown account type: " + accountType);
                }

                // Add the loaded account to the Banking System
                service.addAccount(account, accountNumber);

                System.out.println(
                        "Loaded account: " + accountNumber
                                + " | Balance: P" + savedBalance
                );
            }
        }
    }

    // Update a saved account balance
    public void updateBalance(
            String accountNumber,
            BigDecimal balance) throws SQLException {

        String sql = "UPDATE accounts " +
                "SET balance = ? WHERE account_number = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, balance.toString());
            statement.setString(2, accountNumber);

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new SQLException(
                        "Account not found: " + accountNumber);
            }
        }
    }
}
