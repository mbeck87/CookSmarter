package jixo.cook.application.usecase;

import jixo.cook.domain.interfaces.GeminiGateway;
import jixo.cook.domain.model.Ingredient;

public class AiIngredientUseCase {

    private final GeminiGateway gateway;

    public AiIngredientUseCase(GeminiGateway gateway) {
        this.gateway = gateway;
    }

    public String analyze(Ingredient ingredient) {
        return gateway.analyzeIngredient(ingredient);
    }

    public Ingredient createFromDescription(String description) {
        return gateway.createFromDescription(description);
    }
}
