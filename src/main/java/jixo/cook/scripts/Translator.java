package jixo.cook.scripts;

import java.util.HashMap;

public class Translator {

    // singleton
    private static final Translator TRANSLATOR = new Translator();
    private final HashMap<String, String> map = new HashMap<>();
    private String language = "";

    private Translator() {
    }

    // instanz holen
    public static Translator getInstance() {
        return TRANSLATOR;
    }

    // die sprache setzen
    public void setLanguage(String language) {
        if(!this.language.equals(language)) {
            this.language = language;
            map.clear();
            writeLanguage();
        }
    }

    // für die jeweilige sprache die map schreiben
    private void writeLanguage() {
        if (language.equals("de")) {
            german();
        } else {
            english();
        }
    }

    // deutsch
    private void german() {
        // menu
        map.put("lCreateRecipe", "Rezept erstellen");
        map.put("lImport", "Zutat Importieren");
        map.put("lIngredients", "Zutaten");
        map.put("lRecipe", "Rezepte");
        // import ingredients
        map.put("bImport", "Importieren");
        map.put("lCreateIngredient", "Zutat erstellen");
        map.put("lEnergy", "kj (kcal):");
        map.put("lFat", "Fett:");
        map.put("lFiber",  "Ballaststoffe:");
        map.put("lName", "Name:");
        map.put("lProtein", "Proteine:");
        map.put("lSalt", "Salz:");
        map.put("lSugar", "Zucker:");
        map.put("lSearch", "Suche");
    }

    // englisch
    private void english() {
        // menu
        map.put("lCreateRecipe", "create recipe");
        map.put("lImport", "import ingredients");
        map.put("lIngredients", "ingredients");
        map.put("lRecipe", "recipes");
        // import ingredients
        map.put("bImport", "import");
        map.put("lCreateIngredient", "create ingredient");
        map.put("lEnergy", "kj (kcal):");
        map.put("lFat", "fat:");
        map.put("lFiber",  "fiber:");
        map.put("lName", "name:");
        map.put("lProtein", "protein:");
        map.put("lSalt", "salt:");
        map.put("lSugar", "sugar:");
        map.put("lSearch", "search");
    }

    // den key bekommen
    public String get(String key) {
        return map.get(key);
    }
}
