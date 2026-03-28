package jixo.cook.infrastructure.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jixo.cook.domain.interfaces.GeminiGateway;
import jixo.cook.domain.model.Ingredient;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GeminiGatewayImpl implements GeminiGateway {

    private static final String API_KEY = loadApiKey();
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    private static String loadApiKey() {
        String envFile = System.getProperty("user.dir") + "/.env";
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("GEMINI_API_KEY=")) {
                    return line.substring("GEMINI_API_KEY=".length()).trim();
                }
            }
        } catch (IOException e) {
            System.err.println(".env nicht gefunden oder nicht lesbar: " + e.getMessage());
        }
        return "";
    }

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private static String errorMessage(String raw) {
        if (raw == null) return "Unbekannter Fehler.";
        String code = raw.substring("ERROR:".length());
        if (code.startsWith("HTTP 400")) return "API-Fehler 400: Ungültige Anfrage. Bitte prüfe den API-Key.";
        if (code.startsWith("HTTP 401") || code.startsWith("HTTP 403")) return "API-Fehler: Zugriff verweigert. Bitte prüfe den API-Key.";
        if (code.startsWith("HTTP 429")) return "API-Fehler: Zu viele Anfragen. Bitte warte kurz.";
        if (code.startsWith("HTTP 5"))   return "API-Fehler: Gemini-Server nicht erreichbar. Bitte versuche es erneut.";
        return "Fehler: " + code;
    }

    @Override
    public String analyzeIngredient(Ingredient ingredient) {
        String prompt = "Du bist ein Ernährungsberater. Analysiere diese Zutat auf Deutsch in 3-5 Sätzen:\n" +
                "Name: " + ingredient.getName() +
                ", Energie: " + ingredient.getEnergy() + " kJ" +
                ", Kohlenhydrate: " + ingredient.getCarbohydrates() + " g" +
                ", davon Zucker: " + ingredient.getSugar() + " g" +
                ", Fett: " + ingredient.getSaturatedFat() + " g" +
                ", Salz: " + ingredient.getSalt() + " g" +
                ", Proteine: " + ingredient.getProteins() + " g" +
                ", Ballaststoffe: " + ingredient.getFiber() + " g";
        String result = sendRequest(prompt);
        if (result != null && result.startsWith("ERROR:")) return errorMessage(result);
        return result;
    }

    @Override
    public Ingredient createFromDescription(String description) {
        String prompt = "Du bist ein Ernährungsexperte. Erstelle Nährwerte pro 100g für: \"" + description + "\"\n" +
                "Antworte NUR mit einem JSON-Objekt, kein Text davor oder danach, keine Markdown-Blöcke:\n" +
                "{\"name\":\"...\",\"energy\":\"...\",\"carbohydrates\":\"...\",\"sugar\":\"...\",\"saturatedFat\":\"...\",\"salt\":\"...\",\"proteins\":\"...\",\"fiber\":\"...\"}\n" +
                "Alle Nährwert-Werte sind Zahlen als String mit Punkt als Dezimaltrennzeichen.";

        String response = sendRequest(prompt);
        if (response == null || response.startsWith("ERROR:")) return null;

        try {
            String json = response.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```[a-z]*\\n?", "").replace("```", "").trim();
            }
            JsonNode node = mapper.readTree(json);
            Ingredient ing = new Ingredient();
            ing.setName(node.path("name").asText("unbekannt"));
            ing.setEnergy(node.path("energy").asText("0"));
            ing.setCarbohydrates(node.path("carbohydrates").asText("0"));
            ing.setSugar(node.path("sugar").asText("0"));
            ing.setSaturatedFat(node.path("saturatedFat").asText("0"));
            ing.setSalt(node.path("salt").asText("0"));
            ing.setProteins(node.path("proteins").asText("0"));
            ing.setFiber(node.path("fiber").asText("0"));
            ing.setImageUrl(System.getProperty("user.dir") + "/storage/images/noCover.jpg");
            return ing;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String sendRequest(String prompt) {
        try {
            ObjectNode part = mapper.createObjectNode();
            part.put("text", prompt);
            ArrayNode parts = mapper.createArrayNode();
            parts.add(part);

            ObjectNode content = mapper.createObjectNode();
            content.set("parts", parts);
            ArrayNode contents = mapper.createArrayNode();
            contents.add(content);

            ObjectNode body = mapper.createObjectNode();
            body.set("contents", contents);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Gemini API Fehler: HTTP " + response.statusCode() + " – " + response.body());
                return "ERROR:HTTP " + response.statusCode();
            }

            JsonNode root = mapper.readTree(response.body());
            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty()) {
                System.err.println("Gemini: keine candidates in Antwort – " + response.body());
                return "ERROR:Keine Antwort von Gemini erhalten.";
            }
            return candidates.get(0).path("content").path("parts").get(0).path("text").asText(null);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "ERROR:" + e.getMessage();
        }
    }
}
