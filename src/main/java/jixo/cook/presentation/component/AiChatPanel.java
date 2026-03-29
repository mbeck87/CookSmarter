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
    private final Runnable onDeselect;
    private final VBox chatMessages = new VBox(8);
    private final TextField inputField = new TextField();
    private final Button sendButton = new Button("Senden");
    private final Button btnInfo = new Button("Info");
    private final Button btnNaehrwerte = new Button("Nährwerte");
    private Ingredient currentIngredient = null;
    private boolean waiting = false;

    public AiChatPanel(AiIngredientUseCase useCase, Consumer<Ingredient> onIngredientCreated, Runnable onDeselect) {
        this.useCase = useCase;
        this.onIngredientCreated = onIngredientCreated;
        this.onDeselect = onDeselect;
        build();
    }

    public void setIngredient(Ingredient ingredient) {
        this.currentIngredient = ingredient;
        btnInfo.setDisable(ingredient == null);
        btnNaehrwerte.setDisable(ingredient == null);
    }

    public void showAnalysis(Ingredient ingredient) {
        // kept for external calls if needed
        triggerAnalysis(ingredient);
    }

    private void build() {
        getStyleClass().add("ai-chat-panel");
        setPrefWidth(300);
        setSpacing(0);

        Label title = new Label("KI-Assistent");
        title.getStyleClass().add("ai-chat-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setPadding(new Insets(12, 16, 12, 16));

        btnInfo.getStyleClass().add("ai-action-btn");
        btnInfo.setDisable(true);
        btnInfo.setMaxWidth(Double.MAX_VALUE);
        btnInfo.setOnAction(e -> {
            if (currentIngredient != null) triggerAnalysis(currentIngredient);
        });

        btnNaehrwerte.getStyleClass().add("ai-action-btn");
        btnNaehrwerte.setDisable(true);
        btnNaehrwerte.setMaxWidth(Double.MAX_VALUE);
        btnNaehrwerte.setOnAction(e -> triggerNaehrwerte());

        HBox.setHgrow(btnInfo, Priority.ALWAYS);
        HBox.setHgrow(btnNaehrwerte, Priority.ALWAYS);
        HBox actionRow = new HBox(8, btnInfo, btnNaehrwerte);
        actionRow.setPadding(new Insets(10, 12, 6, 12));

        chatMessages.setPadding(new Insets(12));
        chatMessages.setFillWidth(true);

        ScrollPane scroll = new ScrollPane(chatMessages);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("ai-chat-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        chatMessages.heightProperty().addListener((obs, old, val) ->
                scroll.setVvalue(1.0));

        addAiMessage("Hallo! Beschreibe ein Lebensmittel und ich erstelle die Nährwerte dafür. Oder wähle eine Zutat aus und nutze die Buttons oben.");

        inputField.setPromptText("Lebensmittel beschreiben (z.B. Weizenmehl) …");
        inputField.getStyleClass().add("ai-chat-input");
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) send();
        });
        inputField.setOnMouseClicked(e -> inputField.requestFocus());
        HBox.setHgrow(inputField, Priority.ALWAYS);

        sendButton.getStyleClass().add("btn-primary");
        sendButton.setOnAction(e -> send());

        HBox inputRow = new HBox(8, inputField, sendButton);
        inputRow.setPadding(new Insets(6, 12, 12, 12));
        inputRow.setAlignment(Pos.CENTER);

        getChildren().addAll(title, actionRow, scroll, inputRow);
    }

    private void triggerAnalysis(Ingredient ingredient) {
        if (waiting) return;
        addAiMessage("Analysiere " + ingredient.getName() + " …");
        waiting = true;
        setButtonsDisabled(true);
        new Thread(() -> {
            String response = useCase.analyze(ingredient);
            Platform.runLater(() -> {
                addAiMessage(response != null ? response : "Keine Antwort erhalten.");
                done();
            });
        }).start();
    }

    private void triggerNaehrwerte() {
        if (waiting) return;
        String name = currentIngredient.getName();
        addAiMessage("Erstelle Nährwerte für \"" + name + "\" …");
        waiting = true;
        setButtonsDisabled(true);
        new Thread(() -> {
            Ingredient created = useCase.createFromDescription(name);
            String err = useCase.getLastError();
            Platform.runLater(() -> {
                if (created != null) {
                    addAiMessage("Nährwerte für \"" + created.getName() + "\" wurden ins Formular eingetragen. Bitte prüfen und Speichern klicken.");
                    onIngredientCreated.accept(created);
                } else {
                    addAiMessage(err != null ? err : "Ich konnte keine Nährwerte für \"" + name + "\" ermitteln.");
                }
                done();
            });
        }).start();
    }

    private void send() {
        String text = inputField.getText().trim();
        if (text.isEmpty() || waiting) return;

        addUserMessage(text);
        inputField.clear();
        waiting = true;
        setButtonsDisabled(true);

        new Thread(() -> {
            Ingredient created = useCase.createFromDescription(text);
            String err = useCase.getLastError();
            Platform.runLater(() -> {
                if (created != null) {
                    onDeselect.run();
                    addAiMessage("Nährwerte für \"" + created.getName() + "\" wurden ins Formular eingetragen. Bitte prüfen und Speichern klicken.");
                    onIngredientCreated.accept(created);
                } else {
                    addAiMessage(err != null ? err : "Ich konnte keine Nährwerte für diese Beschreibung ermitteln. Bitte versuche es erneut.");
                }
                done();
            });
        }).start();
    }

    private void done() {
        waiting = false;
        setButtonsDisabled(false);
    }

    private void setButtonsDisabled(boolean disabled) {
        btnInfo.setDisable(disabled || currentIngredient == null);
        btnNaehrwerte.setDisable(disabled || currentIngredient == null);
        inputField.setDisable(disabled);
        sendButton.setDisable(disabled);
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
