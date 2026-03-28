package jixo.cook.domain.interfaces;

import jixo.cook.domain.model.Recipe;
import java.util.List;

public interface RecipeRepository {
    void save(Recipe recipe);
    List<Recipe> loadAll();
    void delete(Recipe recipe);
}
