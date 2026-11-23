
package com.example.bankingsystemgui;

import banking.*;

import java.math.BigDecimal;
import java.util.Map;

public class AccountController {

    private final BankingService service;

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO =
            new TransactionDAO();

    public AccountController(BankingService service) {
        this.service = service;
    }

    // Open a new account
    public String openAccount(
            String accountNumber,
            String customerId,
            String accountType,
            String openingDeposit) {

        try {
            accountNumber = accountNumber.trim();
            customerId = customerId.trim();

            if (accountNumber.isEmpty()) {
                return "Please enter an account number.";
            }

            if (service.accountExists(accountNumber)
                    || accountDAO.accountExists(accountNumber)) {
                return "This account number already exists.";
            }

            Customer owner = service.getCustomer(customerId);

            if (owner == null) {
                return "Customer not found. Register the customer first.";
            }

            if (accountType == null) {
                return "Please select an account type.";
            }

            BigDecimal amount =
                    new BigDecimal(openingDeposit.trim());

            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                return "Opening deposit cannot be negative.";
            }

            if (amount.scale() > 2) {
                return "Use at most two decimal places.";
            }

            Account account;

            // Savings account
            if (accountType.equals("Savings Account")) {

                account = new SavingsAccount(accountNumber, owner);

                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    account.deposit(amount);
                }

                // Investment account
            } else if (accountType.equals("Investment Account")) {

                account = new InvestmentAccount(
                        accountNumber, owner, amount);

                // Cheque account
            } else if (accountType.equals("Cheque Account")) {

                account = new ChequeAccount(accountNumber, owner);

                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    account.deposit(amount);
                }

            } else {
                return "Invalid account type.";
            }

            // Save account to SQLite
            accountDAO.saveAccount(
                    accountNumber,
                    customerId,
                    accountType,
                    account.getBalance()
            );

            // Add to the running Banking System
            service.addAccount(account, accountNumber);

            // Save the opening deposit in transaction history
            try {
                transactionDAO.saveOpeningDeposit(
                        accountNumber, amount);
            } catch (Exception historyError) {
                return "Account opened and saved, but its opening "
                        + "transaction was not recorded: "
                        + historyError.getMessage();
            }

            return "Account opened and saved successfully!\n"
                    + "Account: " + accountNumber + "\n"
                    + "Owner: " + owner.getName() + "\n"
                    + "Type: " + accountType + "\n"
                    + "Balance: P" + account.getBalance();

        } catch (NumberFormatException e) {
            return "Please enter a valid opening deposit.";

        } catch (Exception e) {
            return "Account opening failed: " + e.getMessage();
        }
    }

    // Display all accounts, including restored ones
    public String viewAccounts() {

        Map<String, Account> accounts = service.getAccounts();

        if (accounts.isEmpty()) {
            return "No bank accounts have been created.";
        }

        StringBuilder result =
                new StringBuilder("BANK ACCOUNTS\n\n");

        for (Map.Entry<String, Account> entry :
                accounts.entrySet()) {

            Account account = entry.getValue();

            result.append("Account: ")
                    .append(entry.getKey()).append("\n");

            result.append("Owner: ")
                    .append(account.getOwner().getName()).append("\n");

            result.append("Type: ")
                    .append(account.getClass().getSimpleName())
                    .append("\n");

            result.append("Balance: P")
                    .append(account.getBalance())
                    .append("\n");

            result.append("-------------------------\n");
        }

        return result.toString();
    }
}
