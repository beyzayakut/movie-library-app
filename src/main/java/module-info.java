module com.beyzayakut.filmkutuphanesiuygulamasi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.beyzayakut.filmkutuphanesiuygulamasi to javafx.fxml;
    exports com.beyzayakut.filmkutuphanesiuygulamasi;
}