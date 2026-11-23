
package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public abstract class Account {

    private String accountNumber;
    private Customer owner;
    private BigDecimal balance;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private HashSet<YearMonth> paidMonths = new HashSet<>();

    public Account(String accountNumber, Customer owner) {

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number is required.");
        }

        if (owner == null) {
            throw new IllegalArgumentException("Every account needs a customer.");
        }

        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = new BigDecimal("0.00");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    // Convert an amount to two decimal places.
    protected BigDecimal money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required.");
        }

        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    protected void checkAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Use at most two decimal places.");
        }
    }

    public void deposit(BigDecimal amount) {
        checkAmount(amount);

        amount = money(amount);
        balance = balance.add(amount);

        transactions.add(
            new Transaction("DEPOSIT", amount, balance)
        );
    }

    // Method overloading: another way to deposit.
    public void deposit(double amount) {
        deposit(BigDecimal.valueOf(amount));
    }

    // Account types decide how withdrawals work.
    public abstract void withdraw(BigDecimal amount);

    protected void takeMoney(BigDecimal amount) {
        checkAmount(amount);
        amount = money(amount);

        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        balance = balance.subtract(amount);

        transactions.add(
            new Transaction("WITHDRAWAL", amount, balance)
        );
    }

    // Shared calculation used by interest-bearing accounts.
    protected BigDecimal creditMonthlyInterest(
            BigDecimal rate, YearMonth month) {

        if (month == null || rate == null) {
            throw new IllegalArgumentException("Interest details are required.");
        }

        if (paidMonths.contains(month)) {
            throw new IllegalStateException(
                "Interest has already been paid for " + month
            );
        }

        BigDecimal interest = balance.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);

        balance = balance.add(interest);
        paidMonths.add(month);

        transactions.add(
            new Transaction("INTEREST", interest, balance)
        );

        return interest;
    }

    // Restore an existing account balance from SQLite
    // This must not create a new deposit transaction.
    public void restoreBalance(BigDecimal savedBalance) {

        if (savedBalance == null) {
            throw new IllegalArgumentException(
                    "Saved balance cannot be empty.");
        }

        if (savedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Saved balance cannot be negative.");
        }

        if (savedBalance.scale() > 2) {
            throw new IllegalArgumentException(
                    "Balance must have at most two decimal places.");
        }

        this.balance = money(savedBalance);
    }

}
