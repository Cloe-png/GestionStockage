package org.example.gestionstockage1.controllers;

public class SessionUtilisateur {
    private static int idUtilisateur;
    private static String role;

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        SessionUtilisateur.role = role;
    }

    public static void setUtilisateur(int id) {
        idUtilisateur = id;
    }

    public static int getIdUtilisateur() {
        return idUtilisateur;
    }
}