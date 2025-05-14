package org.example.gestionstockage1.controllers.Chantiers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.gestionstockage1.ConnexionBDD;
import org.example.gestionstockage1.controllers.SessionUtilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierInfosChantierController {

    @FXML
    private TextField fieldTitre;

    @FXML
    private TextField fieldAdresse;


    private Stage stage;

    private final int idUtilisateurActuel = SessionUtilisateur.getIdUtilisateur();
    private int id;
    private String titre;
    private String adresse;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInfos(int id, String titre, String adresse) {
        this.id = id;
        this.titre = titre;
        this.adresse = adresse;

        fieldTitre.setText(titre);
        fieldAdresse.setText(adresse);
    }

    @FXML
    private void annuler() {
        stage.close();
    }

    @FXML
    private void modifierChantier() {
        String titre = fieldTitre.getText();
        String adresse = fieldAdresse.getText();

        if (titre.isEmpty() || adresse.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Tous les champs doivent être remplis !");
            alert.showAndWait();
            return;
        }

        try {
            Connection c = ConnexionBDD.initialiserConnexion();
            if (c != null) {
                String query = "UPDATE chantier SET titreChantier = ?, adresseChantier = ? WHERE idChantier = ?";
                PreparedStatement ps = c.prepareStatement(query);
                ps.setString(1, titre);
                ps.setString(2, adresse);
                ps.setInt(3, id);
                ps.executeUpdate();


                ps.close();
                c.close();

                stage.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
