package jixo.cook.application.usecase;

import jixo.cook.domain.interfaces.RecipeRepository;
import jixo.cook.domain.model.Recipe;
import java.util.List;

public class ManageRecipeUseCase {

    private final RecipeRepository recipeRepository;

    public ManageRecipeUseCase(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> loadAll() {
        return recipeRepository.loadAll();
    }

    public void delete(Recipe recipe) {
        recipeRepository.delete(recipe);
    }

    public boolean exists(String name) {
        return recipeRepository.loadAll().stream().anyMatch(r -> r.getRecipeName().equals(name));
    }
}
