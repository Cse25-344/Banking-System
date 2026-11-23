
package controller;

import interfaces.InterestBearing;
import model.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

public class BankingController {

    // Use our Section 4 banking classes
    private final Bank bank = new Bank();

    // Store registered customers and accounts
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final Map<String, Account> accounts = new LinkedHashMap<>();

    // Register an individual or company
    public String registerCustomer(
            String id, String type, String name,
            String surname, String address,
            boolean employed, String employerName,
            String employerAddress, String registrationNumber) {

        try {
            id = id.trim();
            name = name.trim();
            address = address.trim();

            if (id.isEmpty() || name.isEmpty() || address.isEmpty()) {
                return "Please enter the customer ID, name and address.";
            }

            if (type == null) {
                return "Please select Individual or Company.";
            }

            if (customers.containsKey(id)) {
                return "This customer ID already exists.";
            }

            Customer customer;

            if (type.equals("Individual")) {

                if (surname.trim().isEmpty()) {
                    return "Please enter the surname.";
                }

                customer = new IndividualCustomer(
                        id,
                        name,
                        surname.trim(),
                        address,
                        employed,
                        employerName.trim(),
                        employerAddress.trim()
                );

            } else {

                if (registrationNumber.trim().isEmpty()) {
                    return "Please enter the company registration number.";
                }

                customer = new CompanyCustomer(
                        id,
                        name,
                        address,
                        registrationNumber.trim()
                );
            }

            bank.registerCustomer(customer);
            customers.put(id, customer);

            return "Customer registered successfully!\n"
                    + "Customer ID: " + id + "\n"
                    + "Name: " + customer.getName();

        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }
    }

    // Open savings, investment or cheque account
    public String openAccount(
            String accountNumber,
            String customerId,
            String accountType,
            String openingAmount) {

        try {
            accountNumber = accountNumber.trim();
            customerId = customerId.trim();

            if (accountNumber.isEmpty()) {
                return "Please enter an account number.";
            }

            if (accounts.containsKey(accountNumber)) {
                return "This account number already exists.";
            }

            Customer owner = customers.get(customerId);

            if (owner == null) {
                return "Customer not found. Register the customer first.";
            }

            if (accountType == null) {
                return "Please select an account type.";
            }

            BigDecimal amount = new BigDecimal(openingAmount.trim());

            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                return "Opening deposit cannot be negative.";
            }

            Account account;

            switch (accountType) {

                case "Savings Account":
                    account = new SavingsAccount(accountNumber, owner);

                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        account.deposit(amount);
                    }
                    break;

                case "Investment Account":
                    account = new InvestmentAccount(
                            accountNumber, owner, amount);
                    break;

                case "Cheque Account":
                    account = new ChequeAccount(accountNumber, owner);

                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        account.deposit(amount);
                    }
                    break;

                default:
                    return "Invalid account type.";
            }

            bank.openAccount(account);
            accounts.put(accountNumber, account);

            return "Account opened successfully!\n"
                    + "Account Number: " + accountNumber + "\n"
                    + "Owner: " + owner.getName() + "\n"
                    + "Balance: P" + account.getBalance();

        } catch (NumberFormatException e) {
            return "Please enter a valid opening deposit, such as 500.00.";
        } catch (Exception e) {
            return "Cannot open account: " + e.getMessage();
        }
    }

    // Deposit money
    public String deposit(String accountNumber, String amountText) {

        try {
            Account account = findAccount(accountNumber);
            BigDecimal amount = new BigDecimal(amountText.trim());

            account.deposit(amount);

            return "Deposit successful!\n"
                    + "Amount: P" + amount + "\n"
                    + "New balance: P" + account.getBalance();

        } catch (Exception e) {
            return "Deposit failed: " + e.getMessage();
        }
    }

    // Withdraw money
    public String withdraw(String accountNumber, String amountText) {

        try {
            Account account = findAccount(accountNumber);
            BigDecimal amount = new BigDecimal(amountText.trim());

            account.withdraw(amount);

            return "Withdrawal successful!\n"
                    + "Amount: P" + amount + "\n"
                    + "New balance: P" + account.getBalance();

        } catch (Exception e) {
            return "Withdrawal failed: " + e.getMessage();
        }
    }

    // Check the account balance
    public String checkBalance(String accountNumber) {

        try {
            Account account = findAccount(accountNumber);

            return "Account Number: " + accountNumber.trim() + "\n"
                    + "Account Type: "
                    + account.getClass().getSimpleName() + "\n"
                    + "Balance: P" + account.getBalance();

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // Display transaction history
    public String transactionHistory(String accountNumber) {

        try {
            Account account = findAccount(accountNumber);

            StringBuilder result = new StringBuilder();

            result.append("TRANSACTION HISTORY\n");
            result.append("Account: ").append(accountNumber.trim());
            result.append("\n----------------------------\n");

            if (account.getTransactions().isEmpty()) {
                return result + "No transactions recorded.";
            }

            for (Transaction transaction : account.getTransactions()) {
                result.append(transaction).append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // Pay monthly interest
    public String payInterest(String accountNumber) {

        try {
            Account account = findAccount(accountNumber);

            if (!(account instanceof InterestBearing)) {
                return "This account does not earn monthly interest.";
            }

            InterestBearing interestAccount = (InterestBearing) account;

            BigDecimal interest = interestAccount.payMonthlyInterest(
                    YearMonth.now()
            );

            return "Monthly interest processed.\n"
                    + "Interest: P" + interest + "\n"
                    + "New balance: P" + account.getBalance();

        } catch (Exception e) {
            return "Interest calculation failed: " + e.getMessage();
        }
    }

    // Display all accounts
    public String viewAccounts() {

        if (accounts.isEmpty()) {
            return "No bank accounts have been created yet.";
        }

        StringBuilder result = new StringBuilder("BANK ACCOUNTS\n\n");

        for (Map.Entry<String, Account> entry : accounts.entrySet()) {

            Account account = entry.getValue();

            result.append("Account: ").append(entry.getKey()).append("\n");
            result.append("Type: ")
                    .append(account.getClass().getSimpleName()).append("\n");
            result.append("Balance: P")
                    .append(account.getBalance()).append("\n");
            result.append("----------------------------\n");
        }

        return result.toString();
    }

    // Find an account
    private Account findAccount(String accountNumber) {

        Account account = accounts.get(accountNumber.trim());

        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        return account;
    }
}
