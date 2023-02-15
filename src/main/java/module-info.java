module com.example.connectfour {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;


    opens com.example.connectfour to javafx.fxml, com.google.gson;
    exports com.example.connectfour;
}