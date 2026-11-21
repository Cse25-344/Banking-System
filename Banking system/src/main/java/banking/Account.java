package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {
    private final String accountNumber;
    private final Customer owner;
    private BigDecimal balance = new BigDecimal("0.00");
    private final List<Transaction> transactions = new ArrayList<>();

    protected Account(String accountNumber, Customer owner) {
        if (accountNumber == null || accountNumber.isBlank()) throw new IllegalArgumentException("Account number is required");
        if (owner == null) throw new IllegalArgumentException("An account must have a customer");
        this.accountNumber = accountNumber;
        this.owner = owner;
    }

    public String getAccountNumber() { return accountNumber; }
    public Customer getOwner() { return owner; }
    public BigDecimal getBalance() { return balance; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    protected static BigDecimal validAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2)
            throw new IllegalArgumentException("Enter a positive amount with at most two decimal places");
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    public void deposit(BigDecimal amount) {
        amount = validAmount(amount);
        balance = balance.add(amount);
        transactions.add(new Transaction("DEPOSIT", amount));
    }

    // Method overloading: accepting text as well as BigDecimal.
    public void deposit(String amount) { deposit(new BigDecimal(amount)); }

    protected void takeMoney(BigDecimal amount) {
        amount = validAmount(amount);
        if (amount.compareTo(balance) > 0) throw new IllegalArgumentException("Insufficient funds");
        balance = balance.subtract(amount);
        transactions.add(new Transaction("WITHDRAWAL", amount));
    }

    protected void creditInterest(BigDecimal amount) {
        if (amount.signum() < 0) throw new IllegalArgumentException("Interest cannot be negative");
        balance = balance.add(amount);
        transactions.add(new Transaction("INTEREST", amount));
    }

    public abstract void withdraw(BigDecimal amount);
}
