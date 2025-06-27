module com.jixo.cook.cooksmarter {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires java.desktop;


    opens jixo.cook.controller to javafx.fxml;
    exports jixo.cook.controller;
    exports jixo.cook.scripts;
}