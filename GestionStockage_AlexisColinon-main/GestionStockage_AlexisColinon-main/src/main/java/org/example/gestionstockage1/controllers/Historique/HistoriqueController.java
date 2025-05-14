package org.example.gestionstockage1.controllers.Historique;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.gestionstockage1.App;
import org.example.gestionstockage1.ConnexionBDD;
import org.example.gestionstockage1.controllers.SessionUtilisateur;

import java.io.IOException;
import java.sql.*;

public class HistoriqueController {

    @FXML
    VBox vboxx;

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
        vboxx.getChildren().clear();

        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                String query = "SELECT utilisateur.pseudoUtilisateur AS pseudoUtilisateur, materiel.nomMateriel AS nomMateriel, materiel.typeMateriel AS typeMateriel, chantier.titreChantier AS titreChantier, chantier.adresseChantier AS adresseChantier, action, quantite, dateAction FROM historique INNER JOIN utilisateur ON utilisateur.idUtilisateur = historique.idUtilisateur INNER JOIN materiel ON materiel.idMateriel = historique.idMateriel LEFT JOIN chantier ON chantier.idChantier = historique.idChantier ORDER BY historique.dateAction DESC LIMIT 100";
                PreparedStatement ps = c.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String pseudo = rs.getString("pseudoUtilisateur");
                    String nomMateriel = rs.getString("nomMateriel");
                    String typeMateriel = rs.getString("typeMateriel");
                    String titreChantier = rs.getString("titreChantier");
                    String adresseChantier = rs.getString("adresseChantier");
                    String action = rs.getString("action");
                    int quantite = rs.getInt("quantite");
                    String dateAction = rs.getString("dateAction");

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

// Texte principal
                    Label lblAction = new Label(action);
                    lblAction.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    Label lblNomMateriel = new Label(": " + nomMateriel);
                    lblNomMateriel.setStyle("-fx-font-size: 14px;");

                    Label lblTypeMateriel = new Label(" (" + typeMateriel + ") ");
                    lblTypeMateriel.setStyle("-fx-font-size: 14px;");

                    Label lblQuantite = new Label("x" + quantite + " ");
                    lblQuantite.setStyle("-fx-font-size: 14px;");

                    HBox hboxAction;

                    if(titreChantier == null && adresseChantier == null) {
                        Label lblStock = new Label("au stock");
                        lblStock.setStyle("-fx-font-size: 14px;");

                        hboxAction = new HBox(lblAction, lblNomMateriel, lblTypeMateriel, lblQuantite, lblStock);
                        hboxAction.setSpacing(5);
                    } else {
                        Label lblTitreChantier = new Label("au chantier " + titreChantier + " ");
                        lblTitreChantier.setStyle("-fx-font-size: 14px;");

                        Label lblAdresseChantier = new Label("(" + adresseChantier + ")");
                        lblAdresseChantier.setStyle("-fx-font-size: 14px;");

                        hboxAction = new HBox(lblAction, lblNomMateriel, lblTypeMateriel, lblQuantite, lblTitreChantier, lblAdresseChantier);
                        hboxAction.setSpacing(5);
                    }

// Texte du stock
                    Label lblPseudo = new Label(pseudo);
                    lblPseudo.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

                    Label lblDateAction = new Label(" - " + dateAction);
                    lblDateAction.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

                    HBox pseudoEtDate = new HBox(lblPseudo, lblDateAction);
                    pseudoEtDate.setSpacing(5);

// Colonne gauche (Nom + Stock)
                    VBox boxFinal = new VBox();
                    boxFinal.getChildren().addAll(hboxAction, pseudoEtDate);
                    boxFinal.setSpacing(4);

                    ligne.getChildren().add(boxFinal);
                    vboxx.getChildren().add(ligne);
                }

                ps.close();
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
