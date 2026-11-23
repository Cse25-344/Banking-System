
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Customer {

    private String customerId;
    private String address;
    private ArrayList<Account> accounts = new ArrayList<>();

    public Customer(String customerId, String address) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID is required.");
        }

        this.customerId = customerId;
        this.address = address;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAddress() {
        return address;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }


    void addAccount(Account account) {
        accounts.add(account);
    }

    
    public abstract String getName();
}
