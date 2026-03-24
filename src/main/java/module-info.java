module galleryRouteFinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    opens galleryRouteFinder.main to javafx.fxml;
    exports galleryRouteFinder.main;
    exports galleryRouteFinder.structs;
    exports galleryRouteFinder.controllers;
    opens galleryRouteFinder.controllers to javafx.fxml;
}