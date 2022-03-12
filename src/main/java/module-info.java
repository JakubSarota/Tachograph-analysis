module com.example.tachographanalysis {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
//    requires org.bytedeco.opencv;
    requires opencv;

    opens com.example.tachographanalysis to javafx.fxml;
    exports com.example.tachographanalysis;
}