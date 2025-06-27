package jixo.cook.scripts;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class RecipeIngredient extends GridPane implements Food {

    private String name;
    private String menge;
    private String imageUrl;
    private String energy;
    private String sugar;
    private String saturatedFat;
    private String salt;
    private String proteins;
    private String fiber;

    private Button button;
    private TextField field;

    // convertierung von Ingredient zu RecipeIngredient
    public RecipeIngredient(Ingredient ingredient) {
        this.name = ingredient.getName();
        this.imageUrl = ingredient.getImageUrl();
        this.energy = ingredient.getEnergy();
        this.sugar = ingredient.getSugar();
        this.saturatedFat = ingredient.getSaturatedFat();
        this.salt = ingredient.getSalt();
        this.proteins = ingredient.getProteins();
        this.fiber = ingredient.getFiber();

        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(20);
        this.getColumnConstraints().add(column);

        column = new ColumnConstraints();
        column.setPercentWidth(40);
        this.getColumnConstraints().add(column);

        column = new ColumnConstraints();
        column.setPercentWidth(25);
        this.getColumnConstraints().add(column);

        column = new ColumnConstraints();
        column.setPercentWidth(15);
        this.getColumnConstraints().add(column);

        ImageView view = new ImageView(new Image("file:/" + imageUrl));
        view.setPreserveRatio(true);
        view.setFitWidth(50);
        view.setFitHeight(50);
        this.add(view, 0, 0);

        Label label = new Label(name);
        label.setWrapText(true);
        label.setMaxHeight(50);
        this.add(label, 1, 0);

        // nur zahlen können als wert im textfeld eingegeben werden
        Pattern validNumberPattern = Pattern.compile("\\d*");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (validNumberPattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        field = new TextField();
        field.setTextFormatter(textFormatter);
        this.add(field, 2, 0);

        button = new Button("del");
        GridPane.setHalignment(button, HPos.CENTER);
        this.add(button, 3, 0);
    }

    // setter und getter
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(String energy) {
        this.energy = energy;
    }

    @Override
    public String getSugar() {
        return sugar;
    }

    @Override
    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    @Override
    public String getSaturatedFat() {
        return saturatedFat;
    }

    @Override
    public void setSaturatedFat(String saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    @Override
    public String getSalt() {
        return salt;
    }

    @Override
    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String getProteins() {
        return proteins;
    }

    @Override
    public void setProteins(String proteins) {
        this.proteins = proteins;
    }

    @Override
    public String getFiber() {
        return fiber;
    }

    @Override
    public void setFiber(String fiber) {
        this.fiber = fiber;
    }

    public Button getButton() {
        return button;
    }

    public TextField getField() {
        return field;
    }

    public void setMenge(String menge) {
        this.menge = menge;
    }

    public String getMenge() {
        return menge;
    }
}
