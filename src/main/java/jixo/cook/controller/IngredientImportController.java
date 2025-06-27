package jixo.cook.controller;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import jixo.cook.scripts.Ingredient;
import jixo.cook.scripts.Manager;

public class IngredientImportController {

    @FXML
    private TextField fBallast;

    @FXML
    private TextField fFett;

    @FXML
    private TextField fKJ;

    @FXML
    private TextField fName;

    @FXML
    private TextField fProteine;

    @FXML
    private TextField fSalz;

    @FXML
    private TextField fSearch;

    @FXML
    private TextField fZucker;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ImageView imageView;

    @FXML
    private ScrollPane scrollPane;


    private final Manager manager = Manager.getInstance();
    private Ingredient selected;

    @FXML
    void initialize() {
        flowPane.setHgap(5);
        flowPane.setVgap(5);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: lightblue; -fx-background-color: lightblue");

        imageView.setPreserveRatio(true);
    }

    @FXML
    void SearchKeyReleased(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            fSearch.setDisable(true);
            new Thread(() -> search(fSearch.getText())).start();
        }
    }

    @FXML
    void importAction(ActionEvent event) {
        if(fName.getText().isEmpty() && fKJ.getText().isEmpty() && fZucker.getText().isEmpty()
                && fFett.getText().isEmpty() && fSalz.getText().isEmpty() && fProteine.getText().isEmpty()
                && fBallast.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Ein Fehler ist aufgetreten");
            alert.setContentText("Es müssen alle Werte vorhanden sein.");
            alert.showAndWait();
            return;
        }

        Ingredient ing = new Ingredient();
        ing.setName(fName.getText());
        ing.setImageUrl(selected.getImageUrl());
        ing.setEnergy(fKJ.getText());
        ing.setSugar(fZucker.getText());
        ing.setSaturatedFat(fFett.getText());
        ing.setSalt(fSalz.getText());
        ing.setProteins(fProteine.getText());
        ing.setFiber(fBallast.getText());
        manager.importIngredient(ing);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Action erfolgreich.");
        alert.setContentText("Ihr Produkt wurde erfolgreich hinzugefügt");
        alert.showAndWait();
    }

    private void search(String text) {
        JsonNode products = manager.search(text);
        if(products == null) {
            Platform.runLater(() -> fSearch.setDisable(false));
            return;
        }
        else {
            Platform.runLater(() -> updateUIWithResults(products));
        }
    }

    private void updateUIWithResults(JsonNode items) {
        flowPane.getChildren().clear();
        for (JsonNode item : items) {
            Ingredient ingredient = manager.createIngredient(item);
            if (ingredient != null) {
                addListener(ingredient);
                flowPane.getChildren().add(ingredient);
            }
        }
        fSearch.setDisable(false);
    }

    private void addListener(Ingredient ingredient) {
        ingredient.setOnMouseEntered(event -> {
            if(!ingredient.equals(selected)) ingredient.setStyle("-fx-background-color: cornflowerblue; -fx-background-radius: 10px;");
        });
        ingredient.setOnMouseExited(event -> {
            if(!ingredient.equals(selected)) ingredient.setStyle("-fx-background-color: rgba(100, 149, 237, 0.4); -fx-background-radius: 10px;");
        });
        ingredient.setOnMouseClicked(event -> {
            if(selected != null) selected.setStyle("-fx-background-color: rgba(100, 149, 237, 0.4); -fx-background-radius: 10px;");
            selected = ingredient;
            ingredient.setStyle("-fx-background-color: darkcyan; -fx-background-radius: 10px;");
            setDetails();
        });
    }

    private void setDetails() {
        imageView.setImage(new Image(selected.getImageUrl()));
        fName.setText(selected.getName());
        fKJ.setText(selected.getEnergy());
        fZucker.setText(selected.getSugar());
        fFett.setText(selected.getSaturatedFat());
        fSalz.setText(selected.getSalt());
        fProteine.setText(selected.getProteins());
        fBallast.setText(selected.getFiber());
    }
}
