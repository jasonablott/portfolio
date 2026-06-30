package com.example.synthesizer2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class AudioComponentWidgetBase extends Pane {
    // AudioComponentWidget constructor
    AudioComponentWidgetBase(AudioComponent ac, AnchorPane parentIn, String nameIn) {
        // Data members our widget can set from inputs when constructor is called
        audioComponent = ac;
        parent = parentIn;
        name = nameIn;

        // Base shape for widget is a horizontal box, we can set it's styles here.
        baseLayout = new HBox();
        // onMousePressed method listener, method defined below
        baseLayout.setOnMousePressed(this::onWidgetMouseClicked);
        // make moveWidget method listener for mouse drag
        baseLayout.setOnMouseDragged(this::moveWidget);
        // make onMouseRelease method listener, define method below
        baseLayout.setOnMouseReleased(this::onWidgetMouseReleased);

        baseLayout.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 3");

        // add the base layout to the widget * For ALL AudioComponentWidgets
        this.getChildren().add(baseLayout);

        // Right side, close button and connection circle * For ALL AudioComponentWidgets
        VBox rightSide = new VBox();
        rightSide.setAlignment(Pos.CENTER);
        rightSide.setPadding(new Insets(3));
        rightSide.setSpacing(5);
        // make the button and connection circle
        Button close = new Button("X");
//        Circle circle = new Circle(10);
//        circle.setFill(Color.GRAY);
        close.setOnAction(e -> destroyWidget());
        // add the button and circle to the right side VBox
        rightSide.getChildren().add(close);
//        rightSide.getChildren().add(circle);


        // add right side to base
        baseLayout.getChildren().add(rightSide);

        // set location
        this.setLayoutX(20);
        this.setLayoutY(20+(80*SynthesizeApplication.numACWs));

    }

    // get rid of the widget by removing itself from areas it exists elsewhere
    private void destroyWidget() {
        parent.getChildren().remove(this);
        SynthesizeApplication.allWidgets.remove(this);
        SynthesizeApplication.numACWs--;
        ArrayList<Cable> cablesToRemove = new ArrayList<>();
        for (Cable cable : SynthesizeApplication.allCables) {
            if (cable.input == this || cable.output == this) {
                cablesToRemove.add(cable);
                SynthesizeApplication.mainCanvas.getChildren().remove(cable.line);
            }
        }
        for (Cable cable : cablesToRemove) {
            // doesn't work for anything with multiple inputs
            //cable.output.getOutput().connectInput(null);
            if (cable.output != null) {
                cable.output.getOutput().removeInput(cable.input.getOutput());
            }
            // check if cable is connected to speaker
            if (cable.speaker != null){
                // if so, remove speaker input
                SynthesizeApplication.speaker.connectInput(null);
            }
            SynthesizeApplication.allCables.remove(cable);
        }
    }

    // method to get audio component from widget. Used to play sounds in SynthesizeApplication
    // Can also be used to get output when user clicks output jack?
    public AudioComponent getAudioComponent() {return audioComponent;};

    public AudioComponent getOutput() {
        // if user clicks on output circle {
            // get audioComponent
        //}
        return audioComponent;
    }

    // method that takes in a mouse event x and y and checks if it is over an output jack
    public boolean isOnOutput (double x, double y){
        if (output == null) return false;
        // output jack coordinates
        double xMin = output.getLocalToSceneTransform().getTx()-output.getRadius();
        double yMin = output.getLocalToSceneTransform().getTy()-output.getRadius();
        double xMax = output.getLocalToSceneTransform().getTx()+output.getRadius();
        double yMax = output.getLocalToSceneTransform().getTy()+output.getRadius();

        if (x > xMin && x < xMax && y > yMin && y < yMax){
            return true;
        } else {
            return false;
        }
    }

    // method that takes in a mouse event x and y and checks if it is over an output jack
    public boolean isOnInput (double x, double y){
        if (input == null) return false;
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

    // method to store coordinates from initial click to move widget
    public void onWidgetMouseClicked (MouseEvent mouseEvent) {
        if (!this.isOnOutput(mouseEvent.getSceneX(), mouseEvent.getSceneY())) {
            isMoving = true;
            mouseStartDragX = baseLayout.getLayoutX() - mouseEvent.getSceneX();
            mouseStartDragY = baseLayout.getLayoutY() - mouseEvent.getSceneY();
        }
        // for cable in cables if start x and y are on this output, we need to move the line start x and y the same as the widget
        for (Cable cable : SynthesizeApplication.allCables) {
            if (cable.input == this) {
                cableMoving = true;
                cableInDeltaX = cable.line.getStartX() - baseLayout.getLayoutX() + mouseStartDragX;
                cableInDeltaY = cable.line.getStartY() - baseLayout.getLayoutY() + mouseStartDragY;
            }
            if (cable.output == this){
                cableMoving = true;
                cableOutDeltaX = cable.line.getEndX() - baseLayout.getLayoutX() + mouseStartDragX;
                cableOutDeltaY = cable.line.getEndY() - baseLayout.getLayoutY() + mouseStartDragY;
            }
        }
    }

    // method to move widget while mouse is dragged
    public void moveWidget (MouseEvent mouseEvent) {
        if (isMoving) {

            baseLayout.setLayoutX(mouseEvent.getSceneX() + mouseStartDragX);
            baseLayout.setLayoutY(mouseEvent.getSceneY() + mouseStartDragY);

            if (cableMoving) {
                for (Cable cable : SynthesizeApplication.allCables) {
                    if (cable.input == this) {
                        cable.updateCableStart(mouseEvent.getSceneX() + cableInDeltaX, mouseEvent.getSceneY() + cableInDeltaY);
                    }
                    if (cable.output == this) {
                        cable.updateCableEnd(mouseEvent.getSceneX() + cableOutDeltaX, mouseEvent.getSceneY() + cableOutDeltaY);
                    }
                }

            }
        }
    }

    // method to set widget location when mouse is released and reset local coordinates for next move
    public void onWidgetMouseReleased (MouseEvent mouseEvent) {
        if (isMoving){
            baseLayout.setLayoutX(mouseEvent.getSceneX() + mouseStartDragX);
            baseLayout.setLayoutY(mouseEvent.getSceneY() + mouseStartDragY);
            isMoving = false;
            cableMoving = false;
        }
    }

    // data members stored in the widget
    protected AudioComponent audioComponent;
    private AnchorPane parent;
    protected HBox baseLayout;
    private AudioComponentWidgetBase widgetGetsOutput = null;
    protected String name;
    protected Circle input;
    protected Circle output;
    protected Line line;
    private Label nameLabel;
    double mouseStartDragX, mouseStartDragY, widgetStartDragX, widgetStartDragY;
    boolean isMoving = false;
    boolean cableMoving = false;
    double cableInDeltaX;
    double cableInDeltaY;
    double cableOutDeltaX;
    double cableOutDeltaY;



}

