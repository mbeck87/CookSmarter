package jixo.cook.application.usecase;

import jixo.cook.domain.interfaces.FoodSearchGateway;
import jixo.cook.domain.model.Ingredient;
import java.util.List;

public class SearchIngredientUseCase {

    private final FoodSearchGateway gateway;

    public SearchIngredientUseCase(FoodSearchGateway gateway) {
        this.gateway = gateway;
    }

    public List<Ingredient> search(String query) {
        return gateway.search(query);
    }
}
