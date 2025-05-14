package org.example.gestionstockage1.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Materiel {
    private SimpleStringProperty nom;
    private SimpleStringProperty type;
    private SimpleIntegerProperty stock;

    public Materiel(String nom, String type, int stock) {
        this.nom = new SimpleStringProperty(nom);
        this.type = new SimpleStringProperty(type);
        this.stock = new SimpleIntegerProperty(stock);
    }

    // Getters et setters nécessaires pour le binding
    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }

    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }

    public int getStock() { return stock.get(); }
    public void setStock(int stock) { this.stock.set(stock); }
}
