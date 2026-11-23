
package view;

import model.BankingService;
import util.DatabaseSetup;
import controller.*;
import dao.AuthDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    // Shared banking information
    private BankingService service = new BankingService();

    private CustomerController customers =
            new CustomerController(service);

    private AccountController accounts =
            new AccountController(service);

    private TransactionController transactions =
            new TransactionController(service);

    private InterestController interest =
            new InterestController(service);

    private CustomerPortalController portal =
            new CustomerPortalController(service);

    private AuthDAO auth = new AuthDAO();

    private boolean loaded = false;

    @Override
    public void start(Stage stage) {

        stage.setTitle("ABZA Banking Management System");

        try {
            DatabaseSetup.createTables();
            auth.createTable();

            if (auth.hasAdmin()) {
                showLogin(stage);
            } else {
                showAdminSetup(stage);
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // FIRST-TIME ADMINISTRATOR SETUP
    private void showAdminSetup(Stage stage) {

        Label title = new Label("Create Administrator");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password (at least 8 characters)");

        Button create = new Button("Create Admin");

        Label message = new Label();

        create.setOnAction(e -> {
            try {
                auth.createAdmin(username.getText(), password.getText());
                showLogin(stage);
            } catch (Exception ex) {
                message.setText(ex.getMessage());
            }
        });

        VBox layout = new VBox(15,
                title, username, password, create, message);

        showPage(stage, layout);
    }

    // LOGIN SCREEN
    private void showLogin(Stage stage) {

        Label title = new Label("ABZA BANK LOGIN");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button login = new Button("Login");

        Label message = new Label();

        login.setOnAction(e -> {

            try {
                AuthDAO.LoginUser user =
                        auth.login(username.getText(), password.getText());

                password.clear();

                if (user == null) {
                    message.setText("Incorrect username or password.");
                    return;
                }

                // Load bank records only once
                if (!loaded) {
                    service.loadSavedData();
                    loaded = true;
                }

                if (user.getRole().equals("ADMIN")) {
                    showAdmin(stage);

                } else if (user.getRole().equals("CUSTOMER")) {

                    String customerId = user.getCustomerId();

                    if (service.getCustomer(customerId) == null) {
                        message.setText("Customer not found.");
                        return;
                    }

                    showCustomer(stage, customerId);

                } else {
                    message.setText("Unknown user role.");
                }

            } catch (Exception ex) {
                message.setText("Login error: " + ex.getMessage());
            }
        });

        password.setOnAction(e -> login.fire());

        VBox layout = new VBox(15,
                title, username, password, login, message);

        showPage(stage, layout);
    }

    // ADMINISTRATOR DASHBOARD
    private void showAdmin(Stage stage) {

        TabPane tabs = new TabPane();

        Tab customerTab = new Tab("Register Customer", customerScreen());
        Tab accountTab = new Tab("Accounts", accountScreen());
        Tab transactionTab = new Tab("Transactions", transactionScreen());
        Tab loginTab = new Tab("Customer Login", customerLoginScreen());

        customerTab.setClosable(false);
        accountTab.setClosable(false);
        transactionTab.setClosable(false);
        loginTab.setClosable(false);

        tabs.getTabs().addAll(
                customerTab, accountTab, transactionTab, loginTab);

        Button logout = new Button("Logout");
        logout.setOnAction(e -> showLogin(stage));

        VBox layout = new VBox(10,
                new Label("ABZA BANK - ADMINISTRATOR"),
                logout, tabs);

        layout.setPadding(new Insets(20));

        stage.setScene(new Scene(layout, 850, 650));
        stage.show();
    }

    // REGISTER INDIVIDUAL OR COMPANY
    private ScrollPane customerScreen() {

        TextField id = field("Customer ID");
        TextField name = field("First name or company name");
        TextField surname = field("Surname");
        TextField address = field("Address");

        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Individual", "Company");
        type.setPromptText("Customer type");

        CheckBox employed = new CheckBox("Employed");

        TextField employerName = field("Employer name");
        TextField employerAddress = field("Employer address");
        TextField registration = field("Company registration number");

        Button register = new Button("Register Customer");
        Label result = new Label();
        result.setWrapText(true);

        register.setOnAction(e -> {

            result.setText(customers.registerCustomer(
                    id.getText(),
                    type.getValue(),
                    name.getText(),
                    surname.getText(),
                    address.getText(),
                    employed.isSelected(),
                    employerName.getText(),
                    employerAddress.getText(),
                    registration.getText()
            ));
        });

        VBox layout = box(
                new Label("CUSTOMER REGISTRATION"),
                id, type, name, surname, address,
                employed, employerName, employerAddress,
                registration, register, result);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);

        return scroll;
    }

    // OPEN AND VIEW ACCOUNTS
    private VBox accountScreen() {

        TextField number = field("Account number");
        TextField customerId = field("Customer ID");

        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll(
                "Savings Account",
                "Investment Account",
                "Cheque Account");
        type.setPromptText("Account type");

        TextField amount = field("Opening deposit");

        Button open = new Button("Open Account");
        Button view = new Button("View Accounts");

        TextArea result = output();

        open.setOnAction(e -> result.setText(
                accounts.openAccount(
                        number.getText(),
                        customerId.getText(),
                        type.getValue(),
                        amount.getText()
                )
        ));

        view.setOnAction(e -> result.setText(
                accounts.viewAccounts()
        ));

        return box(new Label("ACCOUNT MANAGEMENT"),
                number, customerId, type, amount,
                open, view, result);
    }

    // ADMIN BANKING TRANSACTIONS
    private VBox transactionScreen() {

        TextField number = field("Account number");
        TextField amount = field("Amount");

        Button deposit = new Button("Deposit");
        Button withdraw = new Button("Withdraw");
        Button balance = new Button("Balance");
        Button history = new Button("History");
        Button monthlyInterest = new Button("Monthly Interest");

        TextArea result = output();

        deposit.setOnAction(e -> result.setText(
                transactions.deposit(number.getText(), amount.getText())
        ));

        withdraw.setOnAction(e -> result.setText(
                transactions.withdraw(number.getText(), amount.getText())
        ));

        balance.setOnAction(e -> result.setText(
                transactions.checkBalance(number.getText())
        ));

        history.setOnAction(e -> result.setText(
                transactions.transactionHistory(number.getText())
        ));

        monthlyInterest.setOnAction(e -> result.setText(
                interest.payInterest(number.getText())
        ));

        FlowPane buttons = new FlowPane(10, 10,
                deposit, withdraw, balance, history, monthlyInterest);

        return box(new Label("BANKING TRANSACTIONS"),
                number, amount, buttons, result);
    }

    // ADMIN CREATES CUSTOMER LOGIN
    private VBox customerLoginScreen() {

        TextField customerId = field("Existing customer ID");
        TextField username = field("Customer username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password (at least 8 characters)");

        Button create = new Button("Create Customer Login");
        Label result = new Label();
        result.setWrapText(true);

        create.setOnAction(e -> {

            try {
                String id = customerId.getText().trim();

                if (service.getCustomer(id) == null) {
                    result.setText("Register this customer first.");
                    return;
                }

                auth.createCustomerLogin(
                        username.getText(), password.getText(), id);

                password.clear();
                result.setText("Customer login created!");

            } catch (Exception ex) {
                result.setText(ex.getMessage());
            }
        });

        return box(new Label("CREATE CUSTOMER LOGIN"),
                customerId, username, password, create, result);
    }

    // CUSTOMER DASHBOARD
    private void showCustomer(Stage stage, String customerId) {

        String customerName =
                service.getCustomer(customerId).getName();

        Label welcome = new Label("Welcome, " + customerName);

        TextArea myAccounts = output();
        myAccounts.setText(portal.viewMyAccounts(customerId));

        Button refresh = new Button("View My Accounts");
        refresh.setOnAction(e ->
                myAccounts.setText(portal.viewMyAccounts(customerId)));

        TextField number = field("Your account number");
        TextField amount = field("Amount");

        Button deposit = new Button("Deposit");
        Button withdraw = new Button("Withdraw");
        Button balance = new Button("Check Balance");
        Button history = new Button("History");
        Button logout = new Button("Logout");

        TextArea result = output();

        deposit.setOnAction(e -> result.setText(
                portal.deposit(customerId, number.getText(), amount.getText())
        ));

        withdraw.setOnAction(e -> result.setText(
                portal.withdraw(customerId, number.getText(), amount.getText())
        ));

        balance.setOnAction(e -> result.setText(
                portal.checkBalance(customerId, number.getText())
        ));

        history.setOnAction(e -> result.setText(
                portal.history(customerId, number.getText())
        ));

        logout.setOnAction(e -> showLogin(stage));

        FlowPane buttons = new FlowPane(10, 10,
                deposit, withdraw, balance, history);

        VBox layout = box(welcome, logout,
                myAccounts, refresh, number, amount,
                buttons, result);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);

        stage.setScene(new Scene(scroll, 850, 650));
        stage.show();
    }

    // SIMPLE HELPER METHODS

    private TextField field(String text) {
        TextField field = new TextField();
        field.setPromptText(text);
        return field;
    }

    private TextArea output() {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setPrefHeight(170);
        return area;
    }

    private VBox box(javafx.scene.Node... items) {
        VBox layout = new VBox(12, items);
        layout.setPadding(new Insets(20));
        return layout;
    }

    private void showPage(Stage stage, VBox layout) {
        layout.setPadding(new Insets(40));stage.setScene(new Scene(layout, 500, 400));
        stage.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
