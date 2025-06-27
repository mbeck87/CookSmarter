package jixo.cook.controller;

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
import jixo.cook.scripts.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateRecipeController {

    @FXML
    private TextField fSearch;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ImageView imageView;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ScrollPane recipeScroll;

    @FXML
    private VBox recipeBox;

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
    private TextArea textArea;

    private final Manager manager = Manager.getInstance();
    private Ingredient ghostBox;
    private Stage stage;
    private List<RecipeIngredient> recipeIngredients;
    private List<Ingredient> ingredientList;
    private FileChooser fileChooser;
    private String imageUrl;
    private Recipe selectedRecipe;

    //construktor lädt rezept wenn es von recipes zum editieren kommt
    public CreateRecipeController(Recipe recipe) {
        if(recipe != null) {
            selectedRecipe = recipe;
        }
    }

    // eigenschaften grafischer elemente setzen
    @FXML
    void initialize() {
        imageUrl = System.getProperty("user.dir") + "/images/noCover.jpg";

        textArea.setMinHeight(250);
        textArea.setWrapText(true);

        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

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

        imageView.setImage(new Image("file:/" + System.getProperty("user.dir") + "/images/noCover.jpg"));

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

    // Buttonaction: bild
    @FXML
    void pictureAction(ActionEvent event) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageUrl = file.getAbsolutePath();
            imageView.setImage(new Image(imageUrl));
        }
    }

    // Buttonaction: save (speichert recipes in den recipe ordner und kopiert bild in images)
    @FXML
    void saveAction(ActionEvent event) {
        TextInputDialog dialog;
        if(selectedRecipe != null) {
            dialog = new TextInputDialog(selectedRecipe.getRecipeName());
        } else {
            dialog = new TextInputDialog();
        }
        dialog.setTitle("Rezept");
        dialog.setHeaderText("Bitte geben Sie dem Rezept einen Namen.");
        dialog.showAndWait().ifPresent(name -> {
            Recipe recipe = new Recipe();
            recipe.setRecipeList(recipeIngredients);
            recipe.setRecipeName(name);
            recipe.setImageURL(imageUrl);
            recipe.setDescription(textArea.getText());
            manager.saveRecipe(recipe);
        });
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

    // listener und funktionen hinzufügen (und ing in recipeing umwandeln)
    private void addListener(Ingredient ing) {
        RecipeIngredient ring = new RecipeIngredient(ing);
        // wenn die maus gedrückt wird, erstelle ghostbox für drag and drop
        ing.setOnMousePressed(event -> {
            ing.setCursor(Cursor.CLOSED_HAND);
            Ingredient temp = (Ingredient) event.getSource();
            ghostBox = cloneIngredient(temp);
            ghostBox.setMouseTransparent(true);
            ghostBox.setStyle("-fx-background-color: rgba(100, 149, 237); -fx-background-radius: 10px;");

            // füge ghostbox einer stage zu
            Scene scene = new Scene(ghostBox);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setX(event.getScreenX() - 70);
            stage.setY(event.getScreenY() - 80);
            stage.show();
        });

        // wenn die maus released wird, reset ghostbox und füge ing dem rezept hinzu
        ing.setOnMouseReleased(event -> {
            ing.setCursor(Cursor.HAND);
            stage.close();
            ghostBox = null;

            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();
            if (isMouseOverScrollPane(mouseX, mouseY)) {
                if (!recipeIngredients.contains(ring)) {
                    recipeIngredients.add(ring);
                    recipeBox.getChildren().add(recipeBox.getChildren().size() - 1, ring);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Fehler");
                    alert.setHeaderText("Ein Fehler ist aufgetreten");
                    alert.setContentText("Zutat befindet sich schon im Rezept");
                    alert.showAndWait();
                }
            }
        });

        // bei mousedrag adjustiere position der ghosbox
        ing.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - 70);
            stage.setY(event.getScreenY() - 80);
        });
        // die funktion des delete buttons des ing im rezept nach dem hinzufügen
        ring.getButton().setOnAction(event -> {
            recipeIngredients.remove(ring);
            recipeBox.getChildren().remove(ring);
            calculateInTotal();
        });
        // mengeneingabe und calcutation der werte (rezept ing)
        ring.getField().setOnKeyReleased(event -> {
            ring.setMenge(ring.getField().getText());
            calculateInTotal();
        });
    }

    // calculiert die detailwerte (energy, protein usw) (errechnet den insgesamtwert und nicht für 100g)
    private void calculateInTotal() {
        int[] totalArray = new int[6];
        for (RecipeIngredient ingredient : recipeIngredients) {
            String menge = "0";
            if (!ingredient.getField().getText().isEmpty()) {
                menge = ingredient.getField().getText();
            }
            totalArray[0] = totalArray[0] + getValue(menge, ingredient.getEnergy());
            totalArray[1] = totalArray[1] + getValue(menge, ingredient.getSugar());
            totalArray[2] = totalArray[2] + getValue(menge, ingredient.getSaturatedFat());
            totalArray[3] = totalArray[3] + getValue(menge, ingredient.getSalt());
            totalArray[4] = totalArray[4] + getValue(menge, ingredient.getProteins());
            totalArray[5] = totalArray[5] + getValue(menge, ingredient.getFiber());
        }
        // setze die werte in den details
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

    // check ob die maus über dem scrollpane ist (rezeptingredient feld)
    private boolean isMouseOverScrollPane(double mouseX, double mouseY) {
        double scrollX = recipeScroll.localToScreen(0, 0).getX();
        double scrollY = recipeScroll.localToScreen(0, 0).getY();
        double scrollWidth = recipeScroll.getWidth();
        double scrollHeight = recipeScroll.getHeight();
        return mouseX >= scrollX && mouseX <= (scrollX + scrollWidth)
                && mouseY >= scrollY && mouseY <= (scrollY + scrollHeight);
    }

    // ingredient kopieren für die ghostbox
    private Ingredient cloneIngredient(Ingredient original) {
        Ingredient copy = new Ingredient();
        copy.setPrefSize(140, 160);
        copy.setAlignment(Pos.CENTER);
        copy.setImageUrl(original.getImageUrl());
        copy.setName(original.getName());
        return copy;
    }
}
