package jixo.cook.domain.model;

public class RecipeIngredient implements Food {

    private String name;
    private String menge;
    private String imageUrl;
    private String energy;
    private String sugar;
    private String saturatedFat;
    private String salt;
    private String proteins;
    private String fiber;

    public RecipeIngredient() {}

    public RecipeIngredient(Ingredient ingredient) {
        this.name = ingredient.getName();
        this.imageUrl = ingredient.getImageUrl();
        this.energy = ingredient.getEnergy();
        this.sugar = ingredient.getSugar();
        this.saturatedFat = ingredient.getSaturatedFat();
        this.salt = ingredient.getSalt();
        this.proteins = ingredient.getProteins();
        this.fiber = ingredient.getFiber();
    }

    public void setMenge(String menge) { this.menge = menge; }
    public String getMenge() { return menge; }

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
}
