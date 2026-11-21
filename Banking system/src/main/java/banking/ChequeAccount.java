package banking;

import java.math.BigDecimal;

public class ChequeAccount extends Account {
    public ChequeAccount(String number, IndividualCustomer owner) {
        super(number, owner);
        if (!owner.isEmployed() || !owner.hasEmployerDetails())
            throw new IllegalArgumentException("Cheque accounts require employment and employer details");
    }

    @Override public void withdraw(BigDecimal amount) { takeMoney(amount); }
}
