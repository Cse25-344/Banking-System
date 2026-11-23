
package banking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime date;

    public Transaction(String type,
                       BigDecimal amount,
                       BigDecimal balanceAfter) {

        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.date = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date + " | " + type
                + " | P" + amount
                + " | Balance: P" + balanceAfter;
    }
}
