
package banking;

import java.math.BigDecimal;

public class ChequeAccount extends Account {

    public ChequeAccount(String number, Customer owner) {
        super(number, owner);

        if (!(owner instanceof IndividualCustomer)) {
            throw new IllegalArgumentException(
                "Cheque Accounts require an employed individual."
            );
        }

        IndividualCustomer person = (IndividualCustomer) owner;

        if (!person.isEmployed()) {
            throw new IllegalArgumentException(
                "Customer must be employed."
            );
        }

        if (person.getEmployerName() == null
                || person.getEmployerName().isBlank()
                || person.getEmployerAddress() == null
                || person.getEmployerAddress().isBlank()) {

            throw new IllegalArgumentException(
                "Employer name and address are required."
            );
        }
    }

    @Override
    public void withdraw(BigDecimal amount) {
        takeMoney(amount);
    }
}
