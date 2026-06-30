package com.example.synthesizer2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class SpeakerWidget extends Pane {
    // AudioComponentWidget constructor
    SpeakerWidget(AudioComponent ac, AnchorPane parentIn) {
        // Data members our widget can set from inputs when constructor is called
        audioComponent = ac;
        parent = parentIn;

        // Base shape for widget is a horizontal box, we can set it's styles here.
        baseLayout = new VBox();
        baseLayout.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 3");
        baseLayout.setAlignment(Pos.CENTER_RIGHT);

        // add the base layout to the widget * For ALL AudioComponentWidgets
        this.getChildren().add(baseLayout);

        // Right side, close button and connection circle * For ALL AudioComponentWidgets
        Label label = new Label("Speaker");
        input = new Circle(10);
        input.setFill(Color.BLACK);

        // add the button and circle to the right side VBox
        baseLayout.getChildren().add(label);
        baseLayout.getChildren().add(input);
    }

    public void connectInput(AudioComponent toPlay){
        this.audioComponent = toPlay;
    }

    // method to get audio component from widget. Used to play sounds in SynthesizeApplication
    public AudioComponent getAudioComponent() {return audioComponent;};

    // method that takes in a mouse event x and y and checks if it is over an output jack
    public boolean isOnInput(double x, double y){

        // input jack coordinates
        double xMin = input.getLocalToSceneTransform().getTx()-input.getRadius();
        double yMin = input.getLocalToSceneTransform().getTy()-input.getRadius();
        double xMax = input.getLocalToSceneTransform().getTx()+input.getRadius();
        double yMax = input.getLocalToSceneTransform().getTy()+input.getRadius();

        if (x > xMin && x < xMax && y > yMin && y < yMax){
            return true;
        } else {
            return false;
        }
    }

    // data members stored in the widget
    protected AudioComponent audioComponent;
    private AnchorPane parent;
    protected VBox baseLayout;
    private AudioComponentWidgetBase widgetGetsOutput = null;
    protected String name = "Speaker";
    private Line line;
    public Circle input;
    private Label nameLabel;
    double mouStartDragX, mouseStartDragY, widgetStartDragX, widgetStartDragY;
    int posX = 650;
    int posY = 300;
    int radius = 20;



}
