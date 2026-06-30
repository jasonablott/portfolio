package com.example.synthesizer2;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class WhiteNoiseWidget extends AudioComponentWidgetBase {
    WhiteNoiseWidget(AudioComponent ac, AnchorPane parentIn, String nameIn) {

        super(ac, parentIn, nameIn);

        VBox leftSide = new VBox();
        Label title = new Label(name);
        leftSide.getChildren().add(title);
        output = new Circle(10);
        output.setFill(Color.GRAY);
        leftSide.getChildren().add(output);

        // add left and right side to baseLayout
        baseLayout.getChildren().add(leftSide);
    }
}
