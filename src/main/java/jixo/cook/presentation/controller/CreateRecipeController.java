package jixo.cook.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jixo.cook.application.usecase.CreateRecipeUseCase;
import jixo.cook.application.usecase.ManageIngredientUseCase;
import jixo.cook.application.usecase.ManageRecipeUseCase;
import jixo.cook.domain.model.Ingredient;
import jixo.cook.domain.model.NutritionalInfo;
import jixo.cook.domain.model.Recipe;

import jixo.cook.domain.model.RecipeIngredient;
import jixo.cook.infrastructure.config.AppConfig;
import jixo.cook.presentation.component.IngredientCard;
import jixo.cook.presentation.component.RecipeIngredientRow;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateRecipeController {

    @FXML private TextField fSearch;
    @FXML private FlowPane flowPane;
    @FXML private ImageView imageView;
    @FXML private ScrollPane scrollPane;
    @FXML private ScrollPane recipeScroll;
    @FXML private VBox recipeBox;
    @FXML private Label lFat;
    @FXML private Label lFiber;
    @FXML private Label lEnergy;
    @FXML private Label lKcal;
    @FXML private Label lProtein;
    @FXML private Label lSalt;
    @FXML private Label lSugar;
    @FXML private TextArea textArea;

    private final ManageIngredientUseCase manageIngredient = AppConfig.getInstance().manageIngredient;
    private final CreateRecipeUseCase createRecipe = AppConfig.getInstance().createRecipe;
    private final ManageRecipeUseCase manageRecipe = AppConfig.getInstance().manageRecipe;
    private IngredientCard ghostBox;
    private Stage stage;
    private List<RecipeIngredient> recipeIngredients;
    private List<Ingredient> ingredientList;
    private FileChooser fileChooser;
    private String imageUrl;
    private Recipe selectedRecipe;

    public CreateRecipeController(Recipe recipe) {
        if (recipe != null) {
            selectedRecipe = recipe;
        }
    }

    @FXML
    void initialize() {
        imageUrl = System.getProperty("user.dir") + "/storage/images/noCover.jpg";

        textArea.setMinHeight(250);
        textArea.setWrapText(true);

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif"));

        recipeIngredients = new ArrayList<>();

        recipeScroll.setFitToWidth(true);
        recipeScroll.setFitToHeight(true);

        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initOwner(null);
        stage.setOpacity(0.5);

        flowPane.setHgap(5);
        flowPane.setVgap(5);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: lightblue; -fx-background-color: lightblue");

        if (selectedRecipe != null) {
            imageUrl = selectedRecipe.getImageURL();
            imageView.setImage(new Image("file:/" + imageUrl));
            textArea.setText(selectedRecipe.getDescription());
            recipeIngredients.addAll(selectedRecipe.getIngredients());
            for (RecipeIngredient ri : recipeIngredients) {
                RecipeIngredientRow row = new RecipeIngredientRow(ri);
                row.getField().setText(ri.getMenge());
                recipeBox.getChildren().add(recipeBox.getChildren().size() - 1, row);
                row.getButton().setOnAction(e -> {
                    recipeIngredients.remove(ri);
                    recipeBox.getChildren().remove(row);
                    calculateInTotal();
                });
                row.getField().setOnKeyReleased(e -> {
                    ri.setMenge(row.getField().getText());
                    calculateInTotal();
                });
            }
            calculateInTotal();
        } else {
            imageView.setImage(new Image("file:/" + imageUrl));
        }

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
    void pictureAction(ActionEvent event) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageUrl = file.getAbsolutePath();
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    void saveAction(ActionEvent event) {
        TextInputDialog dialog;
        if (selectedRecipe != null) {
            dialog = new TextInputDialog(selectedRecipe.getRecipeName());
        } else {
            dialog = new TextInputDialog();
        }
        dialog.setTitle("Rezept");
        dialog.setHeaderText("Bitte geben Sie dem Rezept einen Namen.");
        dialog.showAndWait().ifPresent(name -> {
            boolean isEdit = selectedRecipe != null && selectedRecipe.getRecipeName().equals(name);
            if (!isEdit && manageRecipe.exists(name)) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Überschreiben?");
                confirm.setHeaderText("Rezept \"" + name + "\" existiert bereits.");
                confirm.setContentText("Möchten Sie es überschreiben?");
                if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
            }
            Recipe recipe = new Recipe();
            recipe.setRecipeList(recipeIngredients);
            recipe.setRecipeName(name);
            recipe.setImageURL(imageUrl);
            recipe.setDescription(textArea.getText());
            createRecipe.save(recipe);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Erfolg");
            alert.setHeaderText("Rezept gespeichert");
            alert.setContentText("Das Rezept wurde erfolgreich gespeichert.");
            alert.showAndWait();
        });
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
        ingredientList = manageIngredient.loadAll();
    }

    private void addListener(IngredientCard card) {
        RecipeIngredient ri = new RecipeIngredient(card.getIngredient());
        RecipeIngredientRow row = new RecipeIngredientRow(ri);

        card.setOnMousePressed(event -> {
            card.setCursor(Cursor.CLOSED_HAND);
            ghostBox = cloneCard(card);
            ghostBox.setMouseTransparent(true);
            ghostBox.setStyle("-fx-background-color: rgba(100, 149, 237); -fx-background-radius: 10px;");
            Scene scene = new Scene(ghostBox);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setX(event.getScreenX() - 70);
            stage.setY(event.getScreenY() - 80);
            stage.show();
        });

        card.setOnMouseReleased(event -> {
            card.setCursor(Cursor.HAND);
            stage.close();
            ghostBox = null;
            if (isMouseOverScrollPane(event.getScreenX(), event.getScreenY())) {
                if (!recipeIngredients.contains(ri)) {
                    recipeIngredients.add(ri);
                    recipeBox.getChildren().add(recipeBox.getChildren().size() - 1, row);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Fehler");
                    alert.setHeaderText("Ein Fehler ist aufgetreten");
                    alert.setContentText("Zutat befindet sich schon im Rezept");
                    alert.showAndWait();
                }
            }
        });

        card.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - 70);
            stage.setY(event.getScreenY() - 80);
        });

        row.getButton().setOnAction(event -> {
            recipeIngredients.remove(ri);
            recipeBox.getChildren().remove(row);
            calculateInTotal();
        });

        row.getField().setOnKeyReleased(event -> {
            ri.setMenge(row.getField().getText());
            calculateInTotal();
        });
    }

    private void calculateInTotal() {
        double[] total = new double[6];
        for (RecipeIngredient ri : recipeIngredients) {
            String menge = (ri.getMenge() == null || ri.getMenge().isEmpty()) ? "0" : ri.getMenge();
            total[0] += getValue(menge, ri.getEnergy());
            total[1] += getValue(menge, ri.getSugar());
            total[2] += getValue(menge, ri.getSaturatedFat());
            total[3] += getValue(menge, ri.getSalt());
            total[4] += getValue(menge, ri.getProteins());
            total[5] += getValue(menge, ri.getFiber());
        }
        lEnergy.setText(format(total[0]));
        lKcal.setText(format(NutritionalInfo.getKcal(total[0])));
        lSugar.setText(format(total[1]));
        lFat.setText(format(total[2]));
        lSalt.setText(format(total[3]));
        lProtein.setText(format(total[4]));
        lFiber.setText(format(total[5]));
    }

    private double getValue(String amount, String value) {
        double doubleAmount = Double.parseDouble(amount);
        double val = check(value);
        return doubleAmount / 100.0 * val;
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }

    private double check(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private boolean isMouseOverScrollPane(double mouseX, double mouseY) {
        double scrollX = recipeScroll.localToScreen(0, 0).getX();
        double scrollY = recipeScroll.localToScreen(0, 0).getY();
        return mouseX >= scrollX && mouseX <= scrollX + recipeScroll.getWidth()
            && mouseY >= scrollY && mouseY <= scrollY + recipeScroll.getHeight();
    }

    private IngredientCard cloneCard(IngredientCard original) {
        IngredientCard copy = new IngredientCard(original.getIngredient());
        copy.setPrefSize(140, 160);
        copy.setAlignment(Pos.CENTER);
        return copy;
    }
}
