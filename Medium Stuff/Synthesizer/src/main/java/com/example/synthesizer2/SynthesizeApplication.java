package com.example.synthesizer2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.ArrayList;

// our application class will inherit from the Application class that already exists
public class SynthesizeApplication extends Application {

    // Start method to start our app
    @Override
    public void start(Stage stage) throws IOException {

        // set a BorderPane  to be the base layout of the GUI
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Synthesizer");

        // Right side menu
        VBox rightPane = new VBox();
        rightPane.setPadding(new Insets(5));
        rightPane.setStyle("-fx-background-color: gray;");
        // add buttons to the right menu
        // sineWave button
        Button sineWaveButton = new Button("Sine Wave");
        rightPane.getChildren().add(sineWaveButton);
        sineWaveButton.setOnAction(e -> createComponent("SineWave"));
        // squareWave button
        Button squareWaveButton = new Button("Square Wave");
        rightPane.getChildren().add(squareWaveButton);
        squareWaveButton.setOnAction(e -> createComponent("SquareWave"));
        // white noise button
        Button whiteNoiseButton = new Button("White Noise");
        rightPane.getChildren().add(whiteNoiseButton);
        whiteNoiseButton.setOnAction(e -> createComponent("WhiteNoise"));
        // Volume control button
        Button volumeControlButton = new Button("Volume Control");
        rightPane.getChildren().add(volumeControlButton);
        volumeControlButton.setOnAction(e -> createComponent("VolumeControl"));
        // Mixer Button
        Button mixerButton = new Button("Mixer");
        rightPane.getChildren().add(mixerButton);
        mixerButton.setOnAction(e -> createComponent("Mixer"));
        // Multiplicative Filter Button
        Button multiplicativeFilterButton = new Button("Multiplicative Filter");
        rightPane.getChildren().add(multiplicativeFilterButton);
        multiplicativeFilterButton.setOnAction(e -> createComponent("MultiplicativeFilter"));
        // Linear Ramp Button
        Button linearRampButton = new Button("Linear Ramp");
        rightPane.getChildren().add(linearRampButton);
        linearRampButton.setOnAction(e -> createComponent("LinearRamp"));
        // VF SineWave Button
        Button VFSineWaveButton = new Button("VF Sine Wave");
        rightPane.getChildren().add(VFSineWaveButton);
        VFSineWaveButton.setOnAction(e -> createComponent("VFSineWave"));

        // Center of screen area
        mainCanvas = new AnchorPane();
        // onMousePressed method listener, method defined below
        mainCanvas.setOnMousePressed(this::OnPaneMouseClicked);
        // make onMouseDragged method listener, define method below
        mainCanvas.setOnMouseDragged(this::moveLine);
        // make ouMouseRelease method listener, define method below
        mainCanvas.setOnMouseReleased(this::OnPaneMouseReleased);

        mainCanvas.setStyle("-fx-background-color: lightgray;");

        // speaker widget for mainCanvas
        SpeakerWidget speaker = new SpeakerWidget(null, mainCanvas);
        mainCanvas.getChildren().add(speaker);
        this.speaker = speaker;
        speaker.setLayoutX(speaker.posX);
        speaker.setLayoutY(speaker.posY);

        // Bottom menu
        HBox bottomPane = new HBox();
        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setStyle("-fx-background-color: gray;");
        Button playButton = new Button("Play");
        playButton.setOnAction(e -> play());
        bottomPane.getChildren().add(playButton);

        // Top frame
        Label title = new Label("Jason's Synthesizer");
        title.setPadding(new Insets(5, 5, 5, 5));

        // actions to build UI base
        root.setRight(rightPane);
        root.setCenter(mainCanvas);
        root.setBottom(bottomPane);
        root.setTop(title);
        stage.setScene(scene);
        stage.show();
    }

