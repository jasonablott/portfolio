package com.example.synthesizer2;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class LinearRampWidget extends AudioComponentWidgetBase {

    LinearRampWidget(AudioComponent ac, AnchorPane parentIn, String nameIn) {

        super(ac, parentIn, nameIn);

        VBox leftSide = new VBox();
        Label title = new Label(nameIn);

        Slider slider1 = new Slider();
        slider1.setMin(0);
        slider1.setMax(1000);
        slider1.setValue(((LinearRamp)audioComponent).start);
        slider1.valueChangingProperty().addListener(e -> handleSlider1(slider1));

        Slider slider2 = new Slider();
        slider2.setMin(1000);
        slider2.setMax(10000);
        slider2.setValue(((LinearRamp)audioComponent).stop);
        slider2.valueChangingProperty().addListener(e -> handleSlider2(slider2));

        // add title and slider to the left side VBox
        leftSide.getChildren().add(title);
        leftSide.getChildren().add(slider1);
        leftSide.getChildren().add(slider2);
        // add output to left side
        output = new Circle(10);
        output.setFill(Color.GRAY);
        leftSide.getChildren().add(output);

        // add left and right side to baseLayout
        baseLayout.getChildren().add(leftSide);
    }

    private void handleSlider1(Slider slider) {
        if (!slider.isValueChanging()){
            ((LinearRamp)audioComponent).start = (int)slider.getValue();
        }
    }

    private void handleSlider2(Slider slider) {
        if (!slider.isValueChanging()){
            ((LinearRamp)audioComponent).stop = (int)slider.getValue();
        }
    }
}
