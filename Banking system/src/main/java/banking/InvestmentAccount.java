package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

public class InvestmentAccount extends Account implements InterestBearing {
    private static final BigDecimal RATE = new BigDecimal("0.05"); // 5%
    private static final BigDecimal MINIMUM_OPENING = new BigDecimal("500.00");
    private YearMonth lastPaidMonth;

    public InvestmentAccount(String number, Customer owner, BigDecimal openingDeposit) {
        super(number, owner);
        openingDeposit = validAmount(openingDeposit);
        if (openingDeposit.compareTo(MINIMUM_OPENING) < 0)
            throw new IllegalArgumentException("Investment opening deposit must be at least BWP 500");
        deposit(openingDeposit);
    }

    @Override public void withdraw(BigDecimal amount) { takeMoney(amount); }
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
