package org.example.gestionstockage1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        ConnexionBDD.initialiserConnexion();
        primaryStage = stage;
        App.lancerPage("Connexion", "Connexion/connexion.fxml");
    }

    public static void changerPage(String titre, String page) throws IOException {
        // Fermer tous les stages ouverts
        Stage currentStage = App.getPrimaryStage();
        currentStage.close();

        // Charger la nouvelle page
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(page));
        Scene scene = new Scene(fxmlLoader.load());

        // Configurer un nouveau Stage
        Stage newStage = new Stage();
        newStage.setTitle(titre);
        newStage.setResizable(false);
        newStage.setScene(scene);
        newStage.show();

        primaryStage = newStage;
    }

    public static void lancerPage(String titre, String page) throws IOException {
        // Charger la nouvelle page
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(page));
        Scene scene = new Scene(fxmlLoader.load());

        // Configurer un nouveau Stage
        Stage stage = new Stage();
        stage.setTitle(titre);
        stage.setScene(scene);
        primaryStage = stage;
        stage.show();
    }

    public static void fermerPage() throws IOException {
        // Fermer tous les stages ouverts
        Stage currentStage = App.getPrimaryStage();
        currentStage.close();
    }


    public static void main(String[] args) {
        launch();
    }
}