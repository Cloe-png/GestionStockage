package org.example.gestionstockage1.controllers.Stock;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.gestionstockage1.App;
import org.example.gestionstockage1.ConnexionBDD;
import org.example.gestionstockage1.controllers.SessionUtilisateur;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockController {

    @FXML
    VBox vboxx;

    @FXML
    TextField textFieldRecherche;


    public void allerPageProfil(ActionEvent actionEvent) throws IOException {
        if(SessionUtilisateur.getRole().equals("admin")) {
            App.changerPage("Profil", "Profil/profilAdmin.fxml");
        } else {
            App.changerPage("Profil", "Profil/profil.fxml");
        }
    }

    public void allerPageStock(ActionEvent actionEvent) throws IOException {
        App.changerPage("Stock", "Stock/stock.fxml");
    }

    public void allerPageChantiers(ActionEvent actionEvent) throws IOException {
        App.changerPage("Chantiers", "Chantiers/chantiers.fxml");
    }

    public void allerPageHistorique(ActionEvent actionEvent) throws IOException {
        App.changerPage("Historique", "Historique/historique.fxml");
    }

    public void allerPageDeconnexion(ActionEvent actionEvent) throws IOException {
        App.changerPage("Connexion", "Connexion/connexion.fxml");
    }

    public void initialize() {
        rechercherMateriel(null);
    }




    public void rechercherMateriel(KeyEvent keyEvent) {
        String recherche = textFieldRecherche.getText().toLowerCase().trim();
        vboxx.getChildren().clear();

        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                String query = "SELECT * FROM materiel";
                PreparedStatement ps = c.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("idMateriel");
                    String nom = rs.getString("nomMateriel");
                    String type = rs.getString("typeMateriel");
                    int stock = rs.getInt("stockMateriel");

                    String contenu = (nom + " " + type).toLowerCase();
                    if (contenu.contains(recherche)) {
                        creerLigne(id, nom, type, stock);
                    }
                }

                ps.close();
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void creerLigne(int id, String nom, String type, int stock) {
        HBox ligne = new HBox();
        ligne.setSpacing(20);
        ligne.setStyle(
                "-fx-background-color: #f5f5f5;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);" +
                        "-fx-margin-bottom: 10;"
        );

        ligne.setAlignment(Pos.CENTER_LEFT);

        Label lblNom = new Label(nom);
        lblNom.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblType = new Label(" (" + type + ")");
        lblType.setStyle("-fx-font-size: 14px;");

        HBox nomEtType = new HBox(lblNom, lblType);
        nomEtType.setSpacing(5);

        Label lblStock = new Label(stock + " restant");
        lblStock.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

        VBox colonneGauche = new VBox();
        colonneGauche.getChildren().addAll(nomEtType, lblStock);
        colonneGauche.setSpacing(4);

        Button btnModifier = new Button("Modifier");
        btnModifier.setOnAction(e -> ouvrirFenetreModification(id, nom, type, stock));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ligne.getChildren().addAll(colonneGauche, spacer, btnModifier);
        vboxx.getChildren().add(ligne);
    }



    // Méthode pour ouvrir la fenêtre de modification
    private void ouvrirFenetreModification(int id, String nom, String type, int stock) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("Stock/modifierProduit.fxml"));
            Parent root = loader.load();

            ModifierProduitController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.setProduit(id, nom, type, stock);

            stage.setTitle("Modifier Produit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Recharger la liste après modification
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void nouveauProduit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("Stock/ajoutProduit.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer la fenêtre
            AjoutProduitController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setTitle("Ajouter un Produit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            vboxx.getChildren().clear();
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
