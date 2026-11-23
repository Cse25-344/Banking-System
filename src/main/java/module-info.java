
module com.example.bankingsystemgui {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Core banking classes
    exports model;
    exports interfaces;

    // JavaFX application
    exports view;
    opens view to javafx.graphics, javafx.fxml;

    // Controllers
    exports controller;
    opens controller to javafx.fxml;

    // Database access
    exports dao;
    exports util;
}
