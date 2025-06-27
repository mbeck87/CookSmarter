package jixo.cook.controller;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import jixo.cook.scripts.Ingredient;
import jixo.cook.scripts.Manager;
import jixo.cook.scripts.Translator;

import java.util.List;

public class ImportIngredientController {

    @FXML
    private Button bImport;

    @FXML
    private Label lCreateIngredient;

    @FXML
    private Label lEnergy;

    @FXML
    private Label lFat;

    @FXML
    private Label lFiber;

    @FXML
    private Label lName;

    @FXML
    private Label lProtein;

    @FXML
    private Label lSalt;

    @FXML
    private Label lSugar;

    @FXML
    private Label lSearch;

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
    private final Translator translator = Translator.getInstance();
    private Ingredient selected;

    // flowpane eigenschaften initialisieren
    @FXML
    void initialize() {
        setLanguage();

        flowPane.setHgap(5);
        flowPane.setVgap(5);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: lightblue; -fx-background-color: lightblue");

        imageView.setPreserveRatio(true);
    }

    // suchfunktion
    @FXML
    void SearchKeyReleased(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            fSearch.setDisable(true);
            new Thread(() -> search(fSearch.getText())).start();
        }
    }

    // buttonaction: import
    @FXML
    void importAction(ActionEvent event) {
        // check ob alle werte angegeben sind
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
        // ingredient erstellen
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
        // confirm
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Action erfolgreich.");
        alert.setContentText("Ihr Produkt wurde erfolgreich hinzugefügt");
        alert.showAndWait();
    }

    // nach suchbegriff in der datenbank suchen
    private void search(String text) {
        List<JsonNode> products = manager.search(text);
        System.out.println("json abgerufen " + products.size());
        if(products.size() == 0) {
            System.out.println("check 0");
            Platform.runLater(() -> fSearch.setDisable(false));
        }
        else {
            System.out.println("check 1");
            Platform.runLater(() -> updateUIWithResults(products));
        }

    }

    // das gui aktualisieren
    private void updateUIWithResults(List<JsonNode> items) {
        flowPane.getChildren().clear();
        for (JsonNode item : items) {
            System.out.println("wie lange braucht das hier");
            Ingredient ingredient = Ingredient.createFromJson(item);
            if (ingredient != null) {
                addListener(ingredient);
                flowPane.getChildren().add(ingredient);
            }
        }
        fSearch.setDisable(false);
    }

    // mousehover und selected effekte für ingredients setzen
    private void addListener(Ingredient ingredient) {
        System.out.println("add listener");
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
        fName.setText(getValue(selected.getName()));
        fEnergy.setText(getValue(selected.getEnergy()));
        fSugar.setText(getValue(selected.getSugar()));
        fFat.setText(getValue(selected.getSaturatedFat()));
        fSalt.setText(getValue(selected.getSalt()));
        fProtein.setText(getValue(selected.getProteins()));
        fFiber.setText(getValue(selected.getFiber()));
    }

    // check ob null, wenn ja dann gebe 0 zurück
    private String getValue(String value) {
        if(value == null || value.equals("null")) {
            return "0";
        }
        return value;
    }

    // erstelle image
    private Image getImage(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return new Image(url);
        } else {
            return new Image("file:/" + selected.getImageUrl());
        }
    }

    // sprache einstellen
    public void setLanguage() {
        bImport.setText(translator.get("bImport"));
        lCreateIngredient.setText(translator.get("lCreateIngredient"));
        lEnergy.setText(translator.get("lEnergy"));
        lFat.setText(translator.get("lFat"));
        lFiber.setText(translator.get("lFiber"));
        lName.setText(translator.get("lName"));
        lProtein.setText(translator.get("lProtein"));
        lSalt.setText(translator.get("lSalt"));
        lSugar.setText(translator.get("lSugar"));
        lSearch.setText(translator.get("lSearch"));
    }
}
