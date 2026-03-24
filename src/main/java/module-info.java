module galleryRouteFinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jmh.core;

    exports galleryRouteFinder.main;
    exports galleryRouteFinder.structs;
    exports galleryRouteFinder.controllers;
    exports galleryRouteFinder.main.jmh_generated;

    opens galleryRouteFinder.main.jmh_generated to jmh.core;
    opens galleryRouteFinder.main to javafx.fxml, jmh.core;
    opens galleryRouteFinder.controllers to javafx.fxml;
}