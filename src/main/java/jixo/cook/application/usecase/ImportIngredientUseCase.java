package jixo.cook.application.usecase;

import jixo.cook.domain.interfaces.ImageRepository;
import jixo.cook.domain.interfaces.IngredientRepository;
import jixo.cook.domain.model.Ingredient;

public class ImportIngredientUseCase {

    private final IngredientRepository ingredientRepository;
    private final ImageRepository imageRepository;

    public ImportIngredientUseCase(IngredientRepository ingredientRepository, ImageRepository imageRepository) {
        this.ingredientRepository = ingredientRepository;
        this.imageRepository = imageRepository;
    }

    public void importIngredient(Ingredient ingredient) {
        if (ingredient.getImageUrl().startsWith("http")) {
            try {
                String name = ingredient.getName() + getExt(ingredient.getImageUrl());
                imageRepository.downloadAndSave(name, ingredient.getImageUrl());
                String path = System.getProperty("user.dir") + "/storage/images/" + name;
                path = path.replace('\\', '/');
                ingredient.setImageUrl(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (ingredient.getImageUrl().contains("noCover")) {
            String path = System.getProperty("user.dir") + "/storage/images/noCover.jpg";
            path = path.replace('\\', '/');
            ingredient.setImageUrl(path);
        }
        ingredientRepository.save(ingredient);
    }

    private String getExt(String url) {
        int index = url.lastIndexOf(".");
        return url.substring(index);
    }
}
