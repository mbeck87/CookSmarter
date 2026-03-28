package jixo.cook.presentation.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import jixo.cook.domain.model.Recipe;

public class RecipeCard extends VBox {

    private final Recipe recipe;

    public RecipeCard(Recipe recipe) {
        this.recipe = recipe;

        ImageView img = new ImageView();
        img.setFitWidth(100);
        img.setFitHeight(100);
        img.setPreserveRatio(true);
        this.getChildren().add(img);

        Label title = new Label(recipe.getRecipeName());
        title.getStyleClass().add("food-card-label");
        this.getChildren().add(title);

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(8));
        this.setSpacing(6);
        this.getStyleClass().add("food-card");
        this.setPrefSize(150, 175);
        this.setCursor(Cursor.HAND);

        if (recipe.getImageURL() != null) {
            img.setImage(new Image("file:/" + recipe.getImageURL()));
        }
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
