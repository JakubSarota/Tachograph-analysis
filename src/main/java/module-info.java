module com.example.tachographanalysis {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires opencv;
    requires java.desktop;
    requires javafx.swing;
    requires json;
    requires itextpdf;

    opens com.example.tachographanalysis to javafx.fxml;
    exports com.example.tachographanalysis;
    exports com.example.tachographanalysis.database;
    opens com.example.tachographanalysis.database to javafx.fxml;
    exports com.example.tachographanalysis.database.driver;
    opens com.example.tachographanalysis.database.driver to javafx.fxml;
    exports com.example.tachographanalysis.database.driver.driverInfo;
    opens com.example.tachographanalysis.database.driver.driverInfo to javafx.fxml;

}