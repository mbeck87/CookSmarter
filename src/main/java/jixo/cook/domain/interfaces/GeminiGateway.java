package jixo.cook.domain.interfaces;

import jixo.cook.domain.model.Ingredient;

public interface GeminiGateway {

    String analyzeIngredient(Ingredient ingredient);

    Ingredient createFromDescription(String description);
}
