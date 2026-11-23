
package com.example.bankingsystemgui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    // Save a customer to the database
    public void saveCustomer(
            String id,
            String type,
            String name,
            String surname,
            String address,
            boolean employed,
            String employerName,
            String employerAddress,
            String registrationNumber) throws SQLException {

        String sql = "INSERT INTO customers " +
                "(customer_id, customer_type, name, surname, " +
                "address, employed, employer_name, " +
                "employer_address, registration_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, id);
            statement.setString(2, type);
            statement.setString(3, name);
            statement.setString(4, surname);
            statement.setString(5, address);
            statement.setInt(6, employed ? 1 : 0);
            statement.setString(7, employerName);
            statement.setString(8, employerAddress);
            statement.setString(9, registrationNumber);

            statement.executeUpdate();
        }
    }

    // Check whether a customer is saved
    public boolean customerExists(String id) throws SQLException {

        String sql = "SELECT customer_id FROM customers " +
                "WHERE customer_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public void loadCustomers(BankingService service)
            throws java.sql.SQLException {

        String sql = "SELECT * FROM customers";

        try (java.sql.Connection connection =
                     DatabaseConnection.getConnection();

             java.sql.PreparedStatement statement =
                     connection.prepareStatement(sql);

             java.sql.ResultSet result =
                     statement.executeQuery()) {

            while (result.next()) {

                String id = result.getString("customer_id");
                String type = result.getString("customer_type");
                String name = result.getString("name");
                String surname = result.getString("surname");
                String address = result.getString("address");

                boolean employed = result.getInt("employed") == 1;

                String employerName =
                        result.getString("employer_name");

                String employerAddress =
                        result.getString("employer_address");

                String registrationNumber =
                        result.getString("registration_number");

                banking.Customer customer;

                if ("Individual".equals(type)) {

                    customer = new banking.IndividualCustomer(
                            id,
                            address,
                            name,
                            surname,
                            employed,
                            employerName,
                            employerAddress
                    );

                } else if ("Company".equals(type)) {

                    customer = new banking.CompanyCustomer(
                            id,
                            address,
                            name,
                            registrationNumber
                    );

                } else {
                    throw new java.sql.SQLException(
                            "Unknown customer type: " + type);
                }

                service.addCustomer(customer, id);
            }
        }
    }

}
