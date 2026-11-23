
package com.example.bankingsystemgui;

import banking.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.YearMonth;

public class TransactionDAO {

    // Save a transaction and its updated balance together
    public void saveTransaction(
            Account account,
            String type,
            BigDecimal amount) throws SQLException {

        String updateSql =
                "UPDATE accounts SET balance = ? WHERE account_number = ?";

        String insertSql =
                "INSERT INTO transactions " +
                        "(account_number, transaction_type, amount, transaction_date) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement update =
                         connection.prepareStatement(updateSql);
                 PreparedStatement insert =
                         connection.prepareStatement(insertSql)) {

                // Update the saved balance
                update.setString(1, account.getBalance().toString());
                update.setString(2, account.getAccountNumber());

                if (update.executeUpdate() != 1) {
                    throw new SQLException("Account not found in database.");
                }

                // Record the transaction
                insert.setString(1, account.getAccountNumber());
                insert.setString(2, type);
                insert.setString(3, amount.toString());
                insert.setString(4, LocalDateTime.now().toString());
                insert.executeUpdate();

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // Save the opening deposit of a new account
    public void saveOpeningDeposit(
            String accountNumber,
            BigDecimal amount) throws SQLException {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        String sql =
                "INSERT INTO transactions " +
                        "(account_number, transaction_type, amount, transaction_date) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);
            statement.setString(2, "DEPOSIT");
            statement.setString(3, amount.toString());
            statement.setString(4, LocalDateTime.now().toString());

            statement.executeUpdate();
        }
    }

    // Read saved transaction history
    public String getHistory(String accountNumber)
            throws SQLException {

        String sql =
                "SELECT transaction_type, amount, transaction_date " +
                        "FROM transactions WHERE account_number = ? " +
                        "ORDER BY transaction_id";

        StringBuilder history =
                new StringBuilder("TRANSACTION HISTORY\n");

        history.append("Account: ").append(accountNumber)
                .append("\n\n");

        int count = 0;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    count++;

                    history.append(result.getString("transaction_type"))
                            .append(" - P")
                            .append(result.getString("amount"))
                            .append("\n")
                            .append(result.getString("transaction_date"))
                            .append("\n\n");
                }
            }
        }

        if (count == 0) {
            history.append("No saved transactions yet.");
        }

        return history.toString();
    }

    // Prevent interest being paid twice in one month
    public boolean interestAlreadyPaid(
            String accountNumber,
            YearMonth month) throws SQLException {

        String sql =
                "SELECT transaction_id FROM transactions " +
                        "WHERE account_number = ? " +
                        "AND transaction_type = 'INTEREST' " +
                        "AND substr(transaction_date, 1, 7) = ? " +
                        "LIMIT 1";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);
            statement.setString(2, month.toString());

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
}
