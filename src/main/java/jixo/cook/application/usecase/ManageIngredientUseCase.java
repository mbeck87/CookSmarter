package jixo.cook.application.usecase;

import jixo.cook.domain.interfaces.IngredientRepository;
import jixo.cook.domain.interfaces.RecipeRepository;
import jixo.cook.domain.model.Ingredient;
import jixo.cook.domain.model.Recipe;
import jixo.cook.domain.model.RecipeIngredient;
import java.util.List;

public class ManageIngredientUseCase {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    public ManageIngredientUseCase(IngredientRepository ingredientRepository, RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    public List<Ingredient> loadAll() {
        return ingredientRepository.loadAll();
    }

    public void delete(Ingredient ingredient) {
        // kaskadierende löschung: alle rezepte mit dieser zutat löschen
        List<Recipe> recipes = recipeRepository.loadAll();
        for (Recipe recipe : recipes) {
            for (RecipeIngredient ri : recipe.getIngredients()) {
                if (ingredient.getName().equalsIgnoreCase(ri.getName())) {
                    recipeRepository.delete(recipe);
                    break;
                }
            }
        }
        ingredientRepository.delete(ingredient);
    }
}
