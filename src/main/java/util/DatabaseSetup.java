
package util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {

    public static void createTables() {

        // Create customers table
        String customersTable =
                "CREATE TABLE IF NOT EXISTS customers (" +
                        "customer_id TEXT PRIMARY KEY, " +
                        "customer_type TEXT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "surname TEXT, " +
                        "address TEXT NOT NULL, " +
                        "employed INTEGER, " +
                        "employer_name TEXT, " +
                        "employer_address TEXT, " +
                        "registration_number TEXT)";

        // Create accounts table
        String accountsTable =
                "CREATE TABLE IF NOT EXISTS accounts (" +
                        "account_number TEXT PRIMARY KEY, " +
                        "customer_id TEXT NOT NULL, " +
                        "account_type TEXT NOT NULL, " +
                        "balance TEXT NOT NULL, " +
                        "FOREIGN KEY (customer_id) " +
                        "REFERENCES customers(customer_id))";

        // Create transactions table
        String transactionsTable =
                "CREATE TABLE IF NOT EXISTS transactions (" +
                        "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "account_number TEXT NOT NULL, " +
                        "transaction_type TEXT NOT NULL, " +
                        "amount TEXT NOT NULL, " +
                        "transaction_date TEXT NOT NULL, " +
                        "FOREIGN KEY (account_number) " +
                        "REFERENCES accounts(account_number))";

        // Connect and create the tables
        try (Connection connection =
                     DatabaseConnection.getConnection();

             Statement statement =
                     connection.createStatement()) {

            statement.execute("PRAGMA foreign_keys = ON");

            statement.execute(customersTable);
            statement.execute(accountsTable);
            statement.execute(transactionsTable);

            System.out.println("Database tables created successfully!");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
