package org.example.gestionstockage1.controllers.Stock;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.gestionstockage1.ConnexionBDD;
import org.example.gestionstockage1.controllers.SessionUtilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AjoutProduitController {

    @FXML
    private TextField fieldNom;

    @FXML
    private TextField fieldType;

    @FXML
    private TextField fieldStock;

    private Stage stage;

    private final int idUtilisateurActuel = SessionUtilisateur.getIdUtilisateur();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void annuler() {
        stage.close();
    }

    @FXML
    private void ajouterProduit() {
        String nom = fieldNom.getText();
        String type = fieldType.getText();
        String stockText = fieldStock.getText();

        if (nom.isEmpty() || type.isEmpty() || stockText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Tous les champs doivent être remplis !");
            alert.showAndWait();
            return;
        }

        try {
            int stock = Integer.parseInt(stockText);

            Connection c = ConnexionBDD.initialiserConnexion();
            if (c != null) {
                String query = "INSERT INTO materiel (nomMateriel, typeMateriel, stockMateriel) VALUES (?, ?, ?)";
                PreparedStatement ps = c.prepareStatement(query);
                ps.setString(1, nom);
                ps.setString(2, type);
                ps.setInt(3, stock);
                ps.executeUpdate();

                String query2 = "SELECT idMateriel FROM materiel WHERE nomMateriel = ? AND typeMateriel = ?";
                PreparedStatement ps2 = c.prepareStatement(query2);
                ps2.setString(1, nom);
                ps2.setString(2, type);
                ResultSet rs = ps2.executeQuery();
                rs.next();
                int idMateriel = rs.getInt("idMateriel");

                String query3 = "INSERT INTO historique (idUtilisateur, idMateriel, idChantier, action, quantite, dateAction) VALUES (?, ?, NULL, 'CREATION_NOUVEAU_MATERIEL', ?, CURRENT_TIMESTAMP)";
                PreparedStatement ps3 = c.prepareStatement(query3);
                ps3.setInt(1, idUtilisateurActuel);
                ps3.setInt(2, idMateriel);
                ps3.setInt(3, stock);
                ps3.executeUpdate();

                ps.close();
                ps2.close();
                c.close();

                stage.close();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Le stock doit être un nombre !");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
