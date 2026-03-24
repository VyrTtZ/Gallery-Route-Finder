module galleryRouteFinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    opens galleryRouteFinder.main to javafx.fxml;
    exports galleryRouteFinder.main;
    exports galleryRouteFinder.structs;
}