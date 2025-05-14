package org.example.gestionstockage1.controllers.downdate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.example.gestionstockage1.App;
import org.example.gestionstockage1.models.Materiel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MaterielController {

    private int boutonAction = 1;

    public static ObservableList<Materiel> produits;private
    HashMap<Materiel, Integer> panier = new HashMap<>();
    private ObservableList<String> panierList = FXCollections.observableArrayList();

    public static Materiel produitSelectionne;

    @FXML
    private TableView<Materiel> tableMateriel;

    @FXML
    private TableColumn<Materiel, String> colProduit;

    @FXML
    private TableColumn<Materiel, String> colType;

    @FXML
    private TableColumn<Materiel, Integer> colStock;

    @FXML
    ListView listPanier;

    @FXML
    Button btnPanier;

    @FXML
    Button btnModifier;

    @FXML
    Button btnNouveau;

    @FXML
    public void initialize() {
        btnPanier.setDisable(true);

        // Initialiser la liste des produits
        produits = FXCollections.observableArrayList();
        panier = new HashMap<>();

        // Configurer les colonnes avec la classe Materiel
        colProduit.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Lier la liste observable au TableView
        tableMateriel.setItems(produits);
        listPanier.setItems(panierList);
    }

    public void refresh() {
        tableMateriel.refresh();
    }

    public void cliquerMateriel(MouseEvent mouseEvent) throws IOException {
        produitSelectionne = tableMateriel.getSelectionModel().getSelectedItem();
        if (produitSelectionne != null) {
            // Ajouter
            if(boutonAction == 1) {
                produitSelectionne.setStock(produitSelectionne.getStock() - 1);
                panier.put(produitSelectionne, panier.getOrDefault(produitSelectionne, 0) + 1);
                refreshPanier();
                tableMateriel.refresh();
            }
            // Modifier
            if(boutonAction == 2) {
                App.lancerPage("Modifier un Materiel", "modifierProduit.fxml");
                System.out.println(2);
                tableMateriel.refresh();
            }
        } else {
            // Afficher un message d'erreur si aucun produit n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText("Veuillez sélectionner un produit");
            alert.showAndWait();
        }
    }

    private void refreshPanier() {
        panierList.clear();
        for (Map.Entry<Materiel, Integer> entry : panier.entrySet()) {
            panierList.add(entry.getKey().getNom() + " " + entry.getKey().getType() + " x" + entry.getValue());
        }
    }

    public void cliquerPanier(MouseEvent mouseEvent) {
        String produitSelectionne = listPanier.getSelectionModel().getSelectedItem().toString();
        if (produitSelectionne != null) {
            if(boutonAction == 1) {
                supprimerProduitPanier(produitSelectionne);
                refreshPanier();
            }
        } else {
            // Afficher un message d'erreur si aucun produit n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText("Veuillez sélectionner un produit");
            alert.showAndWait();
        }
    }

    private Materiel chercherProduitPanier(String produitSelectionne) {
        String productName = produitSelectionne.split(" x")[0]; // Récupérer le nom du produit
        Materiel produitASupprimer = null;

        // Trouver l'objet `Product` correspondant dans la HashMap
        for (Materiel produit : panier.keySet()) {
            String p = produit.getNom() + " " + produit.getType();
            if (p.equals(productName)) {
                produitASupprimer = produit;
                break;
            }
        }
        return produitASupprimer;
    }

    private void supprimerProduitPanier(String produitASupprimer) {
        Materiel p = chercherProduitPanier(produitASupprimer);
        if (panier.containsKey(p)) {
            int quantite = panier.get(p);
            if (quantite > 1) {
                panier.put(p, quantite - 1);
            } else {
                panier.remove(p);
            }
            produits.get(produits.indexOf(p)).setStock(produits.get(produits.indexOf(p)).getStock() + 1);
            tableMateriel.refresh();
        }
    }

    public void activerBtnPanier(ActionEvent actionEvent) {
        boutonAction = 1;
        btnPanier.setDisable(true);
        btnModifier.setDisable(false);
    }

    public void activerBtnModifier(ActionEvent actionEvent) {
        boutonAction = 2;
        btnPanier.setDisable(false);
        btnModifier.setDisable(true);
    }

    public void ajouterNouveauProduit(ActionEvent actionEvent) throws IOException {
        App.lancerPage("Ajouter un produit", "nouveauProduit.fxml");
    }

    public void selectionner(ActionEvent actionEvent) {
        panierList.clear();
        panier.clear();
    }
}