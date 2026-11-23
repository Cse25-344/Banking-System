
module com.example.bankingsystemgui {

    requires javafx.controls;
    requires javafx.fxml;

    // Allows Java to use JDBC and SQL
    requires java.sql;

    opens com.example.bankingsystemgui to javafx.fxml;

    exports com.example.bankingsystemgui;
}
