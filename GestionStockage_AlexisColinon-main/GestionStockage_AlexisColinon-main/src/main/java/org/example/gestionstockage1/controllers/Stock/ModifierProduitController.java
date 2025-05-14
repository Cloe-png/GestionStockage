package org.example.gestionstockage1.controllers.Stock;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.gestionstockage1.ConnexionBDD;
import org.example.gestionstockage1.controllers.SessionUtilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierProduitController {

    @FXML
    private TextField fieldNom;

    @FXML
    private TextField fieldType;

    @FXML
    private TextField fieldStock;

    private Stage stage;
    private int id;
    private int stockActuel;
    private int stockInitial;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProduit(int id, String nom, String type, int stock) {
        this.id = id;
        this.stockActuel = stock;
        this.stockInitial = stock;
        fieldNom.setText(nom);
        fieldType.setText(type);
        fieldStock.setText(String.valueOf(stock));
    }

    @FXML
    private void annuler() {
        stage.close();
    }

    @FXML
    private void modifierProduit() {
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
                String query = "UPDATE materiel SET nomMateriel = ?, typeMateriel = ?, stockMateriel = ? WHERE idMateriel = ?";
                PreparedStatement ps = c.prepareStatement(query);
                ps.setString(1, nom);
                ps.setString(2, type);
                ps.setInt(3, stock);
                ps.setInt(4, id);
                ps.executeUpdate();

                ps.close();

                if(stockInitial < stock) {
                    String query2 = "INSERT INTO historique (idUtilisateur, idMateriel, idChantier, action, quantite, dateAction) VALUES (?, ?, NULL, 'AJOUT_STOCK', ?, CURRENT_TIMESTAMP)";
                    PreparedStatement ps2 = c.prepareStatement(query2);
                    ps2.setInt(1, SessionUtilisateur.getIdUtilisateur());
                    ps2.setInt(2, id);
                    ps2.setInt(3, stock - stockInitial);
                    ps2.executeUpdate();
                    ps2.close();
                } else if(stockInitial > stock) {
                    String query3 = "INSERT INTO historique (idUtilisateur, idMateriel, idChantier, action, quantite, dateAction) VALUES (?, ?, NULL, 'SUPPRESSION_STOCK', ?, CURRENT_TIMESTAMP)";
                    PreparedStatement ps3 = c.prepareStatement(query3);
                    ps3.setInt(1, SessionUtilisateur.getIdUtilisateur());
                    ps3.setInt(2, id);
                    ps3.setInt(3, stockInitial - stock);
                    ps3.executeUpdate();
                    ps3.close();
                }


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

    @FXML
    private void ajouter1() {
        changerStock(Integer.parseInt(fieldStock.getText()) + 1);
    }

    @FXML
    private void ajouter10() {
        changerStock(Integer.parseInt(fieldStock.getText()) + 10);
    }

    @FXML
    private void reinitialiser0() {
        changerStock(0);
    }

    @FXML
    private void enlever1() {
        changerStock(Integer.parseInt(fieldStock.getText()) - 1);
    }

    @FXML
    private void enlever10() {
        changerStock(Integer.parseInt(fieldStock.getText()) - 10);
    }

    private void changerStock(int valeur) {
        stockActuel = valeur;
        fieldStock.setText(String.valueOf(stockActuel));
    }

    @FXML
    public void changerValeurStock(ActionEvent event) {
        String stockText = fieldStock.getText();
        if (!stockText.isEmpty()) {
            try {
                int stock = Integer.parseInt(stockText);
                changerStock(stock);
            } catch (NumberFormatException e) {
                fieldStock.setText(String.valueOf(stockActuel)); // Remet l'ancienne valeur si erreur
            }
        }
    }


    public void supprimerProduit(ActionEvent actionEvent) throws SQLException {
        Connection c = ConnexionBDD.initialiserConnexion();
        if (c != null) {
            String query = "DELETE FROM materiel WHERE idMateriel = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();

            ps.close();
            c.close();
        }
        stage.close();
    }
}