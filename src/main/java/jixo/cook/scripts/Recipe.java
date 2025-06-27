package jixo.cook.scripts;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.util.List;

public class Recipe extends VBox {

    private List<RecipeIngredient> ingredients;
    private String recipeName;
    private String imageURL;
    private String description;

    private  ImageView img;
    private Label title;

    // constructor eigenschaften und hinzufügen grafischer elemente
    public Recipe() {
        img = new ImageView();
        img.setFitWidth(100);
        img.setFitHeight(100);
        img.setPreserveRatio(true);
        this.getChildren().add(img);

        title = new Label();
        title.setWrapText(true);
        this.getChildren().add(title);

        this.setStyle("-fx-background-color: rgba(100, 149, 237, 0.4); -fx-background-radius: 10px;");
        this.setPrefSize(140, 160);
        this.setCursor(Cursor.HAND);
    }

    // setter und getter
    public void setRecipeList(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setImageURL(String url) {
        imageURL = url;
        img.setImage(new Image("file:/" + url));
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setRecipeName(String name) {
        this.recipeName = name;
        title.setText(name);
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
