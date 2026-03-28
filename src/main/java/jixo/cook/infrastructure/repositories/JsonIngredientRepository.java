package jixo.cook.infrastructure.repositories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jixo.cook.domain.interfaces.IngredientRepository;
import jixo.cook.domain.model.Ingredient;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonIngredientRepository implements IngredientRepository {

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public void save(Ingredient ingredient) {
        String path = System.getProperty("user.dir") + "/storage/ingredient/";
        try {
            ObjectNode node = mapper.createObjectNode();
            node.put("name", ingredient.getName());
            node.put("energy", ingredient.getEnergy());
            node.put("sugar", ingredient.getSugar());
            node.put("fat", ingredient.getSaturatedFat());
            node.put("salt", ingredient.getSalt());
            node.put("proteins", ingredient.getProteins());
            node.put("fiber", ingredient.getFiber());
            node.put("carbohydrates", ingredient.getCarbohydrates());
            node.put("url", ingredient.getImageUrl());
            mapper.writeValue(new File(path + ingredient.getName() + ".json"), node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Ingredient> loadAll() {
        List<Ingredient> list = new ArrayList<>();
        File dir = new File(System.getProperty("user.dir") + "/storage/ingredient/");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            try {
                JsonNode node = mapper.readTree(file);
                Ingredient ing = new Ingredient();
                ing.setName(node.get("name").asText());
                ing.setEnergy(node.get("energy").asText());
                ing.setSugar(node.get("sugar").asText());
                ing.setSaturatedFat(node.get("fat").asText());
                ing.setSalt(node.get("salt").asText());
                ing.setProteins(node.get("proteins").asText());
                ing.setFiber(node.get("fiber").asText());
                JsonNode carbNode = node.get("carbohydrates");
                ing.setCarbohydrates(carbNode != null ? carbNode.asText() : "unbekannt");
                ing.setImageUrl(node.get("url").asText());
                list.add(ing);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    @Override
    public void delete(Ingredient ingredient) {
        String ext = getExt(ingredient.getImageUrl());
        File json = new File(System.getProperty("user.dir") + "/storage/ingredient/" + ingredient.getName() + ".json");
        File image = new File(System.getProperty("user.dir") + "/storage/images/" + ingredient.getName() + ext);
        json.delete();
        image.delete();
    }

    private String getExt(String url) {
        int index = url.lastIndexOf(".");
        return url.substring(index);
    }
}
