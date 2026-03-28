# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
mvn clean install

# Run the application
mvn javafx:run

# Compile only
mvn compile

# Run tests (JUnit 5 configured but no tests exist yet)
mvn test

# Package
mvn package
```

Java 23 is required. No linter is configured.

## Architecture Overview

CookSmarter is a JavaFX desktop app for managing ingredients and recipes with nutritional data. It uses a local JSON file store and the OpenFoodFacts API.

**Package layout:** `src/main/java/jixo/cook/`
- `domain/` — models (`Ingredient`, `Recipe`, `RecipeIngredient`, `Food`) and repository/gateway interfaces
- `application/usecase/` — use cases: `SearchIngredientUseCase`, `ImportIngredientUseCase`, `ManageIngredientUseCase`, `CreateRecipeUseCase`, `ManageRecipeUseCase`
- `infrastructure/` — API gateway (`OpenFoodFactsGateway`), JSON repositories, image repository, i18n (`Translator`), DI config (`AppConfig`), JavaFX entry points
- `presentation/` — JavaFX FXML controllers and UI components (cards, rows)

**Entry point:** `MainClass.main()` → `InitializeMain.start(Stage)` loads `menu.fxml` and `MenuController`.

### Key classes

- **`AppConfig`** (singleton) — wires all dependencies (repositories, use cases) and initializes storage folders.
- **`Translator`** (singleton) — HashMap-based German/English i18n.
- **`Ingredient`** — pure nutritional data model.
- **`Recipe`** — contains a list of `RecipeIngredient` objects.
- **`RecipeIngredient`** — wraps `Ingredient` with a quantity field.
- **`Food`** (interface) — shared contract for nutritional getters/setters.
- **`IngredientCard`** / **`RecipeCard`** / **`RecipeIngredientRow`** — UI components (extend JavaFX layout classes).

### Data flow

1. User searches → `ImportIngredientController` → `SearchIngredientUseCase` → `OpenFoodFactsGateway` → OpenFoodFacts API (Jackson streaming parser).
2. User confirms import → `ImportIngredientUseCase` → saves `storage/ingredient/{name}.json` + caches image to `storage/images/`.
3. Recipe creation picks ingredients from `storage/ingredient/` JSON files, attaches quantities, saves `storage/recipe/{name}.json`.
4. `RecipesController` recomputes total macros live as quantities change (per-100g basis; KJ → Kcal via ÷ 4.184).

### Local storage

Files are stored under `storage/` relative to the working directory at runtime:
- `storage/ingredient/{name}.json` — ingredient nutritional data
- `storage/recipe/{name}.json` — recipe with embedded ingredient list (`menge` field = grams)
- `storage/images/` — downloaded ingredient/recipe images

### Module system

Java 9+ modules (`module-info.java`). The controller package is opened to `javafx.fxml` for FXML reflection binding.
