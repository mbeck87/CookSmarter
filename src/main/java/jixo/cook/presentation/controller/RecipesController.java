package jixo.cook.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import jixo.cook.application.usecase.CreateRecipeUseCase;
import jixo.cook.application.usecase.ManageRecipeUseCase;
import jixo.cook.domain.model.NutritionalInfo;
import jixo.cook.domain.model.Recipe;
import jixo.cook.domain.model.RecipeIngredient;
import jixo.cook.infrastructure.config.AppConfig;
import jixo.cook.presentation.component.RecipeCard;
import jixo.cook.presentation.component.RecipeIngredientRow;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipesController {

    @FXML private Button bDelete;
    @FXML private Button bEdit;
    @FXML private Button bSave;
    @FXML private TextField fSearch;
    @FXML private FlowPane flowPane;
    @FXML private ImageView imageView;
    @FXML private Label lFat;
    @FXML private Label lFiber;
    @FXML private Label lEnergy;
    @FXML private Label lKcal;
    @FXML private Label lProtein;
    @FXML private Label lSalt;
    @FXML private Label lSugar;
    @FXML private Label lCarbohydrates;
    @FXML private VBox recipeBox;
    @FXML private ScrollPane recipeScroll;
    @FXML private ScrollPane scrollPane;
    @FXML private TextArea textArea;

    private final ManageRecipeUseCase manageUseCase = AppConfig.getInstance().manageRecipe;
    private final CreateRecipeUseCase createUseCase = AppConfig.getInstance().createRecipe;
    private final MenuController menu;
    private List<Recipe> recipeList;
    private Recipe selectedRecipe;
    private RecipeCard selectedCard;

    public RecipesController(MenuController menu) {
        this.menu = menu;
    }

    @FXML
    void initialize() {
        flowPane.setHgap(5);
        flowPane.setVgap(5);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll");
        bEdit.setVisible(false);
        bSave.setVisible(false);
        bDelete.setVisible(false);
        updateList();
        updateUI(recipeList);
    }

    @FXML
    void SearchKeyReleased(KeyEvent event) {
        if (fSearch.getText().isEmpty()) {
            updateList();
            updateUI(recipeList);
        } else {
            List<Recipe> results = new ArrayList<>();
            for (Recipe recipe : recipeList) {
                if (recipe.getRecipeName().toLowerCase().contains(fSearch.getText().toLowerCase())) {
                    results.add(recipe);
                }
            }
            updateUI(results);
        }
    }

    @FXML
    void deleteAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung");
        alert.setHeaderText("Rezept wirklich löschen?");
        alert.setContentText("\"" + selectedRecipe.getRecipeName() + "\" wird dauerhaft gelöscht.");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        imageView.setImage(null);
        resetDetails();
        bEdit.setVisible(false);
        bSave.setVisible(false);
        bDelete.setVisible(false);
        manageUseCase.delete(selectedRecipe);
        selectedRecipe = null;
        updateList();
        updateUI(recipeList);
    }

    @FXML
    void editAction(ActionEvent event) {
        menu.loadRecipeView(selectedRecipe);
    }

    @FXML
    void saveAction(ActionEvent event) {
        selectedRecipe.setDescription(textArea.getText());
        createUseCase.save(selectedRecipe);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Gespeichert");
        alert.setHeaderText(null);
        alert.setContentText("Rezept \"" + selectedRecipe.getRecipeName() + "\" wurde erfolgreich gespeichert.");
        alert.showAndWait();
    }

    private void updateUI(List<Recipe> list) {
        flowPane.getChildren().clear();
        for (Recipe recipe : list) {
            RecipeCard card = new RecipeCard(recipe);
            addListener(card);
            flowPane.getChildren().add(card);
        }
    }

    private void updateList() {
        recipeList = manageUseCase.loadAll();
    }

    private void resetDetails() {
        while (recipeBox.getChildren().size() > 3) {
            recipeBox.getChildren().remove(2);
        }
        textArea.setText("");
    }

    private void addListener(RecipeCard card) {
        card.setOnMouseClicked(event -> {
            if (selectedCard != null) selectedCard.getStyleClass().remove("food-card-selected");
            selectedCard = card;
            card.getStyleClass().add("food-card-selected");
            selectedRecipe = card.getRecipe();
            bEdit.setVisible(true);
            bSave.setVisible(true);
            bDelete.setVisible(true);
            resetDetails();
            imageView.setImage(new Image("file:/" + selectedRecipe.getImageURL()));
            for (RecipeIngredient ri : selectedRecipe.getIngredients()) {
                RecipeIngredientRow row = new RecipeIngredientRow(ri);
                row.getField().setText(ri.getMenge());
                recipeBox.getChildren().add(recipeBox.getChildren().size() - 1, row);
                calculateInTotal(selectedRecipe);

                row.getButton().setOnAction(e -> {
                    selectedRecipe.getIngredients().remove(ri);
                    recipeBox.getChildren().remove(row);
                    calculateInTotal(selectedRecipe);
                });
                row.getField().setOnKeyReleased(e -> {
                    ri.setMenge(row.getField().getText());
                    calculateInTotal(selectedRecipe);
                });
            }
            textArea.setText(selectedRecipe.getDescription());
        });
    }

    private void calculateInTotal(Recipe recipe) {
        double[] total = new double[7];
        for (RecipeIngredient ri : recipe.getIngredients()) {
            String menge = (ri.getMenge() == null || ri.getMenge().isEmpty()) ? "0" : ri.getMenge();
            total[0] += getValue(menge, ri.getEnergy());
            total[1] += getValue(menge, ri.getSugar());
            total[2] += getValue(menge, ri.getSaturatedFat());
            total[3] += getValue(menge, ri.getSalt());
            total[4] += getValue(menge, ri.getProteins());
            total[5] += getValue(menge, ri.getFiber());
            total[6] += getValue(menge, ri.getCarbohydrates());
        }
        lEnergy.setText(format(total[0]));
        lKcal.setText(format(NutritionalInfo.getKcal(total[0])));
        lSugar.setText(format(total[1]));
        lFat.setText(format(total[2]));
        lSalt.setText(format(total[3]));
        lProtein.setText(format(total[4]));
        lFiber.setText(format(total[5]));
        lCarbohydrates.setText(format(total[6]));
    }

    private double getValue(String amount, String value) {
        double doubleAmount = Double.parseDouble(amount.replace(",", "."));
        double val = check(value);
        return doubleAmount / 100.0 * val;
    }

    private String format(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private double check(String text) {
        try {
            return Double.parseDouble(text.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
