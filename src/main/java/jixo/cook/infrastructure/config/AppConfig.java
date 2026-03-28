package jixo.cook.infrastructure.config;

import jixo.cook.application.usecase.*;
import jixo.cook.infrastructure.api.GeminiGatewayImpl;
import jixo.cook.infrastructure.api.OpenFoodFactsGateway;
import jixo.cook.infrastructure.repositories.FileImageRepository;
import jixo.cook.infrastructure.repositories.JsonIngredientRepository;
import jixo.cook.infrastructure.repositories.JsonRecipeRepository;
import java.io.File;
import java.io.IOException;

public class AppConfig {

    private static AppConfig instance;

    public final SearchIngredientUseCase searchIngredient;
    public final ImportIngredientUseCase importIngredient;
    public final ManageIngredientUseCase manageIngredient;
    public final CreateRecipeUseCase createRecipe;
    public final ManageRecipeUseCase manageRecipe;
    public final AiIngredientUseCase aiIngredient;

    private AppConfig() {
        initializeFolders();

        FileImageRepository imageRepository = new FileImageRepository();
        try {
            imageRepository.ensureNoCoverExists();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonIngredientRepository ingredientRepository = new JsonIngredientRepository();
        JsonRecipeRepository recipeRepository = new JsonRecipeRepository(ingredientRepository);
        OpenFoodFactsGateway foodSearchGateway = new OpenFoodFactsGateway();

        searchIngredient = new SearchIngredientUseCase(foodSearchGateway);
        importIngredient = new ImportIngredientUseCase(ingredientRepository, imageRepository);
        manageIngredient = new ManageIngredientUseCase(ingredientRepository, recipeRepository);
        createRecipe = new CreateRecipeUseCase(recipeRepository, imageRepository);
        manageRecipe = new ManageRecipeUseCase(recipeRepository);
        aiIngredient = new AiIngredientUseCase(new GeminiGatewayImpl());
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void initializeFolders() {
        File file = new File(System.getProperty("user.dir") + "/storage");
        if (!file.exists()) file.mkdir();
        file = new File(System.getProperty("user.dir") + "/storage/recipe");
        if (!file.exists()) file.mkdir();
        file = new File(System.getProperty("user.dir") + "/storage/ingredient");
        if (!file.exists()) file.mkdir();
        file = new File(System.getProperty("user.dir") + "/storage/images");
        if (!file.exists()) file.mkdir();
    }
}
