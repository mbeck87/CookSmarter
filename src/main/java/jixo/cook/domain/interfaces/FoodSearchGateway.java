package jixo.cook.domain.interfaces;

import jixo.cook.domain.model.Ingredient;
import java.util.List;

public interface FoodSearchGateway {
    List<Ingredient> search(String query);
}
