package jixo.cook.scripts;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class Ingredient extends VBox implements Food {

    private String name;
    private String imageUrl;
    private String energy;
    private String sugar;
    private String saturatedFat;
    private String salt;
    private String proteins;
    private String fiber;

    private ImageView img;
    private Label title;

    // constructor, setze grafische elemente
    public Ingredient() {
        img = new ImageView();
        img.setFitWidth(100);
        img.setFitHeight(100);
        img.setPreserveRatio(true);
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(img);

        title = new Label();
        title.setWrapText(true);
        this.getChildren().add(title);

        this.setStyle("-fx-background-color: rgba(100, 149, 237, 0.4); -fx-background-radius: 10px;");
        this.setPrefSize(140, 160);
        this.setCursor(Cursor.HAND);
    }

    // ingredient über die json initialisieren
    // (static weil diese methode von der klasse gecalled werden soll, nicht vom objekt)
    public static Ingredient createFromJson(JsonNode node) {
        // überprüfe ob alle notwendigen daten vorhanden sind
        String name = node.has("product_name") ? node.get("product_name").asText() : "";
        if (name.isEmpty()) return null;
        JsonNode nutriData = node.get("nutriscore_data");
        if (nutriData == null) return null;
        JsonNode negatives = nutriData.path("components").get("negative");
        if (negatives == null) return null;
        JsonNode positives = nutriData.path("components").get("positive");
        if (positives == null) return null;

        // erstelle ingredient
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);

        // falls kein bild vorhanden ist, verwende das nocover.jpg
        try {

            String url = node.get("image_url").asText();
            ingredient.setImageUrl(url);
        } catch (Exception e) {
            String url = System.getProperty("user.dir") + "/images/noCover.jpg";
            ingredient.setImageUrl(url);
        }

        // setze inhaltsstoffe
        for (JsonNode val : negatives) {
            String id = val.get("id").asText();
            switch (id) {
                case "energy" -> ingredient.setEnergy(val.get("value").asText());
                case "sugars" -> ingredient.setSugar(val.get("value").asText());
                case "saturated_fat" -> ingredient.setSaturatedFat(val.get("value").asText());
                case "salt" -> ingredient.setSalt(val.get("value").asText());
            }
        }

        for (JsonNode val : positives) {
            String id = val.get("id").asText();
            switch (id) {
                case "proteins" -> ingredient.setProteins(val.get("value").asText());
                case "fiber" -> ingredient.setFiber(val.get("value").asText());
            }
        }
        return ingredient;
    }

    // setter und getter
    @Override
    public String getName() { return name; }

    @Override
    public void setName(String name) {
        this.name = name;
        title.setText(name);
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String path) {
        this.imageUrl = path;
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            img.setImage(new Image(path, true));
        } else {
            img.setImage(new Image("file:/" + path));
        }
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
}
