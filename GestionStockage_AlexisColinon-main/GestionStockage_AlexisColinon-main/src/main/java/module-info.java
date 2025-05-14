module org.example.gestionstockage1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens org.example.gestionstockage1 to javafx.fxml;
    exports org.example.gestionstockage1;
    exports org.example.gestionstockage1.controllers;
    opens org.example.gestionstockage1.controllers to javafx.fxml;
    exports org.example.gestionstockage1.models;
    opens org.example.gestionstockage1.models to javafx.fxml;
    exports org.example.gestionstockage1.controllers.Stock;
    opens org.example.gestionstockage1.controllers.Stock to javafx.fxml;
    exports org.example.gestionstockage1.controllers.downdate;
    opens org.example.gestionstockage1.controllers.downdate to javafx.fxml;
    exports org.example.gestionstockage1.controllers.Historique;
    opens org.example.gestionstockage1.controllers.Historique to javafx.fxml;
    exports org.example.gestionstockage1.controllers.Chantiers;
    opens org.example.gestionstockage1.controllers.Chantiers to javafx.fxml;
    exports org.example.gestionstockage1.controllers.Profil;
    opens org.example.gestionstockage1.controllers.Profil to javafx.fxml;
}