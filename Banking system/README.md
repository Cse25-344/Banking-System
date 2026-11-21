# Banking System — Section 4 (Core Java Model)

Based on the submitted Part A and the CSE202 assignment, with an additional CompanyCustomer subtype to support the assignment scenario. This is an educational prototype, not real banking software.

## GitHub Codespaces instructions
1. Create/open a repository named `BankingSystem` in GitHub Codespaces.
2. Copy the `src` folder from this starter project into the repository root.
3. Open a Codespaces terminal and check `java -version` (JDK 21 recommended).
4. Run `mkdir -p out` then `javac -d out src/main/java/banking/*.java` then `java -cp out banking.Main`.
5. Read the printed results and compare with the expected balances.
6. In Source Control, stage, commit with message `Section 4 core banking model`, and sync/push to GitHub.

## Expected checks
- Savings: BWP 1000.50 after deposit of 1000 and 0.05% monthly interest.
- Investment: BWP 630.00 after opening 500, deposit 200, withdraw 100, and 5% monthly interest.
- Cheque: BWP 800.00 after deposit 850 and withdraw 50.
- Savings withdrawal rejected; investment opening under 500 rejected.
- Company customer owns a Savings account, person owns three accounts.

## Additional tests to try
- Withdrawal larger than balance; negative or zero deposit; 3+ decimal amount; repeated monthly interest; duplicate account number; unregistered customer; unemployed individual opening a Cheque account.

## Next sections
Section 5: Add Maven and JavaFX views (FXML + optional Scene Builder); no banking logic in Views.
Section 6: Controllers connect Views to model.
Section 7: SQLite JDBC, tables for customers/accounts/transactions and DAOs.
Section 8: Integration tests and screenshots/evidence.

Note: `lastPaidMonth` is stored in memory for Section 4. Persist it in SQLite in Section 7 so duplicate interest payments remain prevented after restart.
