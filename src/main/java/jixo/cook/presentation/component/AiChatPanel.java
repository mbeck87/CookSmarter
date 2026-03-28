package jixo.cook.presentation.component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jixo.cook.application.usecase.AiIngredientUseCase;
import jixo.cook.domain.model.Ingredient;

import java.util.function.Consumer;

public class AiChatPanel extends VBox {

    private final AiIngredientUseCase useCase;
    private final Consumer<Ingredient> onIngredientCreated;
    private final VBox chatMessages = new VBox(8);
    private final TextField inputField = new TextField();
    private final Button sendButton = new Button("Senden");
    private boolean waiting = false;

    public AiChatPanel(AiIngredientUseCase useCase, Consumer<Ingredient> onIngredientCreated) {
        this.useCase = useCase;
        this.onIngredientCreated = onIngredientCreated;
        build();
    }

    public void setIngredient(Ingredient ingredient) {
        // kept for deselect-reset, no mode logic needed
    }

    public void showAnalysis(Ingredient ingredient) {
        if (waiting) return;
        addAiMessage("Analysiere " + ingredient.getName() + " …");
        waiting = true;
        inputField.setDisable(true);
        sendButton.setDisable(true);
        new Thread(() -> {
            String response = useCase.analyze(ingredient);
            Platform.runLater(() -> {
                addAiMessage(response != null ? response : "Keine Antwort erhalten.");
                done();
            });
        }).start();
    }

    private void build() {
        getStyleClass().add("ai-chat-panel");
        setPrefWidth(300);
        setSpacing(0);

        Label title = new Label("KI-Assistent");
        title.getStyleClass().add("ai-chat-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setPadding(new Insets(12, 16, 12, 16));

        chatMessages.setPadding(new Insets(12));
        chatMessages.setFillWidth(true);

        ScrollPane scroll = new ScrollPane(chatMessages);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("ai-chat-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        chatMessages.heightProperty().addListener((obs, old, val) ->
                scroll.setVvalue(1.0));

        addAiMessage("Hallo! Beschreibe ein Lebensmittel und ich erstelle die Nährwerte dafür.");

        inputField.setPromptText("Lebensmittel beschreiben (z.B. Weizenmehl) …");
        inputField.getStyleClass().add("ai-chat-input");
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) send();
        });
        HBox.setHgrow(inputField, Priority.ALWAYS);

        sendButton.getStyleClass().add("btn-primary");
        sendButton.setOnAction(e -> send());

        HBox inputRow = new HBox(8, inputField, sendButton);
        inputRow.setPadding(new Insets(10, 12, 12, 12));
        inputRow.setAlignment(Pos.CENTER);

        getChildren().addAll(title, scroll, inputRow);
    }

    private void send() {
        String text = inputField.getText().trim();
        if (text.isEmpty() || waiting) return;

        addUserMessage(text);
        inputField.clear();
        waiting = true;
        inputField.setDisable(true);
        sendButton.setDisable(true);

        new Thread(() -> {
            Ingredient created = useCase.createFromDescription(text);
            Platform.runLater(() -> {
                if (created != null) {
                    addAiMessage("Ich habe die Nährwerte für \"" + created.getName() + "\" erstellt und in das Formular eingetragen. Bitte prüfe die Werte und klicke auf Speichern.");
                    onIngredientCreated.accept(created);
                } else {
                    addAiMessage("Ich konnte keine Nährwerte für diese Beschreibung ermitteln. Bitte versuche es erneut.");
                }
                done();
            });
        }).start();
    }

    private void done() {
        waiting = false;
        inputField.setDisable(false);
        sendButton.setDisable(false);
    }

    private void addUserMessage(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(220);
        label.getStyleClass().add("ai-msg-user");

        HBox row = new HBox(label);
        row.setAlignment(Pos.CENTER_RIGHT);
        chatMessages.getChildren().add(row);
    }

    private void addAiMessage(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(220);
        label.getStyleClass().add("ai-msg-ai");

        HBox row = new HBox(label);
        row.setAlignment(Pos.CENTER_LEFT);
        chatMessages.getChildren().add(row);
    }
}
