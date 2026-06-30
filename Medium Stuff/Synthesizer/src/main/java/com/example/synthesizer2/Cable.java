package com.example.synthesizer2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;

public class Cable extends Pane {

    Line line;
    Pane pane;

    AudioComponentWidgetBase input;
    AudioComponentWidgetBase output;
    SpeakerWidget speaker;

    Cable(double startX, double startY, double endX, double endY) {
        line = new Line(startX, startY, endX, endY);
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);
        line.setOnMouseClicked(this::removeCable);
    }

    public void setSpeaker(SpeakerWidget speaker) {
        this.speaker = speaker;
    }

    public void setInput(AudioComponentWidgetBase input) {
        this.input = input;
    }

    public void setOutput(AudioComponentWidgetBase output) {
        this.output = output;
    }

    public void removeCable (MouseEvent mouseEvent) {
        // needs to remove cable and disconnect inputs appropriately
        if (this.output != null) {
            this.output.getOutput().removeInput(this.input.getOutput());
        }
        // check if cable is connected to speaker
        if (this.speaker != null){
            // if so, remove speaker input
            SynthesizeApplication.speaker.connectInput(null);
        }
        SynthesizeApplication.mainCanvas.getChildren().remove(this.line);
        SynthesizeApplication.allCables.remove(this);
    }
    public void updateCableStart(double startX, double startY) {
        line.setStartX(startX);
        line.setStartY(startY);
    }
    public void updateCableEnd(double endX, double endY) {
        line.setEndX(endX);
        line.setEndY(endY);
    }


}
