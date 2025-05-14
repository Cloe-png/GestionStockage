package org.example.gestionstockage1.controllers.Chantiers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import org.example.gestionstockage1.controllers.Stock.AjoutProduitController;
import org.example.gestionstockage1.controllers.Stock.ModifierProduitController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChantiersController {

    @FXML
    VBox vbAllChantiers;

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
        rechercherChantier(null);
    }


    public void nouveauChantier(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("chantiers/ajouterChantier.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer la fenêtre
            AjouterChantierController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setTitle("Ajouter un Chantier");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            vbAllChantiers.getChildren().clear();
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rechercherChantier(KeyEvent keyEvent) {
        String recherche = textFieldRecherche.getText().toLowerCase().trim();
        vbAllChantiers.getChildren().clear();

        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                String query = "SELECT idChantier, titreChantier, adresseChantier FROM chantier ORDER BY idChantier DESC";
                PreparedStatement ps = c.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("idChantier");
                    String titre = rs.getString("titreChantier");
                    String adresse = rs.getString("adresseChantier");

                    String contenu = (titre + " " + adresse).toLowerCase();
                    if (contenu.contains(recherche)) {
                        creerLigne(id, titre, adresse);
                    }
                }

                ps.close();
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void creerLigne(int idChantier, String titreChantier, String adresseChantier) throws SQLException {
        Connection c = ConnexionBDD.initialiserConnexion();
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

        VBox vbGlobal = new VBox();
        vbGlobal.setSpacing(10);
        vbGlobal.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0.3, 0, 2);");

        HBox hbTop = new HBox();
        hbTop.setSpacing(10);
        hbTop.setAlignment(Pos.CENTER_LEFT);
        hbTop.setStyle("-fx-border-color: #d3d3d3; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 10 0;");

        VBox vbInfos = new VBox();
        vbInfos.setSpacing(5);

        Label labelTitreChantier = new Label(titreChantier);
        labelTitreChantier.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        Label labelAdresseChantier = new Label(adresseChantier);
        labelAdresseChantier.setStyle("-fx-font-size: 14;");

        vbInfos.getChildren().addAll(labelTitreChantier, labelAdresseChantier);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnModifier = new Button("Modifier");
        btnModifier.setStyle("-fx-cursor: hand;");
        btnModifier.setOnAction(e -> ouvrirFenetreModification(idChantier));

        hbTop.getChildren().addAll(vbInfos, spacer, btnModifier);

        // Partie Matériaux
        VBox vbMateriaux = new VBox();
        vbMateriaux.setSpacing(4);

        String queryMateriaux = "SELECT idMateriel, nombreStockUtilise FROM liaisonChantierMateriel WHERE idChantier = ? ORDER BY nombreStockUtilise DESC";
        PreparedStatement psMateriaux = c.prepareStatement(queryMateriaux);
        psMateriaux.setInt(1, idChantier);
        ResultSet rsMateriaux = psMateriaux.executeQuery();

        while (rsMateriaux.next()) {
            int idMateriel = rsMateriaux.getInt("idMateriel");
            int nbStock = rsMateriaux.getInt("nombreStockUtilise");

            String queryNomMateriel = "SELECT nomMateriel, typeMateriel FROM materiel WHERE idMateriel = ?";
            PreparedStatement psNomMateriel = c.prepareStatement(queryNomMateriel);
            psNomMateriel.setInt(1, idMateriel);
            ResultSet rsNomMateriel = psNomMateriel.executeQuery();
            rsNomMateriel.next();
            String nomMateriel = rsNomMateriel.getString("nomMateriel");
            String typeMateriel = rsNomMateriel.getString("typeMateriel");

            Label labelMateriel = new Label("- " + nomMateriel + " (" + typeMateriel + ") x" + nbStock);
            labelMateriel.setStyle("-fx-font-style: italic;");

            vbMateriaux.getChildren().add(labelMateriel);

            psNomMateriel.close();
        }

        vbGlobal.getChildren().addAll(hbTop, vbMateriaux);
        vbAllChantiers.getChildren().add(vbGlobal);

        psMateriaux.close();
    }

    private void ouvrirFenetreModification(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("Chantiers/modifierChantier.fxml"));
            Parent root = loader.load();

            ModifierChantierController controller = loader.getController();
            controller.setInformations(id);
            Stage stage = new Stage();
            controller.setStage(stage);

            controller.rechercherMateriel(null);

            stage.setTitle("Modifier Chantier");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
