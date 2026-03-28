package jixo.cook.presentation.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import jixo.cook.application.usecase.ImportIngredientUseCase;
import jixo.cook.application.usecase.SearchIngredientUseCase;
import jixo.cook.domain.model.Ingredient;
import jixo.cook.infrastructure.config.AppConfig;
import jixo.cook.infrastructure.i18n.Translator;
import jixo.cook.presentation.component.IngredientCard;
import java.util.List;
import java.util.Locale;

public class ImportIngredientController {

    @FXML private Button bImport;
    @FXML private Label lCreateIngredient;

    @FXML private TextField fEnergy;
    @FXML private TextField fKcal;
    @FXML private TextField fFat;
    @FXML private TextField fFiber;
    @FXML private TextField fCarbohydrates;
    @FXML private TextField fName;
    @FXML private TextField fProtein;
    @FXML private TextField fSalt;
    @FXML private TextField fSearch;
    @FXML private TextField fSugar;
    @FXML private FlowPane flowPane;
    @FXML private ImageView imageView;
    @FXML private ScrollPane scrollPane;

    private final SearchIngredientUseCase searchUseCase = AppConfig.getInstance().searchIngredient;
    private final ImportIngredientUseCase importUseCase = AppConfig.getInstance().importIngredient;
    private final Translator translator = Translator.getInstance();
    private IngredientCard selected;

    @FXML
    void initialize() {
        setLanguage();
        flowPane.setHgap(5);
        flowPane.setVgap(5);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll");
        imageView.setPreserveRatio(true);
        fEnergy.setOnKeyReleased(e -> updateKcalFromKj());
        fKcal.setOnKeyReleased(e -> updateKjFromKcal());
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
        if (fName.getText().isEmpty() || fEnergy.getText().isEmpty() || fSugar.getText().isEmpty()
                || fFat.getText().isEmpty() || fSalt.getText().isEmpty() || fProtein.getText().isEmpty()
                || fFiber.getText().isEmpty() || fCarbohydrates.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Ein Fehler ist aufgetreten");
            alert.setContentText("Es müssen alle Werte vorhanden sein.");
            alert.showAndWait();
            return;
        }
        Ingredient ing = new Ingredient();
        ing.setName(fName.getText());
        ing.setImageUrl(selected.getIngredient().getImageUrl());
        ing.setEnergy(fEnergy.getText());
        ing.setSugar(fSugar.getText());
        ing.setSaturatedFat(fFat.getText());
        ing.setSalt(fSalt.getText());
        ing.setProteins(fProtein.getText());
        ing.setFiber(fFiber.getText());
        ing.setCarbohydrates(fCarbohydrates.getText());
        importUseCase.importIngredient(ing);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Action erfolgreich.");
        alert.setContentText("Ihr Produkt wurde erfolgreich hinzugefügt");
        alert.showAndWait();
    }

    private void search(String text) {
        List<Ingredient> products = searchUseCase.search(text);
        System.out.println("abgerufen: " + products.size());
        if (products.isEmpty()) {
            Platform.runLater(() -> fSearch.setDisable(false));
        } else {
            Platform.runLater(() -> updateUIWithResults(products));
        }
    }

    private void updateUIWithResults(List<Ingredient> items) {
        flowPane.getChildren().clear();
        for (Ingredient item : items) {
            IngredientCard card = new IngredientCard(item);
            addListener(card);
            flowPane.getChildren().add(card);
        }
        fSearch.setDisable(false);
    }

    private void addListener(IngredientCard card) {
        card.setOnMouseClicked(event -> {
            if (selected != null) selected.getStyleClass().remove("food-card-selected");
            selected = card;
            card.getStyleClass().add("food-card-selected");
            setDetails();
        });
    }

    private void setDetails() {
        Ingredient ing = selected.getIngredient();
        imageView.setImage(getImage(ing.getImageUrl()));
        fName.setText(getValue(ing.getName()));
        fEnergy.setText(getValue(ing.getEnergy()));
        fKcal.setText(toKcal(getValue(ing.getEnergy())));
        fSugar.setText(getValue(ing.getSugar()));
        fFat.setText(getValue(ing.getSaturatedFat()));
        fSalt.setText(getValue(ing.getSalt()));
        fProtein.setText(getValue(ing.getProteins()));
        fFiber.setText(getValue(ing.getFiber()));
        fCarbohydrates.setText(getValue(ing.getCarbohydrates()));
    }

    private void updateKcalFromKj() {
        fKcal.setText(toKcal(fEnergy.getText()));
    }

    private void updateKjFromKcal() {
        try {
            double kcal = Double.parseDouble(fKcal.getText().replace(",", "."));
            fEnergy.setText(String.format(Locale.US, "%.2f", kcal * 4.184));
        } catch (NumberFormatException ignored) {}
    }

    private String toKcal(String kj) {
        try {
            double val = Double.parseDouble(kj.replace(",", "."));
            return String.format(Locale.US, "%.2f", val / 4.184);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private String getValue(String value) {
        if (value == null || value.equals("null")) return "0";
        return value;
    }

    private Image getImage(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return new Image(url);
        } else {
            return new Image("file:/" + url);
        }
    }

    public void setLanguage() {
        bImport.setText(translator.get("bImport"));
        lCreateIngredient.setText(translator.get("lCreateIngredient"));
    }
}
