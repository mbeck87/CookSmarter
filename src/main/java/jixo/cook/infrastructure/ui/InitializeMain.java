package jixo.cook.infrastructure.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jixo.cook.infrastructure.config.AppConfig;
import jixo.cook.infrastructure.i18n.Translator;
import jixo.cook.presentation.controller.MenuController;
import java.io.IOException;

public class InitializeMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Translator.getInstance().setLanguage("de");
        AppConfig.getInstance(); // initialisiert ordner + noCover

        FXMLLoader loader = new FXMLLoader(InitializeMain.class.getResource("/jixo/cook/fxml/menu.fxml"));
        MenuController controller = new MenuController();
        loader.setController(controller);
        Scene scene = new Scene(loader.load(), 1440, 820);
        stage.setTitle("CookSmarter");
        stage.setMinWidth(1440);
        stage.setMinHeight(820);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
