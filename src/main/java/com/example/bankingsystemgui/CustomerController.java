
package com.example.bankingsystemgui;

import banking.*;
import java.sql.SQLException;

public class CustomerController {

    private final BankingService service;
    private final CustomerDAO customerDAO = new CustomerDAO();

    public CustomerController(BankingService service) {
        this.service = service;
    }

    // Register an individual or company customer
    public String registerCustomer(
            String id,
            String type,
            String name,
            String surname,
            String address,
            boolean employed,
            String employerName,
            String employerAddress,
            String registrationNumber) {

        try{
            id = id.trim();
            name = name.trim();
            address = address.trim();


            if (id.isEmpty() || name.isEmpty() || address.isEmpty()) {
                return "Please enter the ID, name and address.";
            }

            if (type == null) {
                return "Please select a customer type.";
            }


            if (service.customerExists(id)
                    || customerDAO.customerExists(id)) {
                return "This customer ID already exists.";
            }

            Customer customer;

            if (type.equals("Individual")) {

                if (surname.trim().isEmpty()) {
                    return "Please enter the surname.";
                }

                if (employed && (employerName.trim().isEmpty()
                        || employerAddress.trim().isEmpty())) {
                    return "Please enter the employer details.";
                }

                customer = new IndividualCustomer(
                        id,
                        address,
                        name,
                        surname.trim(),
                        employed,
                        employerName.trim(),
                        employerAddress.trim()
                );

            } else if (type.equals("Company")) {

                if (registrationNumber.trim().isEmpty()) {
                    return "Please enter the company registration number.";
                }

                customer = new CompanyCustomer(
                        id,
                        address,
                        name,
                        registrationNumber.trim()
                );

            } else {
                return "Invalid customer type.";
            }

            // Save customer to SQLite
            customerDAO.saveCustomer(
                    id,
                    type,
                    name,
                    surname.trim(),
                    address,
                    employed,
                    employerName.trim(),
                    employerAddress.trim(),
                    registrationNumber.trim()
            );

            // Add customer to the running Banking System
            service.addCustomer(customer, id);

            return "Customer registered and saved successfully!\n"
                    + "Customer ID: " + id + "\n"
                    + "Customer Name: " + customer.getName();

        } catch (SQLException e) {
            return "Database error: " + e.getMessage();

        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }
    }
}
