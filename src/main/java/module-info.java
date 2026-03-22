module galleryRouteFinder {
    requires javafx.controls;
    requires javafx.fxml;
    opens galleryRouteFinder.main to javafx.fxml;
    exports galleryRouteFinder.main;
    exports galleryRouteFinder.structs;
}