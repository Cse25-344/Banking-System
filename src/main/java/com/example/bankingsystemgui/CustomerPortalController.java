
package com.example.bankingsystemgui;

import banking.Account;

public class CustomerPortalController {

    private BankingService service;
    private TransactionController transactions;

    public CustomerPortalController(BankingService service) {
        this.service = service;
        this.transactions = new TransactionController(service);
    }

    // Check if the account belongs to this customer
    private boolean ownsAccount(String customerId, String accountNumber) {

        Account account = service.getAccount(accountNumber);

        if (account == null) {
            return false;
        }

        return account.getOwner().getCustomerId().equals(customerId);
    }

    // Display only the customer's accounts
    public String viewMyAccounts(String customerId) {

        String result = "MY BANK ACCOUNTS\n\n";
        int count = 0;

        for (Account account : service.getAccounts().values()) {

            if (account.getOwner().getCustomerId().equals(customerId)) {

                result += "Account: " + account.getAccountNumber() + "\n";
                result += "Type: " + account.getClass().getSimpleName() + "\n";
                result += "Balance: P" + account.getBalance() + "\n\n";

                count++;
            }
        }

        if (count == 0) {
            return "You have no bank accounts yet.";
        }

        return result;
    }

    public String deposit(String customerId, String number, String amount) {

        if (!ownsAccount(customerId, number)) {
            return "Access denied. This is not your account.";
        }

        return transactions.deposit(number, amount);
    }

    public String withdraw(String customerId, String number, String amount) {

        if (!ownsAccount(customerId, number)) {
            return "Access denied. This is not your account.";
        }

        return transactions.withdraw(number, amount);
    }

    public String checkBalance(String customerId, String number) {

        if (!ownsAccount(customerId, number)) {
            return "Access denied. This is not your account.";
        }

        return transactions.checkBalance(number);
    }

    public String history(String customerId, String number) {

        if (!ownsAccount(customerId, number)) {
            return "Access denied. This is not your account.";
        }

        return transactions.transactionHistory(number);
    }
}
