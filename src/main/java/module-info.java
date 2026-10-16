module application {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires transitive javafx.base;
    
    opens application to javafx.fxml, javafx.base;
    exports application;
}
