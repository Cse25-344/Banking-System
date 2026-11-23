
package banking;

import java.math.BigDecimal;
import java.time.YearMonth;

public class Main {

    public static void main(String[] args) {

        System.out.println("ABZA BANKING SYSTEM");

        Bank bank = new Bank();

        IndividualCustomer customer1 = new IndividualCustomer(
            "C001",
            "Gaborone",
            "Karel",
            "Gaseemelwe",
            true,
            "Debswana Company",
            "Gaborone"
        );

        CompanyCustomer company = new CompanyCustomer(
            "C002",
            "Francistown",
            "Botswana Accountancy College",
            "BW2026-0001"
        );

        
        bank.registerCustomer(customer1);
        bank.registerCustomer(company);

    
        SavingsAccount savings = new SavingsAccount(
            "S1001", customer1
        );

        InvestmentAccount investment = new InvestmentAccount(
            "I1001", customer1, new BigDecimal("500.00")
        );

        ChequeAccount cheque = new ChequeAccount(
            "CH1001", customer1
        );

        SavingsAccount companySavings = new SavingsAccount(
            "S2001", company
        );

        bank.openAccount(savings);
        bank.openAccount(investment);
        bank.openAccount(cheque);
        bank.openAccount(companySavings);

        // Deposit money
        savings.deposit(new BigDecimal("1000.00"));
        investment.deposit(new BigDecimal("100.00"));
        cheque.deposit(new BigDecimal("1000.00"));
        companySavings.deposit(new BigDecimal("2000.00"));

        // Withdraw money
        investment.withdraw(new BigDecimal("50.00"));
        cheque.withdraw(new BigDecimal("200.00"));

        // Show blocked Savings withdrawals.
        try {
            savings.withdraw(new BigDecimal("100.00"));
        } catch (UnsupportedOperationException e) {
            System.out.println("\nWithdrawal blocked: " + e.getMessage());
        }

        //Show small amount rejected amounts for investment
        try {
            new InvestmentAccount(
                "I9999", customer1, new BigDecimal("200.00")
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Account rejected: " + e.getMessage());
        }

        // Pay monthly interest
        YearMonth month = YearMonth.of(2026, 11);

        System.out.println("\nMONTHLY INTEREST");

        // Polymorphism
        InterestBearing[] interestAccounts = {
            savings, investment, companySavings
        };

        for (InterestBearing account : interestAccounts) {
            BigDecimal interest = account.payMonthlyInterest(month);
            System.out.println("Interest paid: P" + interest);
        }

        // Display customers and accounts.
        bank.showCustomers();
        bank.showAccounts();

        // Display one customer's multiple accounts.
        System.out.println("\nKAREL'S ACCOUNTS");

        for (Account account : customer1.getAccounts()) {
            System.out.println(
                account.getAccountNumber()
                + " - P" + account.getBalance()
            );
        }

        // Display transaction history
        System.out.println("\nSAVINGS TRANSACTION HISTORY");

        for (Transaction transaction : savings.getTransactions()) {
            System.out.println(transaction);
        }

        System.out.println("\nBanking System completed successfully!");
    }
}
