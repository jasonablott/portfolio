package com.example.synthesizer2;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class VFSineWaveWidget extends AudioComponentWidgetBase {

    VFSineWaveWidget(AudioComponent ac, AnchorPane parentIn, String nameIn) {

        super(ac, parentIn, nameIn);

        VBox leftSide = new VBox();
        Label title = new Label(nameIn);

        input = new Circle(10);
        input.setFill(Color.BLACK);
        output = new Circle(10);
        output.setFill(Color.GRAY);
        leftSide.getChildren().add(input);
        leftSide.getChildren().add(output);
        leftSide.getChildren().add(title);

        // add left and right side to baseLayout
        baseLayout.getChildren().add(leftSide);
    }
}
