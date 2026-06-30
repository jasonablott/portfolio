module com.example.synthesizer2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.synthesizer2 to javafx.fxml;
    exports com.example.synthesizer2;
}