package jixo.cook.presentation.component;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import jixo.cook.domain.model.Ingredient;

public class IngredientCard extends VBox {

    private final Ingredient ingredient;
    private final ImageView img = new ImageView();

    public IngredientCard(Ingredient ingredient) {
        this.ingredient = ingredient;

        ImageView img = this.img;
        img.setFitWidth(100);
        img.setFitHeight(100);
        img.setPreserveRatio(true);
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(img);

        Label title = new Label(ingredient.getName());
        title.getStyleClass().add("food-card-label");
        this.getChildren().add(title);

        this.getStyleClass().add("food-card");
        this.setPrefSize(150, 175);
        this.setCursor(Cursor.HAND);

        String path = ingredient.getImageUrl();
        if (path != null) {
            if (path.startsWith("http://") || path.startsWith("https://")) {
                img.setImage(new Image(path, true));
            } else {
                Image image = new Image("file:/" + path);
                img.setImage(image.isError() ? noCover() : image);
            }
        }
    }

    private static Image noCover() {
        return new Image("file:/" + System.getProperty("user.dir") + "/storage/images/noCover.jpg");
    }

    public void updateImage(String absolutePath) {
        ingredient.setImageUrl(absolutePath);
        img.setImage(new Image("file:/" + absolutePath));
    }

    public Ingredient getIngredient() {
        return ingredient;
    }
}
