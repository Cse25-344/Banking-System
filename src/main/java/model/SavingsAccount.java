
package model;

import interfaces.InterestBearing;

import java.math.BigDecimal;
import java.time.YearMonth;

public class SavingsAccount extends Account
        implements InterestBearing {

    private static final BigDecimal RATE = new BigDecimal("0.0005");

    public SavingsAccount(String number, Customer owner) {
        super(number, owner);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        throw new UnsupportedOperationException(
            "Savings Accounts do not allow withdrawals."
        );
    }

    @Override
    public BigDecimal payMonthlyInterest(YearMonth month) {
        return creditMonthlyInterest(RATE, month);
    }
}