    private void play() {

        try {
            // AudioSystem is a class from the Java standard library.
            Clip c = AudioSystem.getClip(); // Note, this is different from our AudioClip class.
            // This is the format that we're following, 44.1 KHz mono audio, 16 bits per sample.

            AudioListener listener = new AudioListener(c);

            AudioFormat format16 = new AudioFormat(44100, 16, 1, true, false);

            AudioClip clip = speaker.getAudioComponent().getClip();

            // code to play clip
            c.open(format16, clip.getData(), 0, clip.getData().length); // Reads data from our byte array to play it.

            System.out.println("About to play...");
            c.start(); // Plays it.
           // c.loop(2); // Plays it 2 more times if desired, so 6 seconds total

            // Makes sure the method doesn't quit before the sound plays.
            c.addLineListener(listener);

            System.out.println("Done.");
        }
        catch (LineUnavailableException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private void createComponent(String name) {

        if (name.equals("SineWave")) {
            AudioComponent ac = new SineWave(440);
            SineWidget sineWidget = new SineWidget(ac, mainCanvas, "Sine Wave");
            // add to list of widgets in main program
            allWidgets.add(sineWidget);
            // add to parent so it will be displayed
            mainCanvas.getChildren().add(sineWidget);
            numACWs++;
        } else if (name.equals("SquareWave")) {
            AudioComponent ac = new SquareWave(440);
            SquareWidget squareWidget = new SquareWidget(ac, mainCanvas, "Square Wave");
            allWidgets.add(squareWidget);
            mainCanvas.getChildren().add(squareWidget);
            numACWs++;
        } else if (name.equals("WhiteNoise")) {
            AudioComponent ac = new WhiteNoise();
            WhiteNoiseWidget whiteNoiseWidget = new WhiteNoiseWidget(ac, mainCanvas, "White Noise");
            allWidgets.add(whiteNoiseWidget);
            mainCanvas.getChildren().add(whiteNoiseWidget);
            numACWs++;
        } else if (name.equals("VolumeControl")) {
            AudioComponent ac = new VolumeAdjuster(1.0);
            VolumeControlWidget volumeControlWidget = new VolumeControlWidget(ac, mainCanvas, "Volume Control");
            allWidgets.add(volumeControlWidget);
            mainCanvas.getChildren().add(volumeControlWidget);
            numACWs++;
        } else if (name.equals("Mixer")) {
            AudioComponent ac = new Mixer();
            MixerWidget mixer = new MixerWidget(ac, mainCanvas, "Mixer");
            allWidgets.add(mixer);
            mainCanvas.getChildren().add(mixer);
            numACWs++;
        } else if (name.equals("MultiplicativeFilter")) {
            AudioComponent ac = new MultiplicativeFilter();
            MultiplicativeFilterWidget multiplicativeFilter = new MultiplicativeFilterWidget(ac, mainCanvas, "Multiplicative Filter");
            allWidgets.add(multiplicativeFilter);
            mainCanvas.getChildren().add(multiplicativeFilter);
            numACWs++;
        } else if (name.equals("LinearRamp")) {
            AudioComponent ac = new LinearRamp(500, 5000);
            LinearRampWidget linearRamp = new LinearRampWidget(ac, mainCanvas, "Linear Ramp");
            allWidgets.add(linearRamp);
            mainCanvas.getChildren().add(linearRamp);
            numACWs++;
        } else if (name.equals("VFSineWave")) {
            AudioComponent ac = new VFSineWave();
            VFSineWaveWidget VFSineWave = new VFSineWaveWidget(ac, mainCanvas, "VF Sine Wave");
            allWidgets.add(VFSineWave);
            mainCanvas.getChildren().add(VFSineWave);
            numACWs++;
        }

    }


    public static void main(String[] args) {
        launch();
    }


    // method to handle mouse click on output jack
    public void OnPaneMouseClicked (MouseEvent mouseEvent) {
        // is mouse click on an output jack?
        // loop through each widget to check if the click is over an output jack
        for (AudioComponentWidgetBase acw : allWidgets){
            if (acw.isOnOutput(mouseEvent.getSceneX(), mouseEvent.getSceneY())){
                // if the click is over an output jack, draw a line to and from the same point to start
                double distance = Math.sqrt(
                        Math.pow(mouseEvent.getSceneX()-acw.output.getLocalToSceneTransform().getTx(), 2)
                        + Math.pow(mouseEvent.getSceneY()-acw.output.getLocalToSceneTransform().getTy(), 2)
                );
                if (distance < acw.output.getRadius()) {
                    acw.line = new Line();
                    acw.line.setStrokeWidth(2);
                    acw.line.setStroke(Color.BLACK);
                    mainCanvas.getChildren().add(acw.line);

                    acw.line.setStartX(mouseEvent.getX());
                    acw.line.setStartY(mouseEvent.getY());
                    acw.line.setEndX(mouseEvent.getX());
                    acw.line.setEndY(mouseEvent.getY());

                }
                // need a way to remember this widget is the output widget
                this.output = acw;
            }
        }
    }

    // method to move line while mouse is dragged
    public void moveLine (MouseEvent mouseEvent) {
        if (output != null && output.line != null){
            output.line.setEndX(mouseEvent.getX());
            output.line.setEndY(mouseEvent.getY());
        }
    }

    // method to create cable and link audiocomponents when mouse is released over an input jack
    public void OnPaneMouseReleased (MouseEvent mouseEvent) {
        if(output != null) {
            // check if release is over an output jack
            if (speaker.isOnInput(mouseEvent.getSceneX(), mouseEvent.getSceneY())) {
                speaker.connectInput(this.output.getAudioComponent());
                Cable cable = new Cable(
                        output.line.getStartX(),
                        output.line.getStartY(),
                        output.line.getEndX(),
                        output.line.getEndY()
                );
                cable.setInput(output);
                //cable.setOutput(output);
                cable.setSpeaker(speaker);
                mainCanvas.getChildren().add(cable.line);
                allCables.add(cable);
                mainCanvas.getChildren().remove(output.line);
                output.line = null;
                this.input = null;
                this.output = null;
            } else {
                // loop through each widget to check if the release is over an input jack
                for (AudioComponentWidgetBase acw : allWidgets) {
                    if (acw.isOnInput(mouseEvent.getSceneX(), mouseEvent.getSceneY())) {
                        double Distance = Math.sqrt(Math.pow(mouseEvent.getSceneX() - acw.input.getLocalToSceneTransform().getTx(), 2)
                                + Math.pow(mouseEvent.getSceneY() - acw.input.getLocalToSceneTransform().getTy(), 2)
                        );

                        if (output.line != null && Distance < acw.input.getRadius()) {
                            // link audio connections now
                            this.input = acw;
                            input.getAudioComponent().connectInput(output.getAudioComponent());

                            Cable cable = new Cable(
                                    output.line.getStartX(),
                                    output.line.getStartY(),
                                    output.line.getEndX(),
                                    output.line.getEndY()
                            );
                            cable.setInput(output);
                            cable.setOutput(input);
                            mainCanvas.getChildren().add(cable.line);
                            allCables.add(cable);
                            mainCanvas.getChildren().remove(output.line);
                            output.line = null;
                            this.input = null;
                            this.output = null;
                        }
                    }
                }
                mainCanvas.getChildren().remove(output.line);
                output.line = null;
                this.input = null;
                this.output = null;
            }
        }
    }


    // data members
    // keep track out AudioComponentWidgets to dynamically place them on screen
    public static int numACWs;

    public static AnchorPane mainCanvas;
    public static SpeakerWidget speaker;

    public AudioComponentWidgetBase input;
    public AudioComponentWidgetBase output;

    public static ArrayList<Cable> allCables = new ArrayList<>();
    public static ArrayList<AudioComponentWidgetBase> allWidgets = new ArrayList<AudioComponentWidgetBase>();
    public static ArrayList<AudioComponentWidgetBase> widgetsConnectedToSpeaker = new ArrayList<>();

}