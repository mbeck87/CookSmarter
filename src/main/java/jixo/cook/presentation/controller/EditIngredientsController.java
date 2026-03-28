package jixo.cook.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import jixo.cook.application.usecase.ImportIngredientUseCase;
import jixo.cook.application.usecase.ManageIngredientUseCase;
import jixo.cook.domain.model.Ingredient;
import jixo.cook.infrastructure.config.AppConfig;
import jixo.cook.presentation.component.IngredientCard;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditIngredientsController {

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

    private final ManageIngredientUseCase manageUseCase = AppConfig.getInstance().manageIngredient;
    private final ImportIngredientUseCase importUseCase = AppConfig.getInstance().importIngredient;
    private List<Ingredient> ingredientList;
    private IngredientCard selected;

    @FXML
    void initialize() {
        flowPane.setHgap(5);
        flowPane.setVgap(5);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll");
        imageView.setPreserveRatio(true);
        fEnergy.setOnKeyReleased(e -> updateKcalFromKj());
        fKcal.setOnKeyReleased(e -> updateKjFromKcal());
        updateList();
        updateUI(ingredientList);
    }

    @FXML
    void SearchKeyReleased(KeyEvent event) {
        if (fSearch.getText().isEmpty()) {
            updateList();
            updateUI(ingredientList);
        } else {
            List<Ingredient> results = new ArrayList<>();
            for (Ingredient ing : ingredientList) {
                if (ing.getName().toLowerCase().contains(fSearch.getText().toLowerCase())) {
                    results.add(ing);
                }
            }
            updateUI(results);
        }
    }

    @FXML
    void okayAction(ActionEvent event) {
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
        updateList();
        updateUI(ingredientList);

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Gespeichert");
        info.setHeaderText(null);
        info.setContentText("Zutat \"" + ing.getName() + "\" wurde erfolgreich gespeichert.");
        info.showAndWait();
    }

    @FXML
    void deleteAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung");
        alert.setHeaderText("Zutat wirklich löschen?");
        alert.setContentText("Wenn Sie diese Zutat wirklich löschen, werden auch alle Rezepte mit dieser gelöscht.");
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        if (result == ButtonType.OK) {
            manageUseCase.delete(selected.getIngredient());
            updateList();
            updateUI(ingredientList);
            imageView.setImage(null);
            fName.setText(""); fEnergy.setText(""); fSugar.setText("");
            fFat.setText(""); fSalt.setText(""); fProtein.setText(""); fFiber.setText(""); fCarbohydrates.setText("");
        }
    }

    private void updateUI(List<Ingredient> list) {
        flowPane.getChildren().clear();
        for (Ingredient ing : list) {
            IngredientCard card = new IngredientCard(ing);
            addListener(card);
            flowPane.getChildren().add(card);
        }
    }

    private void updateList() {
        ingredientList = manageUseCase.loadAll();
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
        fName.setText(ing.getName());
        fEnergy.setText(ing.getEnergy());
        fKcal.setText(toKcal(ing.getEnergy()));
        fSugar.setText(ing.getSugar());
        fFat.setText(ing.getSaturatedFat());
        fSalt.setText(ing.getSalt());
        fProtein.setText(ing.getProteins());
        fFiber.setText(ing.getFiber());
        fCarbohydrates.setText(ing.getCarbohydrates());
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

    private Image getImage(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return new Image(url);
        } else {
            return new Image("file:/" + url);
        }
    }
}
