module me.croshaw.carservicesimulation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;


    opens me.croshaw.carservicesimulation to javafx.fxml;
    exports me.croshaw.carservicesimulation;
    opens me.croshaw.carservicesimulation.simulation to com.google.gson;
    opens me.croshaw.carservicesimulation.simulation.core to com.google.gson;
    opens me.croshaw.carservicesimulation.simulation.core.util to com.google.gson;
    opens me.croshaw.carservicesimulation.simulation.core.service to com.google.gson;
    opens me.croshaw.carservicesimulation.drawers to com.google.gson;
}