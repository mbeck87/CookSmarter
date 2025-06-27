package jixo.cook.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import jixo.cook.scripts.Ingredient;
import jixo.cook.scripts.Manager;
import java.util.ArrayList;
import java.util.List;

public class EditIngredientsController {

    @FXML
    private TextField fEnergy;

    @FXML
    private TextField fFat;

    @FXML
    private TextField fFiber;

    @FXML
    private TextField fName;

    @FXML
    private TextField fProtein;

    @FXML
    private TextField fSalt;

    @FXML
    private TextField fSearch;

    @FXML
    private TextField fSugar;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ImageView imageView;

    @FXML
    private ScrollPane scrollPane;

    private final Manager manager = Manager.getInstance();
    private List<Ingredient> ingredientList;
    private Ingredient selected;

    // eigenschaften grafischer elemente setzen
    @FXML
    void initialize() {
        flowPane.setHgap(5);
        flowPane.setVgap(5);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: lightblue; -fx-background-color: lightblue");

        imageView.setPreserveRatio(true);

        updateList();
        updateUI(ingredientList);
    }

    // searchbaraction bei eingabe
    @FXML
    void SearchKeyReleased(KeyEvent event) {
        if(fSearch.getText().isEmpty()) {
            updateList();
            updateUI(ingredientList);
        } else {
            List<Ingredient> results = new ArrayList<>();
            for(Ingredient ingredient : ingredientList) {
                if(ingredient.getName().toLowerCase().startsWith(fSearch.getText().toLowerCase())) {
                    results.add(ingredient);
                }
            }
            updateUI(results);
        }
    }

    // buttonaction: okay
    @FXML
    void okayAction(ActionEvent event) {
        // check ob alle werte vorhanden sind
        if(fName.getText().isEmpty() || fEnergy.getText().isEmpty() || fSugar.getText().isEmpty()
                || fFat.getText().isEmpty() || fSalt.getText().isEmpty() || fProtein.getText().isEmpty()
                || fFiber.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Ein Fehler ist aufgetreten");
            alert.setContentText("Es müssen alle Werte vorhanden sein.");
            alert.showAndWait();
            return;
        }
        // neuen ingredient mit aktualisierten werten erstellen und local speichern
        Ingredient ing = new Ingredient();
        ing.setName(fName.getText());
        ing.setImageUrl(selected.getImageUrl());
        ing.setEnergy(fEnergy.getText());
        ing.setSugar(fSugar.getText());
        ing.setSaturatedFat(fFat.getText());
        ing.setSalt(fSalt.getText());
        ing.setProteins(fProtein.getText());
        ing.setFiber(fFiber.getText());
        manager.importIngredient(ing);
    }

    // buttonaction: löschen
    @FXML
    void deleteAction(ActionEvent event) {
        // nochmaliges nachfragen
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung");
        alert.setHeaderText("Zutat wirklich löschen?");
        alert.setContentText("Wenn Sie diese Zutat wirklich löschen, werden auch alle Rezepte mit dieser gelöscht.");
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        if(result == ButtonType.OK) {
            // ingredient + rezepte löschen und gui aktualisieren
            manager.deleteIngredient(selected);
            updateList();
            updateUI(ingredientList);
            imageView.setImage(null);
            fName.setText("");
            fEnergy.setText("");
            fSugar.setText("");
            fFat.setText("");
            fSalt.setText("");
            fProtein.setText("");
            fFiber.setText("");
        }
    }

    // gui aktualisieren
    private void updateUI(List<Ingredient> list) {
        flowPane.getChildren().clear();
        for(Ingredient ing : list) {
            addListener(ing);
            flowPane.getChildren().add(ing);
        }
    }

    // ingredient liste updaten (local ingredient ordner)
    private void updateList() {
        ingredientList = manager.loadIngredients();
    }

    // mousehover und selected effekte für ingredients setzen
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

    // detailinformationen (werte) im gui setzen
    private void setDetails() {
        imageView.setImage(getImage(selected.getImageUrl()));
        fName.setText(selected.getName());
        fEnergy.setText(selected.getEnergy());
        fSugar.setText(selected.getSugar());
        fFat.setText(selected.getSaturatedFat());
        fSalt.setText(selected.getSalt());
        fProtein.setText(selected.getProteins());
        fFiber.setText(selected.getFiber());
    }

    // erstelle image
    private Image getImage(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return new Image(url);
        } else {
            return new Image("file:/" + selected.getImageUrl());
        }
    }
}
