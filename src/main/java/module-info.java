module com.abendclyde.triviagame {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.net.http;
    requires org.apache.commons.text;
    requires java.sql;

    opens com.abendclyde.triviagame to javafx.fxml;
    exports com.abendclyde.triviagame;
    exports com.abendclyde.triviagame.game;
    exports com.abendclyde.triviagame.database;
}