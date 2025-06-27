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
- `scripts/` — models and core logic
- `controller/` — JavaFX FXML controllers

**Entry point:** `MainClass.main()` → `InitializeMain.start(Stage)` loads `menu.fxml` and `MenuController`.

### Key classes

- **`Manager`** (singleton) — central hub: HTTP calls to OpenFoodFacts, JSON read/write via Jackson, image downloading/caching, nutritional calculations.
- **`Translator`** (singleton) — HashMap-based German/English i18n.
- **`Ingredient`** (extends `VBox`) — nutritional model that also renders itself as a clickable card.
- **`Recipe`** (extends `VBox`) — contains a list of `RecipeIngredient` objects; renders as a card.
- **`RecipeIngredient`** (extends `GridPane`) — wraps `Ingredient` with a quantity field; recalculates macros in real time.
- **`Food`** (interface) — shared contract between `Ingredient` and `RecipeIngredient` for nutritional getters/setters.

### Data flow

1. User searches → `ImportIngredientController` → `Manager.search()` → OpenFoodFacts API (Jackson streaming parser).
2. User confirms import → `Manager.importIngredient()` → saves `ingredient/{name}.json` + caches image to `images/`.
3. Recipe creation picks ingredients from `ingredient/` JSON files, attaches quantities, saves `recipe/{name}.json`.
4. `RecipesController.calculateInTotal()` recomputes total macros live as quantities change (per-100g basis; KJ → Kcal via ÷ 4.184).

### Local storage

Files are stored relative to the working directory at runtime:
- `ingredient/{name}.json` — ingredient nutritional data
- `recipe/{name}.json` — recipe with embedded ingredient list (`menge` field = grams)
- `images/` — downloaded ingredient/recipe images

### Module system

Java 9+ modules (`module-info.java`). The controller package is opened to `javafx.fxml` for FXML reflection binding.
