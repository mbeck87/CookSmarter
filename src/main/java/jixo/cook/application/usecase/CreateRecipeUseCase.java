package jixo.cook.application.usecase;

import jixo.cook.domain.interfaces.ImageRepository;
import jixo.cook.domain.interfaces.RecipeRepository;
import jixo.cook.domain.model.Recipe;
import java.io.File;

public class CreateRecipeUseCase {

    private final RecipeRepository recipeRepository;
    private final ImageRepository imageRepository;

    public CreateRecipeUseCase(RecipeRepository recipeRepository, ImageRepository imageRepository) {
        this.recipeRepository = recipeRepository;
        this.imageRepository = imageRepository;
    }

    public void save(Recipe recipe) {
        imageRepository.copyLocal(recipe.getImageURL());
        File file = new File(recipe.getImageURL());
        String newPath = System.getProperty("user.dir") + "/storage/images/" + file.getName();
        newPath = newPath.replace('\\', '/');
        recipe.setImageURL(newPath);
        recipeRepository.save(recipe);
    }
}
