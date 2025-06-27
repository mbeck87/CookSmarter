package jixo.cook.scripts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jixo.cook.controller.MenuController;
import java.io.IOException;

public class InitializeMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // standartsprache setzen
        Translator translator = Translator.getInstance();
        translator.setLanguage("de");
        // Mainframe laden und öffnen
        FXMLLoader loader = new FXMLLoader(InitializeMain.class.getResource("/jixo/cook/fxml/menu.fxml"));
        MenuController controller = new MenuController();
        loader.setController(controller);
        Scene scene = new Scene(loader.load(), 1200, 600);
        stage.setTitle("CookSmarter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}