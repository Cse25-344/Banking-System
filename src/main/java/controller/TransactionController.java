
package controller;

import model.BankingService;
import dao.TransactionDAO;
import model.Account;

import java.math.BigDecimal;

public class TransactionController {

    private final BankingService service;
    private final TransactionDAO transactionDAO =
            new TransactionDAO();

    public TransactionController(BankingService service) {
        this.service = service;
    }

    private Account findAccount(String number) {

        Account account = service.getAccount(number);

        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        return account;
    }

    // Deposit money and save it to SQLite
    public String deposit(String number, String amountText) {

        try {
            Account account = findAccount(number);
            BigDecimal amount = new BigDecimal(amountText.trim());

            account.deposit(amount);

            transactionDAO.saveTransaction(
                    account, "DEPOSIT", amount);

            return "Deposit saved successfully!\n"
                    + "New balance: P" + account.getBalance();

        } catch (Exception e) {
            return "Deposit failed";
        }
    }

    // Withdraw money and save it to SQLite
    public String withdraw(String number, String amountText) {

        try {
            Account account = findAccount(number);
            BigDecimal amount = new BigDecimal(amountText.trim());

            account.withdraw(amount);

            transactionDAO.saveTransaction(
                    account, "WITHDRAWAL", amount);

            return "Withdrawal saved successfully!\n"
                    + "New balance: P" + account.getBalance();

        } catch (Exception e) {
            return "Withdrawal not allowed " ;
        }
    }

    // Display current account balance
    public String checkBalance(String number) {

        try {
            Account account = findAccount(number);

            return "ACCOUNT BALANCE\n"
                    + "Account: " + account.getAccountNumber() + "\n"
                    + "Balance: P" + account.getBalance();

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // Display transaction history stored in SQLite
    public String transactionHistory(String number) {

        try {
            Account account = findAccount(number);

            return transactionDAO.getHistory(
                    account.getAccountNumber());

        } catch (Exception e) {
            return "History error: " + e.getMessage();
        }
    }
}
