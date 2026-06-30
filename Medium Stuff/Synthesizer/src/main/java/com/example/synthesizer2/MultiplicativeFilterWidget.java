package com.example.synthesizer2;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MultiplicativeFilterWidget extends AudioComponentWidgetBase {

    MultiplicativeFilterWidget(AudioComponent ac, AnchorPane parentIn, String nameIn) {

        super(ac, parentIn, nameIn);

        VBox leftSide = new VBox();
        Label title = new Label(nameIn);

        // add title and slider to the left side VBox
        input = new Circle(10);
        input.setFill(Color.BLACK);
        output = new Circle(10);
        output.setFill(Color.GRAY);
        leftSide.getChildren().add(input);
        leftSide.getChildren().add(output);

        // add left and right side to baseLayout
        baseLayout.getChildren().add(title);
        baseLayout.getChildren().add(leftSide);
    }
}
