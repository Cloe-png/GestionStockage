package org.example.gestionstockage1.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.gestionstockage1.App;
import org.example.gestionstockage1.ConnexionBDD;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class ConnexionController {

    @FXML
    TextField fieldUtilisateur;

    @FXML
    PasswordField fieldMdp;

    @FXML
    Button btnLogin;

    @FXML
    private void cliquerConnecter(ActionEvent actionEvent) throws IOException  {
        String pseudo = fieldUtilisateur.getText();
        String mdp = fieldMdp.getText();

        if (pseudo.isEmpty() || mdp.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }
        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                String query = "SELECT mdpUtilisateur, idUtilisateur, role FROM utilisateur WHERE pseudoUtilisateur = ?";
                try (PreparedStatement stmt =  c.prepareStatement(query)) {
                    stmt.setString(1, pseudo);

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String hashedMdpStocke = rs.getString("mdpUtilisateur");
                        int id = rs.getInt("idUtilisateur");
                        String role = rs.getString("role");

                        if (hashedMdpStocke.equals(hashPassword(mdp))) {
                            SessionUtilisateur.setUtilisateur(id);
                            SessionUtilisateur.setRole(role);
                            App.changerPage("Chantier", "Chantiers/chantiers.fxml");
                        } else {
                            showAlert("Erreur", "Mot de passe incorrect.");
                        }
                    } else {
                        showAlert("Erreur", "Nom d'utilisateur introuvable.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur de connexion à la base de données.");
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hashage", e);
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
