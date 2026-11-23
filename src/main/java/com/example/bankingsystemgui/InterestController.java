
package com.example.bankingsystemgui;

import banking.*;

import java.math.BigDecimal;
import java.time.YearMonth;

public class InterestController {

    private final BankingService service;
    private final TransactionDAO transactionDAO =
            new TransactionDAO();

    public InterestController(BankingService service) {
        this.service = service;
    }

    public String payInterest(String accountNumber) {

        try {
            Account account = service.getAccount(accountNumber);

            if (account == null) {
                return "Account not found.";
            }

            if (!(account instanceof InterestBearing)) {
                return "This account does not earn monthly interest.";
            }

            YearMonth month = YearMonth.now();

            if (transactionDAO.interestAlreadyPaid(
                    account.getAccountNumber(), month)) {
                return "Interest was already paid for " + month;
            }

            InterestBearing interestAccount =
                    (InterestBearing) account;

            BigDecimal interest =
                    interestAccount.payMonthlyInterest(month);

            transactionDAO.saveTransaction(
                    account, "INTEREST", interest);

            return "Monthly interest saved!\n"
                    + "Interest: P" + interest + "\n"
                    + "New balance: P" + account.getBalance();

        } catch (Exception e) {
            return "Interest failed: " + e.getMessage()
                    + "\nIf this was a database error, restart "
                    + "the application before trying again.";
        }
    }
}
