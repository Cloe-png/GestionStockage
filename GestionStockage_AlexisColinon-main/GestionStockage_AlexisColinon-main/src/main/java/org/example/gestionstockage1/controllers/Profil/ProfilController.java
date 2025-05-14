package org.example.gestionstockage1.controllers.Profil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.gestionstockage1.App;
import org.example.gestionstockage1.ConnexionBDD;
import org.example.gestionstockage1.controllers.SessionUtilisateur;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilController {

    @FXML
    private TextField nouveauPseudoField;

    @FXML
    private PasswordField ancienMdpField;

    @FXML
    private PasswordField nouveauMdpField;

    @FXML
    private PasswordField confirmerMdpField;

    public void allerPageProfil(ActionEvent actionEvent) throws IOException {
        if (SessionUtilisateur.getRole().equals("admin")) {
            App.changerPage("Profil", "Profil/profilAdmin.fxml");
        } else {
            App.changerPage("Profil", "Profil/profil.fxml");
        }
    }

    public void allerPageStock(ActionEvent event) throws IOException {
        App.changerPage("Stock", "Stock/stock.fxml");
    }

    public void allerPageChantiers(ActionEvent event) throws IOException {
        App.changerPage("Chantiers", "Chantiers/chantiers.fxml");
    }

    public void allerPageHistorique(ActionEvent event) throws IOException {
        App.changerPage("Historique", "Historique/historique.fxml");
    }

    public void allerPageDeconnexion(ActionEvent event) throws IOException {
        App.changerPage("Connexion", "Connexion/connexion.fxml");
    }


    public void modifierProfil(ActionEvent actionEvent) {

        String ancienMdp = ancienMdpField.getText();

        if (ancienMdp.isEmpty()) {
            showAlert("Erreur", "Veuillez rentrer le mot de passe actuel.");
            return;
        }
        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                String query = "SELECT mdpUtilisateur FROM utilisateur WHERE idUtilisateur = ?";
                try (PreparedStatement stmt = c.prepareStatement(query)) {
                    stmt.setInt(1, SessionUtilisateur.getIdUtilisateur());

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String hashedMdpStocke = rs.getString("mdpUtilisateur");

                        if (hashedMdpStocke.equals(hashPassword(ancienMdp))) {
                            if (!nouveauPseudoField.getText().isEmpty()) {
                                sauvegarderPseudo(SessionUtilisateur.getIdUtilisateur(), nouveauPseudoField.getText());
                                showAlert("Modification", "Pseudo modifié avec succès");
                            }
                            if (!nouveauMdpField.getText().isEmpty() && nouveauMdpField.getText().equals(confirmerMdpField.getText())) {
                                if (c != null) {
                                    try {
                                        String updateQuery = "UPDATE utilisateur SET mdpUtilisateur = ? WHERE idUtilisateur = ?";
                                        PreparedStatement ps = c.prepareStatement(updateQuery);
                                        ps.setString(1, hashPassword(nouveauMdpField.getText().trim()));
                                        ps.setInt(2, SessionUtilisateur.getIdUtilisateur());
                                        ps.executeUpdate();

                                        ps.close();
                                        c.close();

                                        showAlert("Modification", "Mot de passe modifié avec succès");

                                        ancienMdpField.clear();
                                        nouveauMdpField.clear();
                                        confirmerMdpField.clear();

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        showAlert("Erreur", "Une erreur est survenue lors de l'insertion du nouveau mot de passe");
                                    }
                                }
                            } else if (!nouveauMdpField.getText().isEmpty()){
                                showAlert("Erreur", "Les mot de passes ne correspondent pas");
                            }
                        } else {
                            showAlert("Erreur", "Mot de passe actuel incorrect");
                        }
                    }
                }

            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
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

    private void sauvegarderPseudo(int idUtilisateur, String nouveauPseudo) {
        if (nouveauPseudo == null || nouveauPseudo.trim().isEmpty()) return;

        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                String updateQuery = "UPDATE utilisateur SET pseudoUtilisateur = ? WHERE idUtilisateur = ?";
                PreparedStatement ps = c.prepareStatement(updateQuery);
                ps.setString(1, nouveauPseudo.trim());
                ps.setInt(2, idUtilisateur);
                ps.executeUpdate();

                ps.close();
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
