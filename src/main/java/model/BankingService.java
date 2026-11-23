
package model;

import dao.AccountDAO;
import dao.CustomerDAO;

import java.util.LinkedHashMap;
import java.util.Map;

public class BankingService {

    private final Bank bank = new Bank();

    private final Map<String, Customer> customers =
            new LinkedHashMap<>();

    private final Map<String, Account> accounts =
            new LinkedHashMap<>();

    public void addCustomer(Customer customer, String id) {
        bank.registerCustomer(customer);
        customers.put(id, customer);
    }

    public Customer getCustomer(String id) {
        return customers.get(id.trim());
    }

    public boolean customerExists(String id) {
        return customers.containsKey(id.trim());
    }

    public void addAccount(Account account, String number) {
        bank.openAccount(account);
        accounts.put(number, account);
    }

    public Account getAccount(String number) {
        return accounts.get(number.trim());
    }

    public boolean accountExists(String number) {
        return accounts.containsKey(number.trim());
    }

    public Map<String, Account> getAccounts() {
        return java.util.Collections.unmodifiableMap(accounts);
    }
    // Load customers and accounts from SQLite
    public void loadSavedData() throws java.sql.SQLException {

        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();

        // Load customers first
        customerDAO.loadCustomers(this);

        // Then load their accounts
        accountDAO.loadAccounts(this);

        System.out.println("Saved banking records loaded!");
    }
}
