package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        // Käynnistää JavaFX-sovelluksen
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Lataa FXML-tiedoston ja luo HBox-root-objekti
        HBox root = FXMLLoader.load(getClass().getResource("/application/primary.fxml"));
        Scene scene = new Scene(root, 1200, 700);
        // Lisää CSS-tyylitiedoston Sceneen
        scene.getStylesheets().add(getClass().getResource("/application/style.css").toExternalForm());
        primaryStage.setTitle("Veikkaussovellus");
        // Aseta Scene ensisijaiselle Stage:lle
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}