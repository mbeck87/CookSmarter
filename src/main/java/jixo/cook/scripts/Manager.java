package jixo.cook.scripts;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Manager {
    // singleton
    private static final Manager INSTANCE = new Manager();
    private final Translator translator = Translator.getInstance();

    private Manager() {
        // falls ordner (images, ingredient, recipe) nicht vorhanden: erstellen
        initializeFolders();
        // das integrierte cover in den cover ordner kopieren
        try {
            copyNoCover();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // getter für singleton
    public static Manager getInstance() {
        return INSTANCE;
    }

    // falls ordner (images, ingredient, recipe) nicht vorhanden: erstellen
    public void initializeFolders() {
        File file = new File(System.getProperty("user.dir") + "/recipe");
        if(!file.exists()) {
            file.mkdir();
        }
        file = new File(System.getProperty("user.dir") + "/ingredient");
        if(!file.exists()) {
            file.mkdir();
        }
        file = new File(System.getProperty("user.dir") + "/images");
        if(!file.exists()) {
            file.mkdir();
        }
    }

    // das integrierte cover in den cover ordner kopieren
    private void copyNoCover() throws IOException {
        File outputFile = new File(System.getProperty("user.dir") + "/images/noCover.jpg");
        if(!outputFile.exists()) {
            InputStream stream = getClass().getResourceAsStream("/jixo/cook/images/noCover.jpg");
            assert stream != null;
            Files.copy(stream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            stream.close();
        }
    }

    // suchanfrage nach ingredient an die datenbank von openfoodfacts
    // (mit inputstream um die speichernutzung und performance zu verbesser)
    public List<JsonNode> search(String query) {
        List<JsonNode> list = new ArrayList<>();
        if (query.contains(" ")) {
            query = query.replace(" ", "_");
        }
        try {
            URI url = new URI("https://world.openfoodfacts.org/cgi/search.pl?search_terms=" + query + "&json=true");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(url).timeout(Duration.ofSeconds(25)).build();
            System.out.println("gesendet");
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            System.out.println("erhalten");

            JsonFactory factory = new JsonFactory();
            ObjectMapper mapper = new ObjectMapper(factory);
            JsonParser parser = factory.createParser(response.body());

            System.out.println("vor der schleife");
            while (!parser.isClosed()) {
                System.out.println("in der schleife");
                // hier try and catch falls ergebnis leer ist!
                JsonToken token = parser.nextToken();
                System.out.println("token: " + token);
                if (JsonToken.FIELD_NAME.equals(token) && "products".equals(parser.getCurrentName())) {
                    System.out.println("sind im array");
                    // nächster eintrag im array
                    parser.nextToken();
                    // lese alles im array
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        System.out.println("lets go");
                        JsonNode productNode = mapper.readTree(parser);
                        list.add(productNode);
                    }
                    break;
                }
            }
            parser.close();
            client.close();

            // falls iwas fehlschlägt, zeige error msg
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText("Ein Fehler ist aufgetreten");
                alert.setContentText("Es gab ein Problem bei der Verarbeitung Ihrer Anfrage.");
                alert.showAndWait();
            });
        }
        System.out.println("fertig");
        return list;
    }

    // bild in den imageordner laden
    private void copyPicture(String name, String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        client.close();

        byte[] imageBytes = response.body();
        File file = new File(System.getProperty("user.dir") + "/images/" + name);
        FileOutputStream out = new FileOutputStream(file);
        out.write(imageBytes);
        out.close();
    }

    // locales bild in den image ordner kopieren
    private void copyPicturePC(String targetPath) {
        Path quelle = Paths.get(targetPath);
        Path dateiName = quelle.getFileName();
        Path zielOrdner = Paths.get(System.getProperty("user.dir") + "/images/");
        Path ziel = zielOrdner.resolve(dateiName);

        if(!quelle.equals(ziel)) {
            try {
                Files.copy(quelle, ziel, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("Fehler beim Kopieren: " + e.getMessage());
            }
        }
    }

    // dateityp erhalten
    private String getExt(String url) {
        int index = url.lastIndexOf(".");
        return url.substring(index);
    }

    // ingredient (online) importieren (ingredient json im ingredient ordner erstellen)
    public void importIngredient(Ingredient ingredient) {
        // wenn das image online ist herunter laden und local path setzen
        if(ingredient.getImageUrl().startsWith("http")) {
            try {
                String name = ingredient.getName() + getExt(ingredient.getImageUrl());
                copyPicture(name, ingredient.getImageUrl());
                String path = System.getProperty("user.dir") + "/images/" + name;
                path = path.replace('\\', '/');
                ingredient.setImageUrl(path);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // kein bild? setze nocover.jpg als path
        else if(ingredient.getImageUrl().contains("noCover")) {
            String path = System.getProperty("user.dir") + "/images/noCover.jpg";
            path = path.replace('\\', '/');
            ingredient.setImageUrl(path);
        }

        // create json (ingredientornder)
        String path = System.getProperty("user.dir") + "/ingredient/";
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();

            node.put("name", ingredient.getName());
            node.put("energy", ingredient.getEnergy());
            node.put("sugar", ingredient.getSugar());
            node.put("fat", ingredient.getSaturatedFat());
            node.put("salt", ingredient.getSalt());
            node.put("proteins", ingredient.getProteins());
            node.put("fiber", ingredient.getFiber());
            node.put("url", ingredient.getImageUrl());

            mapper.writeValue(new File(path + ingredient.getName() + ".json"), node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // alle vorhandenen Ingredients laden und in einer liste wiedergeben
    public List<Ingredient> loadIngredients() {
        List<Ingredient> list = new ArrayList<>();
        File dir = new File(System.getProperty("user.dir") + "/ingredient/");
        ObjectMapper mapper = new ObjectMapper();
        for(File file : Objects.requireNonNull(dir.listFiles())) {
            try {
                JsonNode node = mapper.readTree(file);
                Ingredient ing = new Ingredient();
                ing.setAlignment(Pos.CENTER);
                ing.setName(node.get("name").asText());
                ing.setEnergy(node.get("energy").asText());
                ing.setSugar(node.get("sugar").asText());
                ing.setSaturatedFat(node.get("fat").asText());
                ing.setSalt(node.get("salt").asText());
                ing.setProteins(node.get("proteins").asText());
                ing.setFiber(node.get("fiber").asText());
                ing.setImageUrl(node.get("url").asText());
                list.add(ing);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    // ingredient und alle rezepte die dieses beinhaltet löschen
    public void deleteIngredient(Ingredient ingredient) {
        List<Recipe> list = loadRecipes();
        for(Recipe recipe : list) {
            for(RecipeIngredient ing : recipe.getIngredients()) {
                if(ingredient.getName().equalsIgnoreCase(ing.getName())) {
                    File recipeJson = new File(System.getProperty("user.dir") + "/recipe/" + recipe.getRecipeName() + ".json");
                    File image = new File(recipe.getImageURL());
                    recipeJson.delete();
                    if(!image.getName().contains("noCover")) image.delete();
                    break;
                }
            }
        }
        File json = new File(System.getProperty("user.dir") + "/ingredient/" + ingredient.getName() + ".json");
        File image = new File(System.getProperty("user.dir") + "/images/" + ingredient.getName() + getExt(ingredient.getImageUrl()));
        json.delete();
        image.delete();
    }

    public void deleteRecipe(Recipe recipe) {
        File recipeJson = new File(System.getProperty("user.dir") + "/recipe/" + recipe.getRecipeName() + ".json");
        File image = new File(recipe.getImageURL());
        recipeJson.delete();
        if(!image.getName().contains("noCover")) image.delete();
    }

    // rezept speichern (als json)
    public void saveRecipe(Recipe recipe) {
        // bild in images ordner kopieren
        copyPicturePC(recipe.getImageURL());
        // json anlegen
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("name", recipe.getRecipeName());
        // temp file erstellen um neuen imageurl pfad zu setzen
        File file = new File(recipe.getImageURL());
        recipe.setImageURL(System.getProperty("user.dir") + "/images/" + file.getName());
        node.put("image", recipe.getImageURL());
        //ingredients array erstellen (name, menge)
        ArrayNode ingredients = mapper.createArrayNode();
        for(RecipeIngredient ingredient : recipe.getIngredients()) {
            ObjectNode temp = mapper.createObjectNode();
            temp.put("name", ingredient.getName());
            temp.put("menge", ingredient.getMenge());
            ingredients.add(temp);
        }
        node.set("ingredients", ingredients);
        node.put("description", recipe.getDescription());
        // json schreiben (recipe ordner)
        try {
            mapper.writeValue(new File(System.getProperty("user.dir") + "/recipe/" + recipe.getRecipeName() + ".json"), node);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Erfolg");
            alert.setHeaderText("Rezept gespeichert");
            alert.setContentText("Das Rezept wurde erfolgreich gespeichert.");
            alert.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // alle rezepte laden und als liste wiedergeben
    public List<Recipe> loadRecipes() {
        List<Recipe> list = new ArrayList<>();
        List<Ingredient> ingredientList = loadIngredients();
        File dir = new File(System.getProperty("user.dir") + "/recipe/");
        ObjectMapper mapper = new ObjectMapper();
        // alle jsons im recipe ordner laden
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            List<RecipeIngredient> ringList = new ArrayList<>();
            // konvertierung von json zu recipe
            try {
                JsonNode node = mapper.readTree(file);
                Recipe recipe = new Recipe();
                recipe.setRecipeName(node.get("name").asText());
                recipe.setImageURL(node.get("image").asText());
                recipe.setDescription(node.get("description").asText());
                recipe.setAlignment(Pos.CENTER);

                JsonNode ingredients = node.get("ingredients");
                for(JsonNode ingredient : ingredients) {
                    for(Ingredient ing : ingredientList) {
                        if(ingredient.get("name").asText().equals(ing.getName())) {
                            RecipeIngredient ring = new RecipeIngredient(ing);
                            ring.setMenge(ingredient.get("menge").asText());
                            ringList.add(ring);
                            break;
                        }
                    }
                }
                recipe.setRecipeList(ringList);
                list.add(recipe);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    // berechne kcal von kj
    public int getKcal(int kj) {
        // Runden vor dem Casting
        int result = (int) Math.round( kj / 4.184);
        return result;
    }
}
