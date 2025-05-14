package org.example.gestionstockage1.controllers.Profil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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

public class ProfilAdminController {

    @FXML
    private VBox utilisateurListVBox;

    @FXML
    private TextField nouveauPseudoField;

    @FXML
    private PasswordField ancienMdpField;

    @FXML
    private PasswordField nouveauMdpField;

    @FXML
    private PasswordField confirmerMdpField;

    @FXML
    private TextField creationPseudoField;

    @FXML
    private PasswordField creationMdpField;

    public void initialize() {
        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        utilisateurListVBox.getChildren().clear();

        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                String query = "SELECT * FROM utilisateur";
                PreparedStatement ps = c.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("idUtilisateur");
                    String pseudo = rs.getString("pseudoUtilisateur");
                    String role = rs.getString("role"); // Supposons que tu as un champ 'role' dans ta table (ex: "utilisateur" ou "admin")

                    creerCarteUtilisateur(id, pseudo, role);
                }

                ps.close();
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void creerCarteUtilisateur(int id, String pseudo, String role) {
        HBox ligne = new HBox();
        ligne.setSpacing(20);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle(
                "-fx-background-color: #f5f5f5;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);" +
                        "-fx-margin-bottom: 10;"
        );

        // Bouton crayon
        Button btnEditer = new Button("Editer");
        btnEditer.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: lightgray; -fx-border-radius: 10");

        // Label du pseudo
        Label lblPseudo = new Label(pseudo);
        lblPseudo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // TextField pour l'édition du pseudo (caché par défaut)
        TextField txtPseudo = new TextField(pseudo);
        txtPseudo.setVisible(false);

        // Action du bouton crayon
        btnEditer.setOnAction(e -> {
            btnEditer.setText("Appuyez sur Entrée pour valider");
            lblPseudo.setVisible(false);
            txtPseudo.setVisible(true);
            txtPseudo.requestFocus();
        });

        // Quand on appuie "Entrée" ou sort du TextField, on sauvegarde
        txtPseudo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sauvegarderPseudo(id, txtPseudo.getText());
                chargerUtilisateurs(); // Recharge pour rafraîchir
            }
        });

        // Bouton pour changer entre "Utilisateur" et "Admin"
        Button btnRole = new Button(role);
        btnRole.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnRole.setOnAction(e -> {
            String roleUtilisateur = btnRole.getText();
            String nouveauRole;
            if (roleUtilisateur.equalsIgnoreCase("admin")) {
                nouveauRole = "user";
            } else {
                nouveauRole = "admin";
            }
            changerRole(id, nouveauRole);
            chargerUtilisateurs();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnSupprimer.setOnAction(e -> supprimerUtilisateur(id));

        ligne.getChildren().addAll(btnEditer, lblPseudo, txtPseudo, spacer, btnRole, btnSupprimer);
        utilisateurListVBox.getChildren().add(ligne);
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

    private void changerRole(int idUtilisateur, String nouveauRole) {
        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                String updateQuery = "UPDATE utilisateur SET role = ? WHERE idUtilisateur = ?";
                PreparedStatement ps = c.prepareStatement(updateQuery);
                ps.setString(1, nouveauRole.toLowerCase());
                ps.setInt(2, idUtilisateur);
                ps.executeUpdate();

                ps.close();
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void supprimerUtilisateur(int idUtilisateur) {
        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                String deleteQuery = "DELETE FROM utilisateur WHERE idUtilisateur = ?";
                PreparedStatement ps = c.prepareStatement(deleteQuery);
                ps.setInt(1, idUtilisateur);
                ps.executeUpdate();

                ps.close();
                c.close();

                chargerUtilisateurs();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

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


    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        chargerUtilisateurs();
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

    public void annulerModification(ActionEvent actionEvent) {
        nouveauPseudoField.clear();
        ancienMdpField.clear();
        nouveauMdpField.clear();
        confirmerMdpField.clear();
    }

    public void creerUtilisateur(ActionEvent actionEvent) {
        if (!creationPseudoField.getText().isEmpty() && !creationMdpField.getText().isEmpty()) {
            Connection c = ConnexionBDD.initialiserConnexion();
            if (c != null) {
                try {

                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hashBytes = digest.digest(creationPseudoField.getText().getBytes());
                    StringBuilder sb = new StringBuilder();

                    for (byte b : hashBytes) {
                        sb.append(String.format("%02x", b));
                    }


                    String updateQuery = "INSERT INTO utilisateur(pseudoUtilisateur, mdpUtilisateur, role) VALUES (?, ?, 'user')";
                    PreparedStatement ps = c.prepareStatement(updateQuery);
                    ps.setString(1, creationPseudoField.getText());
                    ps.setString(2, sb.toString());
                    ps.executeUpdate();

                    ps.close();
                    c.close();

                    chargerUtilisateurs();
                    creationPseudoField.clear();
                    creationMdpField.clear();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Création du compte");
                    alert.setHeaderText(null);
                    alert.setContentText("la création a abouti");
                    alert.showAndWait();

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void annulerCreation(ActionEvent actionEvent) {
        creationPseudoField.clear();
        creationMdpField.clear();
    }
}
