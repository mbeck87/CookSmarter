package jixo.cook.infrastructure.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import jixo.cook.domain.interfaces.FoodSearchGateway;
import jixo.cook.domain.model.Ingredient;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OpenFoodFactsGateway implements FoodSearchGateway {

    @Override
    public List<Ingredient> search(String query) {
        List<Ingredient> list = new ArrayList<>();
        if (query.contains(" ")) {
            query = query.replace(" ", "_");
        }
        try {
            URI url = new URI("https://de.openfoodfacts.org/cgi/search.pl?search_terms=" + query + "&json=true");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(url).timeout(Duration.ofSeconds(25)).build();

            HttpResponse<InputStream> response = null;
            for (int i = 0; i < 50; i++) {
                System.out.println("Versuch " + (i + 1));
                response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() == 200) break;
                System.err.println("API Fehler: HTTP " + response.statusCode() + " – nochmal versuchen...");
                response.body().close();
                response = null;
            }

            if (response == null) {
                client.close();
                throw new IOException("API nach 50 Versuchen nicht erreichbar");
            }

            JsonFactory factory = new JsonFactory();
            ObjectMapper mapper = new ObjectMapper(factory);
            JsonParser parser = factory.createParser(response.body());

            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(token) && "products".equals(parser.currentName())) {
                    parser.nextToken();
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        JsonNode productNode = mapper.readTree(parser);
                        Ingredient ingredient = Ingredient.createFromJson(productNode);
                        if (ingredient != null) {
                            list.add(ingredient);
                        }
                    }
                    break;
                }
            }
            parser.close();
            client.close();

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
}
