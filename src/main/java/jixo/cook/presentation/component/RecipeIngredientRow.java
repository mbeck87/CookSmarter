package jixo.cook.presentation.component;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import jixo.cook.domain.model.RecipeIngredient;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class RecipeIngredientRow extends GridPane {

    private final RecipeIngredient ingredient;
    private final Button button;
    private final TextField field;

    public RecipeIngredientRow(RecipeIngredient ingredient) {
        this.ingredient = ingredient;

        this.getStyleClass().add("ingredient-row");
        this.setHgap(8);
        this.setVgap(0);
        GridPane.setMargin(this, new Insets(0, 0, 4, 0));

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(18);
        this.getColumnConstraints().add(col);
        col = new ColumnConstraints();
        col.setPercentWidth(42);
        this.getColumnConstraints().add(col);
        col = new ColumnConstraints();
        col.setPercentWidth(25);
        this.getColumnConstraints().add(col);
        col = new ColumnConstraints();
        col.setPercentWidth(15);
        this.getColumnConstraints().add(col);

        ImageView view = new ImageView(new Image("file:/" + ingredient.getImageUrl()));
        view.setPreserveRatio(true);
        view.setFitWidth(40);
        view.setFitHeight(40);
        this.add(view, 0, 0);

        Label label = new Label(ingredient.getName());
        label.getStyleClass().add("ingredient-row-label");
        label.setWrapText(true);
        label.setMaxHeight(50);
        this.add(label, 1, 0);

        Pattern validNumberPattern = Pattern.compile("\\d*");
        UnaryOperator<TextFormatter.Change> filter = change ->
            validNumberPattern.matcher(change.getControlNewText()).matches() ? change : null;

        field = new TextField();
        field.getStyleClass().add("ingredient-row-field");
        field.setTextFormatter(new TextFormatter<>(filter));
        this.add(field, 2, 0);

        button = new Button("✕");
        button.getStyleClass().add("btn-del");
        GridPane.setHalignment(button, HPos.CENTER);
        this.add(button, 3, 0);
    }

    public RecipeIngredient getIngredient() { return ingredient; }
    public Button getButton() { return button; }
    public TextField getField() { return field; }
}
