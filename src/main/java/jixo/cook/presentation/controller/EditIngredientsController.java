package jixo.cook.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import jixo.cook.application.usecase.ImportIngredientUseCase;
import jixo.cook.application.usecase.ManageIngredientUseCase;
import jixo.cook.domain.model.Ingredient;
import jixo.cook.infrastructure.config.AppConfig;
import jixo.cook.presentation.component.AiChatPanel;
import jixo.cook.presentation.component.IngredientCard;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditIngredientsController {

    @FXML private Button btnDeleteImage;
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
    @FXML private StackPane chatContainer;

    private final ManageIngredientUseCase manageUseCase = AppConfig.getInstance().manageIngredient;
    private final ImportIngredientUseCase importUseCase = AppConfig.getInstance().importIngredient;
    private final javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
    private List<Ingredient> ingredientList;
    private IngredientCard selected;
    private AiChatPanel chatPanel;
    private String selectedImageUrl = null; // null = unchanged, "" = deleted (use noCover), path = new image
    private String noCoverDisplayUrl;

    @FXML
    void initialize() {
        flowPane.setHgap(5);
        flowPane.setVgap(5);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll");
        imageView.setPreserveRatio(true);
        noCoverDisplayUrl = getClass().getResource("/jixo/cook/images/noCover.jpg").toExternalForm();
        imageView.setImage(new Image(noCoverDisplayUrl));
        btnDeleteImage.setDisable(true);
        fEnergy.setOnKeyReleased(e -> updateKcalFromKj());
        fKcal.setOnKeyReleased(e -> updateKjFromKcal());

        chatPanel = new AiChatPanel(AppConfig.getInstance().aiIngredient, this::fillFormFromAi, this::deselect);
        chatContainer.getChildren().add(chatPanel);
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Bilddateien", "*.jpg", "*.jpeg", "*.png", "*.gif"));

        scrollPane.setOnMouseClicked(e -> {
            if (e.getTarget() == scrollPane || e.getTarget() == flowPane) {
                deselect();
            }
        });

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
        String noCoverPath = System.getProperty("user.dir") + "/storage/images/noCover.jpg";
        String imageUrl;
        if (selectedImageUrl == null) {
            imageUrl = selected != null ? selected.getIngredient().getImageUrl() : noCoverPath;
        } else if (selectedImageUrl.isEmpty()) {
            imageUrl = noCoverPath;
        } else {
            imageUrl = selectedImageUrl;
        }
        ing.setImageUrl(imageUrl);
        ing.setEnergy(fEnergy.getText());
        ing.setSugar(fSugar.getText());
        ing.setSaturatedFat(fFat.getText());
        ing.setSalt(fSalt.getText());
        ing.setProteins(fProtein.getText());
        ing.setFiber(fFiber.getText());
        ing.setCarbohydrates(fCarbohydrates.getText());
        importUseCase.importIngredient(ing);
        chatPanel.setIngredient(ing);
        updateList();
        updateUI(ingredientList);

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Gespeichert");
        info.setHeaderText(null);
        info.setContentText("Zutat \"" + ing.getName() + "\" wurde erfolgreich gespeichert.");
        info.showAndWait();
    }

    @FXML
    void pictureAction(ActionEvent event) {
        java.io.File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (file == null) return;
        try {
            String ext = file.getName().substring(file.getName().lastIndexOf('.'));
            String name = fName.getText().isBlank() ? file.getName() : fName.getText();
            java.io.File dest = new java.io.File(
                    System.getProperty("user.dir") + "/storage/images/" + name + ext);
            java.nio.file.Files.copy(file.toPath(), dest.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            selectedImageUrl = dest.getAbsolutePath();
            imageView.setImage(new Image(dest.toURI().toString()));
            btnDeleteImage.setDisable(false);
        } catch (java.io.IOException e) {
            new Alert(Alert.AlertType.ERROR, "Bild konnte nicht gespeichert werden.").showAndWait();
        }
    }

    @FXML
    void deleteImageAction(ActionEvent event) {
        // delete custom image file from disk
        String current = selectedImageUrl != null && !selectedImageUrl.isEmpty() ? selectedImageUrl
                : (selected != null ? selected.getIngredient().getImageUrl() : null);
        if (current != null && !current.contains("noCover")) {
            new java.io.File(current).delete();
        }

        String noCoverPath = System.getProperty("user.dir") + "/storage/images/noCover.jpg";
        selectedImageUrl = "";

        // update detail panel
        imageView.setImage(new Image(noCoverDisplayUrl));
        btnDeleteImage.setDisable(true);

        // update card image + ingredient model + JSON
        if (selected != null) {
            selected.updateImage(noCoverPath);
            importUseCase.importIngredient(selected.getIngredient());
        }
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
            chatPanel.setIngredient(null);
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
            selectedImageUrl = null;
            card.getStyleClass().add("food-card-selected");
            setDetails();
            chatPanel.setIngredient(card.getIngredient());
        });
    }

    private void setDetails() {
        Ingredient ing = selected.getIngredient();
        imageView.setImage(getImage(ing.getImageUrl()));
        btnDeleteImage.setDisable(ing.getImageUrl() == null || ing.getImageUrl().contains("noCover"));
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
        }
        Image image = new Image("file:/" + url);
        if (image.isError()) {
            image = new Image(noCoverDisplayUrl);
        }
        return image;
    }

    private void deselect() {
        if (selected != null) {
            selected.getStyleClass().remove("food-card-selected");
            selected = null;
        }
        chatPanel.setIngredient(null);
        selectedImageUrl = null;
        imageView.setImage(new Image(noCoverDisplayUrl));
        btnDeleteImage.setDisable(true);
        fName.setText(""); fEnergy.setText(""); fKcal.setText("");
        fCarbohydrates.setText(""); fSugar.setText(""); fFat.setText("");
        fSalt.setText(""); fProtein.setText(""); fFiber.setText("");
    }

    private void fillFormFromAi(Ingredient ing) {
        fName.setText(ing.getName() != null ? ing.getName() : "");
        fEnergy.setText(ing.getEnergy() != null ? ing.getEnergy() : "");
        fKcal.setText(toKcal(ing.getEnergy() != null ? ing.getEnergy() : ""));
        fCarbohydrates.setText(ing.getCarbohydrates() != null ? ing.getCarbohydrates() : "");
        fSugar.setText(ing.getSugar() != null ? ing.getSugar() : "");
        fFat.setText(ing.getSaturatedFat() != null ? ing.getSaturatedFat() : "");
        fSalt.setText(ing.getSalt() != null ? ing.getSalt() : "");
        fProtein.setText(ing.getProteins() != null ? ing.getProteins() : "");
        fFiber.setText(ing.getFiber() != null ? ing.getFiber() : "");
    }
}
