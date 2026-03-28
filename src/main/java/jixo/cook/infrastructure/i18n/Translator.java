package jixo.cook.infrastructure.i18n;

import java.util.HashMap;

public class Translator {

    private static final Translator TRANSLATOR = new Translator();
    private final HashMap<String, String> map = new HashMap<>();
    private String language = "";

    private Translator() {}

    public static Translator getInstance() {
        return TRANSLATOR;
    }

    public void setLanguage(String language) {
        if (!this.language.equals(language)) {
            this.language = language;
            map.clear();
            writeLanguage();
        }
    }

    private void writeLanguage() {
        if (language.equals("de")) {
            german();
        } else {
            english();
        }
    }

    private void german() {
        map.put("lCreateRecipe", "Rezept erstellen");
        map.put("lImport", "Zutat Importieren");
        map.put("lIngredients", "Zutaten");
        map.put("lRecipe", "Rezepte");
        map.put("bImport", "Importieren");
        map.put("lCreateIngredient", "Zutat erstellen");
        map.put("lEnergy", "kJ:");
        map.put("lFat", "Fett:");
        map.put("lFiber", "Ballaststoffe:");
        map.put("lName", "Name:");
        map.put("lProtein", "Proteine:");
        map.put("lSalt", "Salz:");
        map.put("lSugar", "Zucker:");
        map.put("lSearch", "Suche");
    }

    private void english() {
        map.put("lCreateRecipe", "create recipe");
        map.put("lImport", "import ingredients");
        map.put("lIngredients", "ingredients");
        map.put("lRecipe", "recipes");
        map.put("bImport", "import");
        map.put("lCreateIngredient", "create ingredient");
        map.put("lEnergy", "kJ:");
        map.put("lFat", "fat:");
        map.put("lFiber", "fiber:");
        map.put("lName", "name:");
        map.put("lProtein", "protein:");
        map.put("lSalt", "salt:");
        map.put("lSugar", "sugar:");
        map.put("lSearch", "search");
    }

    public String get(String key) {
        return map.get(key);
    }
}
