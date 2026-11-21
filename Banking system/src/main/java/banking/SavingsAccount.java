package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

public class SavingsAccount extends Account implements InterestBearing {
    private static final BigDecimal RATE = new BigDecimal("0.0005"); // 0.05%
    private YearMonth lastPaidMonth;

    public SavingsAccount(String number, Customer owner) { super(number, owner); }

    @Override public void withdraw(BigDecimal amount) {
        throw new UnsupportedOperationException("Savings accounts do not allow withdrawals");
    }

    @Override public BigDecimal calculateMonthlyInterest() {
        return getBalance().multiply(RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override public void payMonthlyInterest(YearMonth month) {
        if (month == null) throw new IllegalArgumentException("Month is required");
        if (lastPaidMonth != null && !month.isAfter(lastPaidMonth))
            throw new IllegalArgumentException("Interest has already been paid for this month or a later month");
        creditInterest(calculateMonthlyInterest());
        lastPaidMonth = month;
    }
}
