
package model;

import interfaces.InterestBearing;

import java.math.BigDecimal;
import java.time.YearMonth;

public class InvestmentAccount extends Account
        implements InterestBearing {

    private static final BigDecimal RATE =
            new BigDecimal("0.05");

    private static final BigDecimal MINIMUM =
            new BigDecimal("500.00");

    // Constructor for opening a NEW investment account
    public InvestmentAccount(
            String number,
            Customer owner,
            BigDecimal openingDeposit) {

        super(number, owner);

        checkAmount(openingDeposit);

        if (openingDeposit.compareTo(MINIMUM) < 0) {
            throw new IllegalArgumentException(
                    "Investment Accounts require at least P500."
            );
        }

        deposit(openingDeposit);
    }

    // Private constructor for loading an EXISTING account
    private InvestmentAccount(
            String number,
            Customer owner) {

        super(number, owner);
    }

    // Restore an account saved in SQLite
    public static InvestmentAccount restoreAccount(
            String number,
            Customer owner,
            BigDecimal savedBalance) {

        InvestmentAccount account =
                new InvestmentAccount(number, owner);

        account.restoreBalance(savedBalance);

        return account;
    }

    // Withdraw money
    @Override
    public void withdraw(BigDecimal amount) {
        takeMoney(amount);
    }

    // Calculate monthly interest
    @Override
    public BigDecimal payMonthlyInterest(YearMonth month) {
        return creditMonthlyInterest(RATE, month);
    }
}
