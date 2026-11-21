package banking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Customer {
    private final String customerId;
    private final String address;
    private final List<Account> accounts = new ArrayList<>();

    public Customer(String customerId, String address) {
        if (customerId == null || customerId.isBlank()) throw new IllegalArgumentException("Customer ID is required");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Address is required");
        this.customerId = customerId;
        this.address = address;
    }

    public String getCustomerId() { return customerId; }
    public String getAddress() { return address; }
    public abstract String getDisplayName();
    public List<Account> getAccounts() { return Collections.unmodifiableList(accounts); }

    void addAccount(Account account) { accounts.add(account); }
}
