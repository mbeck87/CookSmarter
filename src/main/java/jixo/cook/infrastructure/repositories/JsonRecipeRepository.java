package jixo.cook.infrastructure.repositories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jixo.cook.domain.interfaces.IngredientRepository;
import jixo.cook.domain.interfaces.RecipeRepository;
import jixo.cook.domain.model.Ingredient;
import jixo.cook.domain.model.Recipe;
import jixo.cook.domain.model.RecipeIngredient;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonRecipeRepository implements RecipeRepository {

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final IngredientRepository ingredientRepository;

    public JsonRecipeRepository(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public void save(Recipe recipe) {
        try {
            ObjectNode node = mapper.createObjectNode();
            node.put("name", recipe.getRecipeName());
            node.put("image", recipe.getImageURL());

            ArrayNode ingredients = mapper.createArrayNode();
            for (RecipeIngredient ri : recipe.getIngredients()) {
                ObjectNode temp = mapper.createObjectNode();
                temp.put("name", ri.getName());
                temp.put("menge", ri.getMenge());
                ingredients.add(temp);
            }
            node.set("ingredients", ingredients);
            node.put("description", recipe.getDescription());

            mapper.writeValue(
                new File(System.getProperty("user.dir") + "/storage/recipe/" + recipe.getRecipeName() + ".json"),
                node
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Recipe> loadAll() {
        List<Recipe> list = new ArrayList<>();
        List<Ingredient> ingredientList = ingredientRepository.loadAll();
        File dir = new File(System.getProperty("user.dir") + "/storage/recipe/");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            try {
                JsonNode node = mapper.readTree(file);
                Recipe recipe = new Recipe();
                recipe.setRecipeName(node.get("name").asText());
                recipe.setImageURL(node.get("image").asText());
                recipe.setDescription(node.get("description").asText());

                List<RecipeIngredient> ringList = new ArrayList<>();
                for (JsonNode ingNode : node.get("ingredients")) {
                    for (Ingredient ing : ingredientList) {
                        if (ingNode.get("name").asText().equals(ing.getName())) {
                            RecipeIngredient ri = new RecipeIngredient(ing);
                            ri.setMenge(ingNode.get("menge").asText());
                            ringList.add(ri);
                            break;
                        }
                    }
                }
                recipe.setRecipeList(ringList);
                list.add(recipe);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    @Override
    public void delete(Recipe recipe) {
        File recipeJson = new File(System.getProperty("user.dir") + "/storage/recipe/" + recipe.getRecipeName() + ".json");
        File image = new File(recipe.getImageURL());
        recipeJson.delete();
        if (!image.getName().contains("noCover")) image.delete();
    }
}
