
package banking;

import java.util.ArrayList;

public class Bank {

    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Account> accounts = new ArrayList<>();

    public void registerCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required.");
        }

        for (Customer existing : customers) {
            if (existing.getCustomerId().equals(customer.getCustomerId())) {
                throw new IllegalArgumentException("Customer ID already exists.");
            }
        }

        customers.add(customer);
        System.out.println("Registered: " + customer.getName());
    }

    public void openAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account is required.");
        }

        if (!customers.contains(account.getOwner())) {
            throw new IllegalArgumentException(
                "Register the customer before opening an account."
            );
        }

        for (Account existing : accounts) {
            if (existing.getAccountNumber().equals(account.getAccountNumber())) {
                throw new IllegalArgumentException(
                    "Account number already exists."
                );
            }
        }

        accounts.add(account);
        account.getOwner().addAccount(account);

        System.out.println("Opened account: " + account.getAccountNumber());
    }

    public void showCustomers() {
        System.out.println("\nREGISTERED CUSTOMERS");

        for (Customer customer : customers) {
            System.out.println(
                customer.getCustomerId() + " - " + customer.getName()
            );
        }
    }

    public void showAccounts() {
        System.out.println("\nBANK ACCOUNTS");

        for (Account account : accounts) {
            System.out.println(
                account.getAccountNumber()
                + " | " + account.getOwner().getName()
                + " | P" + account.getBalance()
            );
        }
    }
}
