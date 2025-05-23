module com.nilay.finalprojectcalculator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.nilay.finalprojectcalculator to javafx.fxml;
    exports com.nilay.finalprojectcalculator;
}