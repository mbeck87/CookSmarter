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
        String name = node.has("product_name") ? node.get("product_name").asText() : "";
        if (name.isEmpty()) return null;

        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);

        // bild setzen
        JsonNode imageNode = node.get("image_url");
        if (imageNode != null && !imageNode.asText().isEmpty()) {
            ingredient.setImageUrl(imageNode.asText());
        } else {
            ingredient.setImageUrl(System.getProperty("user.dir") + "/images/noCover.jpg");
        }

        // nährwerte aus nutriments lesen (per 100g)
        JsonNode n = node.get("nutriments");
        ingredient.setEnergy(nutriValue(n, "energy-kj_100g"));
        ingredient.setSugar(nutriValue(n, "sugars_100g"));
        ingredient.setSaturatedFat(nutriValue(n, "saturated-fat_100g"));
        ingredient.setSalt(nutriValue(n, "salt_100g"));
        ingredient.setProteins(nutriValue(n, "proteins_100g"));
        ingredient.setFiber(nutriValue(n, "fiber_100g"));

        return ingredient;
    }

    private static String nutriValue(JsonNode nutriments, String key) {
        if (nutriments == null) return "unbekannt";
        JsonNode val = nutriments.get(key);
        if (val == null || val.asText().isEmpty()) return "unbekannt";
        return val.asText();
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
