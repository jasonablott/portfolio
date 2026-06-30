package com.example.synthesizer2;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class VolumeControlWidget extends AudioComponentWidgetBase {
    VolumeControlWidget(AudioComponent ac, AnchorPane parentIn, String nameIn) {

        super(ac, parentIn, nameIn);

        VBox leftSide = new VBox();
        Label title = new Label(name);
        Slider slider = new Slider();
        slider.setMin(0.1);
        slider.setMax(2.0);
        slider.setValue(1.0);

        slider.setValue(((VolumeAdjuster) audioComponent).scale);
        //.setOnMouseDragOver(e -> handleSlider(slider)) doesn't work
        slider.valueChangingProperty().addListener(e -> handleSlider(slider));
        // add title and slider to the left side VBox
        input = new Circle(10);
        input.setFill(Color.BLACK);
        output = new Circle(10);
        output.setFill(Color.GRAY);
        leftSide.getChildren().add(input);
        leftSide.getChildren().add(output);
        leftSide.getChildren().add(title);
        leftSide.getChildren().add(slider);

        // add left and right side to baseLayout
        baseLayout.getChildren().add(leftSide);
    }

    // this method checks for when the slider is released after being clicked, and then updates the scale of the
    // VolumeAdjuster audioComponent
    private void handleSlider(Slider slider) {
        if (!slider.isValueChanging()) {
            ((VolumeAdjuster)audioComponent).scale = (slider.getValue());
        }
    }
}
