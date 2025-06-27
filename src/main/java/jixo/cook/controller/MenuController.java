package jixo.cook.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import jixo.cook.scripts.MenuButton;
import jixo.cook.scripts.Recipe;
import jixo.cook.scripts.Translator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuController {

    @FXML
    private Label lCreateRecipe;

    @FXML
    private Label lImport;

    @FXML
    private Label lIngredients;

    @FXML
    private Label lRecipe;

    @FXML
    private BorderPane borderPane;

    @FXML
    private ImageView imageBackground;

    @FXML
    private HBox menuIngredient;

    @FXML
    private HBox menuCreateRecipe;

    @FXML
    private HBox menuImported;

    @FXML
    private HBox menuRecipe;

    private final List<MenuButton> buttonList = new ArrayList<>();
    private final Translator translator = Translator.getInstance();

    // button hashmap (timeline), starthintergrund, hintergrundfarbe und sprache initialisieren
    @FXML
    void initialize() {
        setLanguage();

        buttonList.add(new MenuButton(menuCreateRecipe));
        buttonList.add(new MenuButton(menuIngredient));
        buttonList.add(new MenuButton(menuImported));
        buttonList.add(new MenuButton(menuRecipe));

        imageBackground.setImage(new Image(getClass().getResource("/jixo/cook/images/background.png").toExternalForm()));
        borderPane.setStyle("-fx-background-color: lightblue");
    }

    // buttonaction: rezepte
    @FXML
    void menuRecipeClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jixo/cook/fxml/recipes.fxml"));
            RecipesController recipesController = new RecipesController(this);
            loader.setController(recipesController);
            Node recipes = loader.load();

            select(menuRecipe);
            borderPane.setCenter(recipes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // buttonaction: zutat importieren
    @FXML
    void menuCreateIngredientClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jixo/cook/fxml/importIngredient.fxml"));
            loader.setController(new ImportIngredientController());
            Node importIngredient = loader.load();

            select(menuIngredient);
            borderPane.setCenter(importIngredient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // buttonaction: rezept erstellen
    @FXML
    void menuCreateRecipeClicked(MouseEvent event) {
        loadRecipeView(null);
    }

    // handle für rezept erstellen
    public void loadRecipeView(Recipe recipe) {
        try  {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jixo/cook/fxml/createRecipe.fxml"));
            loader.setController(new CreateRecipeController(recipe));
            Node createRecipe = loader.load();

            select(menuCreateRecipe);
            borderPane.setCenter(createRecipe);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // buttonaction: Zutaten
    @FXML
    void menuImportedIngredientClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jixo/cook/fxml/editIngredients.fxml"));
            loader.setController(new EditIngredientsController());
            Node editIngredients = loader.load();

            select(menuImported);
            borderPane.setCenter(editIngredients);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // button farbwechsel beim betreten der maus
    @FXML
    void menuEntered(MouseEvent event) {
        MenuButton button = getButton((HBox) event.getSource());
        if (!button.getSelected()) {
            button.highlight();
        }
    }

    // button farbwechsel beim verlassen der maus
    @FXML
    void menuExited(MouseEvent event) {
        MenuButton button = getButton((HBox) event.getSource());
        if (!button.getSelected()) {
            button.unhighlight();
        }
    }

    // den richtigen menubutton bekommen
    private MenuButton getButton(HBox box) {
        for(MenuButton button : buttonList) {
            if(button.getBox().equals(box)) {
                return button;
            }
        }
        return null;
    }

    // button auswählen, einfärben und alle anderen deselectieren
    private void select(HBox box) {
        MenuButton button = getButton(box);
        button.setSelected(true);
        for(MenuButton temp : buttonList) {
            if(!temp.equals(button)) {
                temp.setSelected(false);
            }
        }
    }

    // sprache setzen
    public void setLanguage() {
        lCreateRecipe.setText(translator.get("lCreateRecipe"));
        lImport.setText(translator.get("lImport"));
        lIngredients.setText(translator.get("lIngredients"));
        lRecipe.setText(translator.get("lRecipe"));
    }
}
