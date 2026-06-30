package com.example.synthesizer2;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SquareWidget extends AudioComponentWidgetBase {
    SquareWidget(AudioComponent ac, AnchorPane parentIn, String nameIn) {

        super(ac, parentIn, nameIn);

        VBox leftSide = new VBox();
        Label title = new Label(name);
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(1000);
        slider.setValue(((SquareWave) audioComponent).frequency);
        //.setOnMouseDragOver(e -> handleSlider(slider)) doesn't work
        slider.valueChangingProperty().addListener(e -> handleSlider(slider));
        // add title and slider to the left side VBox
        leftSide.getChildren().add(title);
        leftSide.getChildren().add(slider);
        output = new Circle(10);
        output.setFill(Color.GRAY);
        leftSide.getChildren().add(output);

        // add left and right side to baseLayout
        baseLayout.getChildren().add(leftSide);
    }

    // this method checks for when the slider is released after being clicked, and then updates the frequency of the
    // sineWave audioComponent
    private void handleSlider(Slider slider) {
        if (!slider.isValueChanging()) {
            ((SquareWave)audioComponent).frequency = (int)slider.getValue();
//            int newFrequency = (int) slider.getValue();
//            audioComponent = new SquareWave(newFrequency);
        }
    }
}
