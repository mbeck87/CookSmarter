module com.jixo.cook.cooksmarter {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires java.desktop;

    opens jixo.cook.presentation.controller to javafx.fxml;
    exports jixo.cook.presentation.controller;
    exports jixo.cook.domain.model;
    exports jixo.cook.application.usecase;
    exports jixo.cook.infrastructure.ui;
}