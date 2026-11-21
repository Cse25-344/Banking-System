package banking;

import java.math.BigDecimal;
import java.time.YearMonth;

public class Main {
    public static void main(String[] args) {
        BankingSystem bank = new BankingSystem();
        IndividualCustomer person = new IndividualCustomer("C001", "Gaborone", "Neo", "Molefe",
                true, "Example Employer", "Gaborone");
        CompanyCustomer company = new CompanyCustomer("C002", "Francistown", "Demo Traders", "REG-100");
        bank.registerCustomer(person);
        bank.registerCustomer(company);

        SavingsAccount savings = new SavingsAccount("S001", person);
        InvestmentAccount investment = new InvestmentAccount("I001", person, new BigDecimal("500.00"));
        ChequeAccount cheque = new ChequeAccount("Q001", person);
        SavingsAccount companySavings = new SavingsAccount("S002", company);
        bank.openAccount(savings);
        bank.openAccount(investment);
        bank.openAccount(cheque);
        bank.openAccount(companySavings);

        savings.deposit("1000.00");
        investment.deposit("200.00");
        investment.withdraw(new BigDecimal("100.00"));
        cheque.deposit("850.00");
        cheque.withdraw(new BigDecimal("50.00"));
        savings.payMonthlyInterest(YearMonth.of(2026, 11));
        investment.payMonthlyInterest(YearMonth.of(2026, 11));

        System.out.println("Person: " + person.getDisplayName() + ", accounts: " + person.getAccounts().size());
        System.out.println("Company: " + company.getDisplayName() + ", accounts: " + company.getAccounts().size());
        System.out.println("Savings balance: BWP " + savings.getBalance()); // 1000.50
        System.out.println("Investment balance: BWP " + investment.getBalance()); // 630.00
        System.out.println("Cheque balance: BWP " + cheque.getBalance()); // 800.00
        System.out.println("Savings transaction history:");
        for (Transaction transaction : savings.getTransactions()) System.out.println(transaction);

        try {
            savings.withdraw(new BigDecimal("10.00"));
        } catch (UnsupportedOperationException ex) {
            System.out.println("Expected savings restriction: " + ex.getMessage());
        }
        try {
            new InvestmentAccount("I002", person, new BigDecimal("100.00"));
        } catch (IllegalArgumentException ex) {
            System.out.println("Expected investment restriction: " + ex.getMessage());
        }
    }
}
