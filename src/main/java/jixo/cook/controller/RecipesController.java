package jixo.cook.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import jixo.cook.scripts.Manager;
import jixo.cook.scripts.Recipe;
import jixo.cook.scripts.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipesController {

    @FXML
    private Button bDelete;

    @FXML
    private Button bEdit;

    @FXML
    private Button bSave;

    @FXML
    private TextField fSearch;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ImageView imageView;

    @FXML
    private Label lFat;

    @FXML
    private Label lFiber;

    @FXML
    private Label lEnergy;

    @FXML
    private Label lProtein;

    @FXML
    private Label lSalt;

    @FXML
    private Label lSugar;

    @FXML
    private VBox recipeBox;

    @FXML
    private ScrollPane recipeScroll;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextArea textArea;

    private final Manager manager = Manager.getInstance();
    private final MenuController menu;
    private List<Recipe> recipeList;
    private Recipe selectedRecipe;

    // constructor
    public RecipesController(MenuController menu) {
        this.menu = menu;
    }

    // eigenschaften grafischer elemente setzen
    @FXML
    void initialize() {
        flowPane.setHgap(5);
        flowPane.setVgap(5);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: lightblue; -fx-background-color: lightblue");

        bEdit.setVisible(false);
        bSave.setVisible(false);
        bDelete.setVisible(false);

        updateList();
        updateUI(recipeList);
    }

    // searchbaraction bei eingabe
    @FXML
    void SearchKeyReleased(KeyEvent event) {
        if(fSearch.getText().isEmpty()) {
            updateList();
            updateUI(recipeList);
        } else {
            List<Recipe> results = new ArrayList<>();
            for(Recipe recipe : recipeList) {
                if(recipe.getRecipeName().toLowerCase().startsWith(fSearch.getText().toLowerCase())) {
                    results.add(recipe);
                }
            }
            updateUI(results);
        }
    }

    // buttonaction: delete (löscht recipe)
    @FXML
    void deleteAction(ActionEvent event) {
        imageView.setImage(null);
        resetDetails();
        bEdit.setVisible(false);
        bSave.setVisible(false);
        bDelete.setVisible(false);
        manager.deleteRecipe(selectedRecipe);
        selectedRecipe = null;
        updateList();
        updateUI(recipeList);
    }

    // buttonaction: edit (wechsel zum createRecipes mit ausgewähltem recipe)
    @FXML
    void editAction(ActionEvent event) {
        menu.loadRecipeView(selectedRecipe);
    }

    // buttonaction: save (speichere änderungen am recipe)
    @FXML
    void saveAction(ActionEvent event) {
        manager.saveRecipe(selectedRecipe);
    }

    // gui aktualisieren
    private void updateUI(List<Recipe> list) {
        flowPane.getChildren().clear();
        for(Recipe recipe : list) {
            addListener(recipe);
            flowPane.getChildren().add(recipe);
        }
    }

    // ingredient liste updaten (local ingredient ordner)
    private void updateList() {
        recipeList = manager.loadRecipes();
    }

    private void resetDetails() {
        while(recipeBox.getChildren().size() > 3) {
            recipeBox.getChildren().remove(2);
        }
        textArea.setText("");
    }

    // listener und dessen funktion zu dem recipe hinzufügen
    // (berechnung der details und anzeigen der ingredients usw)
    private void addListener(Recipe recipe) {
        recipe.setOnMouseClicked(even -> {
            selectedRecipe = recipe;

            bEdit.setVisible(true);
            bSave.setVisible(true);
            bDelete.setVisible(true);

            resetDetails();
            imageView.setImage(new Image("file:/" + recipe.getImageURL()));
            for(RecipeIngredient ring : recipe.getIngredients()) {
                ring.getField().setText(ring.getMenge());
                recipeBox.getChildren().add(recipeBox.getChildren().size() - 1, ring);
                calculateInTotal(recipe);

                ring.getButton().setOnAction(event -> {
                    recipe.getIngredients().remove(ring);
                    recipeBox.getChildren().remove(ring);
                    calculateInTotal(recipe);
                });

                ring.getField().setOnKeyReleased(event -> {
                    ring.setMenge(ring.getField().getText());
                    calculateInTotal(recipe);
                });
            }
            textArea.setText(recipe.getDescription());
        });
    }

    // die werte aller ingredients calculieren
    private void calculateInTotal(Recipe recipe) {
        int[] totalArray = new int[6];
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            String text = "0";
            if (!ingredient.getField().getText().isEmpty()) {
                text = ingredient.getField().getText();
            }
            totalArray[0] = totalArray[0] + getValue(text, ingredient.getEnergy());
            totalArray[1] = totalArray[1] + getValue(text, ingredient.getSugar());
            totalArray[2] = totalArray[2] + getValue(text, ingredient.getSaturatedFat());
            totalArray[3] = totalArray[3] + getValue(text, ingredient.getSalt());
            totalArray[4] = totalArray[4] + getValue(text, ingredient.getProteins());
            totalArray[5] = totalArray[5] + getValue(text, ingredient.getFiber());

        }
        lEnergy.setText(totalArray[0] + " (" + manager.getKcal(totalArray[0]) + ")");
        lSugar.setText(String.valueOf(totalArray[1]));
        lFat.setText(String.valueOf(totalArray[2]));
        lSalt.setText(String.valueOf(totalArray[3]));
        lProtein.setText(String.valueOf(totalArray[4]));
        lFiber.setText(String.valueOf(totalArray[5]));
    }

    // errechne den gesamtwert wert aus der menge und den ingredients(100g werte)
    private int getValue(String amount, String value) {
        double doubleAmount = Double.parseDouble(amount);
        double val = check(value);
        double temp = doubleAmount / 100.0;
        temp *= val;
        return (int) temp;
    }

    // checkt ob es eine gültige zahl ist
    private Double check(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
