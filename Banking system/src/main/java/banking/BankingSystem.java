package banking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BankingSystem {
    private final List<Customer> customers = new ArrayList<>();
    private final Set<String> accountNumbers = new HashSet<>();

    public void registerCustomer(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer is required");
        for (Customer existing : customers) {
            if (existing.getCustomerId().equals(customer.getCustomerId()))
                throw new IllegalArgumentException("Customer ID already exists");
        }
        customers.add(customer);
    }

    public void openAccount(Account account) {
        if (account == null) throw new IllegalArgumentException("Account is required");
        if (!customers.contains(account.getOwner()))
            throw new IllegalArgumentException("Register the account owner first");
        if (!accountNumbers.add(account.getAccountNumber()))
            throw new IllegalArgumentException("Account number already exists");
        account.getOwner().addAccount(account);
    }

    public List<Customer> getCustomers() { return Collections.unmodifiableList(customers); }
}
