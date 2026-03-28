package jixo.cook.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

public class Ingredient implements Food {

    private String name;
    private String imageUrl;
    private String energy;
    private String sugar;
    private String saturatedFat;
    private String salt;
    private String proteins;
    private String fiber;
    private String carbohydrates;

    public Ingredient() {}

    // ingredient über die json von openfoodfacts initialisieren
    public static Ingredient createFromJson(JsonNode node) {
        String name = node.has("product_name") ? node.get("product_name").asText() : "";
        if (name.isEmpty()) return null;

        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);

        JsonNode imageNode = node.get("image_url");
        if (imageNode != null && !imageNode.asText().isEmpty()) {
            ingredient.setImageUrl(imageNode.asText());
        } else {
            ingredient.setImageUrl(System.getProperty("user.dir") + "/storage/images/noCover.jpg");
        }

        JsonNode n = node.get("nutriments");
        ingredient.setEnergy(nutriValue(n, "energy-kj_100g"));
        ingredient.setSugar(nutriValue(n, "sugars_100g"));
        ingredient.setSaturatedFat(nutriValue(n, "saturated-fat_100g"));
        ingredient.setSalt(nutriValue(n, "salt_100g"));
        ingredient.setProteins(nutriValue(n, "proteins_100g"));
        ingredient.setFiber(nutriValue(n, "fiber_100g"));
        ingredient.setCarbohydrates(nutriValue(n, "carbohydrates_100g"));

        return ingredient;
    }

    private static String nutriValue(JsonNode nutriments, String key) {
        if (nutriments == null) return "unbekannt";
        JsonNode val = nutriments.get(key);
        if (val == null || val.asText().isEmpty()) return "unbekannt";
        return val.asText();
    }

    @Override public String getName() { return name; }
    @Override public void setName(String name) { this.name = name; }
    @Override public String getImageUrl() { return imageUrl; }
    @Override public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    @Override public String getEnergy() { return energy; }
    @Override public void setEnergy(String energy) { this.energy = energy; }
    @Override public String getSugar() { return sugar; }
    @Override public void setSugar(String sugar) { this.sugar = sugar; }
    @Override public String getSaturatedFat() { return saturatedFat; }
    @Override public void setSaturatedFat(String saturatedFat) { this.saturatedFat = saturatedFat; }
    @Override public String getSalt() { return salt; }
    @Override public void setSalt(String salt) { this.salt = salt; }
    @Override public String getProteins() { return proteins; }
    @Override public void setProteins(String proteins) { this.proteins = proteins; }
    @Override public String getFiber() { return fiber; }
    @Override public void setFiber(String fiber) { this.fiber = fiber; }
    @Override public String getCarbohydrates() { return carbohydrates; }
    @Override public void setCarbohydrates(String carbohydrates) { this.carbohydrates = carbohydrates; }
}
