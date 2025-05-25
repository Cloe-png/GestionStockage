package org.example.gestionstockage1.controllers.Chantiers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.gestionstockage1.App;
import org.example.gestionstockage1.ConnexionBDD;
import org.example.gestionstockage1.controllers.SessionUtilisateur;
import org.example.gestionstockage1.models.Materiel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifierChantierController {

    @FXML
    private VBox vboxStock;

    @FXML
    private VBox vboxSelectionnes;

    @FXML
    private TextField textFieldRecherche;

    @FXML
    private Button btnEnregistrerModifications;

    private Map<Integer, Integer> modifications = new HashMap<>();

    private Connection c = ConnexionBDD.initialiserConnexion();

    private Stage stage;
    private int id;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInformations(int id) {
        this.id = id;
    }

    @FXML
    public void rechercherMateriel(KeyEvent keyEvent) {
        rechercherStock();
        rechercherSelectionnes();
    }

    public void rechercherStock() {
        String recherche = textFieldRecherche.getText().toLowerCase().trim();
        vboxStock.getChildren().clear();

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
                        creerLigneStock(id, nom, type, stock);
                    }
                }

                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void rechercherSelectionnes() {
        String recherche = textFieldRecherche.getText().toLowerCase().trim();
        vboxSelectionnes.getChildren().clear();

        if (c != null) {
            try {
                String query1 = "SELECT materiel.idMateriel, materiel.nomMateriel, materiel.typeMateriel, liaisonchantiermateriel.nombreStockUtilise FROM materiel INNER JOIN liaisonchantiermateriel ON liaisonchantiermateriel.idMateriel = materiel.idMateriel INNER JOIN chantier ON chantier.idChantier = liaisonchantiermateriel.idChantier WHERE chantier.idChantier = ?";
                PreparedStatement ps = c.prepareStatement(query1);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("idMateriel");
                    String nom = rs.getString("nomMateriel");
                    String type = rs.getString("typeMateriel");
                    int stock = rs.getInt("nombreStockUtilise");

                    String contenu = (nom + " " + type).toLowerCase();
                    if (contenu.contains(recherche)) {
                        creerLigneChantier(id, nom, type, stock);
                    }
                }

                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private HBox trouverLigneMateriel(int idMateriel) {
        for (Node node : vboxSelectionnes.getChildren()) {
            if (node instanceof HBox) {
                HBox ligne = (HBox) node;
                if (ligne.getUserData() != null && ligne.getUserData().equals(idMateriel)) {
                    return ligne;
                }
            }
        }
        return null;
    }

    private void creerLigneStock(int idMateriel, String nom, String type, int stock) {
        HBox ligne = new HBox();
        ligne.setSpacing(20);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");

        Label lblNom = new Label(nom);
        lblNom.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblType = new Label(" (" + type + ")");
        lblType.setStyle("-fx-font-size: 14px;");

        Label lblStock = new Label(stock + " restant");
        lblStock.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

        VBox colonneGauche = new VBox(new HBox(lblNom, lblType), lblStock);
        colonneGauche.setSpacing(4);

        Button btnUtiliser = new Button("Utiliser pour ce chantier");
        btnUtiliser.setOnAction(e -> utiliserMaterielPourChantier(idMateriel, nom, type));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ligne.getChildren().addAll(colonneGauche, spacer, btnUtiliser);
        vboxStock.getChildren().add(ligne);
    }

    private void utiliserMaterielPourChantier(int idMateriel, String nom, String type) {
        HBox ligneExistante = trouverLigneMateriel(idMateriel);

        if (ligneExistante != null) {
            TextField quantiteField = (TextField) ((HBox) ligneExistante.getChildren().get(2)).getChildren().get(1);
            int ancienneQuantite = Integer.parseInt(quantiteField.getText());
            quantiteField.setText(String.valueOf(ancienneQuantite + 1));
            mettreAJourQuantite(idMateriel, ancienneQuantite + 1);
        } else {
            creerLigneChantier(idMateriel, nom, type, 1);
            insererMaterielChantier(idMateriel, 1);
        }

        modifications.put(idMateriel, modifications.getOrDefault(idMateriel, 0) + 1);
        retirerDuStock(idMateriel, 1);
        rechercherStock();
    }

    private void creerLigneChantier(int idMateriel, String nom, String type, int quantite) {
        HBox ligne = new HBox();
        ligne.setSpacing(20);
        ligne.setUserData(idMateriel);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");

        Label lblNom = new Label(nom);
        lblNom.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblType = new Label(" (" + type + ")");
        lblType.setStyle("-fx-font-size: 14px;");

        VBox colonneGauche = new VBox(new HBox(lblNom, lblType));
        colonneGauche.setSpacing(4);

        TextField textFieldQuantite = new TextField(String.valueOf(quantite));
        textFieldQuantite.setPrefWidth(50);
        textFieldQuantite.setAlignment(Pos.CENTER);
        textFieldQuantite.setEditable(false);

        Button btnPlus = new Button("+");
        btnPlus.setOnAction(e -> {
            int qte = Integer.parseInt(textFieldQuantite.getText());
            mettreAJourQuantite(idMateriel, qte + 1);
            retirerDuStock(idMateriel, 1);
            textFieldQuantite.setText(String.valueOf(qte + 1));
            modifications.put(idMateriel, modifications.getOrDefault(idMateriel, 0) + 1);
            rechercherStock();
        });

        Button btnMoins = new Button("-");
        btnMoins.setOnAction(e -> {
            int qte = Integer.parseInt(textFieldQuantite.getText());
            if (qte > 1) {
                mettreAJourQuantite(idMateriel, qte - 1);
                remettreAuStock(idMateriel, 1);
                textFieldQuantite.setText(String.valueOf(qte - 1));
                rechercherStock();
            } else {
                supprimerMaterielChantier(idMateriel);
                remettreAuStock(idMateriel, 1);
                vboxSelectionnes.getChildren().remove(ligne);
                rechercherStock();
            }
            modifications.put(idMateriel, modifications.getOrDefault(idMateriel, 0) - 1);
        });

        HBox quantiteBox = new HBox(btnMoins, textFieldQuantite, btnPlus);
        quantiteBox.setSpacing(5);
        quantiteBox.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ligne.getChildren().addAll(colonneGauche, spacer, quantiteBox);
        vboxSelectionnes.getChildren().add(ligne);
    }

    private void mettreAJourQuantite(int idMateriel, int nouvelleQuantite) {
        if (c != null) {
            try {
                String update = "UPDATE liaisonchantiermateriel SET nombreStockUtilise = ? WHERE idChantier = ? AND idMateriel = ?";
                PreparedStatement ps = c.prepareStatement(update);
                ps.setInt(1, nouvelleQuantite);
                ps.setInt(2, id);
                ps.setInt(3, idMateriel);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void insererMaterielChantier(int idMateriel, int quantite) {
        if (c != null) {
            try {
                String insert = "INSERT INTO liaisonchantiermateriel (idChantier, idMateriel, nombreStockUtilise) VALUES (?, ?, ?)";
                PreparedStatement ps = c.prepareStatement(insert);
                ps.setInt(1, id);
                ps.setInt(2, idMateriel);
                ps.setInt(3, quantite);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void supprimerMaterielChantier(int idMateriel) {
        if (c != null) {
            try {
                String delete = "DELETE FROM liaisonchantiermateriel WHERE idChantier = ? AND idMateriel = ?";
                PreparedStatement ps = c.prepareStatement(delete);
                ps.setInt(1, id);
                ps.setInt(2, idMateriel);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void retirerDuStock(int idMateriel, int quantite) {
        if (c != null) {
            try {
                String update = "UPDATE materiel SET stockMateriel = stockMateriel - ? WHERE idMateriel = ?";
                PreparedStatement ps = c.prepareStatement(update);
                ps.setInt(1, quantite);
                ps.setInt(2, idMateriel);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void remettreAuStock(int idMateriel, int quantite) {
        if (c != null) {
            try {
                String update = "UPDATE materiel SET stockMateriel = stockMateriel + ? WHERE idMateriel = ?";
                PreparedStatement ps = c.prepareStatement(update);
                ps.setInt(1, quantite);
                ps.setInt(2, idMateriel);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void enregistrerModifications(ActionEvent event) {
        try {
            for (Map.Entry<Integer, Integer> entry : modifications.entrySet()) {
                int idMateriel = entry.getKey();
                int quantite = entry.getValue();

                if (quantite != 0) {
                    String action = "RETRAIT_CHANTIER";
                    if (quantite > 0) action = "AJOUT_CHANTIER";
                    int quantiteAbsolue = Math.abs(quantite);

                    String query = "INSERT INTO historique (idUtilisateur, idChantier, idMateriel, action, quantite, dateAction) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP())";
                    PreparedStatement ps = c.prepareStatement(query);
                    ps.setInt(1, SessionUtilisateur.getIdUtilisateur());
                    ps.setInt(2, id);
                    ps.setInt(3, idMateriel);
                    ps.setString(4, action);
                    ps.setInt(5, quantiteAbsolue);
                    ps.executeUpdate();
                    ps.close();
                }
            }

            // Fermer la fenêtre après avoir enregistré
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void annuler(ActionEvent actionEvent) {
        stage.close();
    }

    public void modifierInfos(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("chantiers/infosChantier.fxml"));
            Parent root = loader.load();

            Connection c = ConnexionBDD.initialiserConnexion();
            if (c != null) {
                String query = "SELECT titreChantier, adresseChantier FROM chantier WHERE idChantier = ?";
                PreparedStatement ps = c.prepareStatement(query);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                String titre = rs.getString("titreChantier");
                String adresse = rs.getString("adresseChantier");
                ps.close();
                c.close();

                // Récupérer le contrôleur et lui passer la fenêtre
                ModifierInfosChantierController controller = loader.getController();
                Stage stage = new Stage();
                controller.setStage(stage);
                controller.setInfos(id, titre, adresse);

                stage.setTitle("Modifier les infos");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void supprimerChantier(ActionEvent event) {
        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            try {
                // Supprimer les liaisons entre le chantier et les matériaux
                String deleteLiaisons = "DELETE FROM liaisonchantiermateriel WHERE idChantier = ?";
                PreparedStatement psLiaisons = c.prepareStatement(deleteLiaisons);
                psLiaisons.setInt(1, id);
                psLiaisons.executeUpdate();
                psLiaisons.close();

                // Supprimer le chantier
                String deleteChantier = "DELETE FROM chantier WHERE idChantier = ?";
                PreparedStatement psChantier = c.prepareStatement(deleteChantier);
                psChantier.setInt(1, id);
                psChantier.executeUpdate();
                psChantier.close();

                // Afficher un message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Le chantier a été supprimé avec succès.");
                alert.showAndWait();

                // Fermer la fenêtre après suppression
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Afficher un message d'erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de la suppression du chantier.");
                alert.showAndWait();
            }
        }
    }
}
