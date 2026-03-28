package jixo.cook.domain.model;

import java.util.List;

public class Recipe {

    private String recipeName;
    private String imageURL;
    private String description;
    private List<RecipeIngredient> ingredients;

    public Recipe() {}

    public void setRecipeList(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }

    public void setRecipeName(String name) { this.recipeName = name; }
    public String getRecipeName() { return recipeName; }

    public void setImageURL(String url) { this.imageURL = url; }
    public String getImageURL() { return imageURL; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }
}
