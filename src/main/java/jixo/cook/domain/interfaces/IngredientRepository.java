package jixo.cook.domain.interfaces;

import jixo.cook.domain.model.Ingredient;
import java.util.List;

public interface IngredientRepository {
    void save(Ingredient ingredient);
    List<Ingredient> loadAll();
    void delete(Ingredient ingredient);
}
