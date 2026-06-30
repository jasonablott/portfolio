package com.example.synthesizer2;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

public class Main {
    public static void main(String[] args) throws LineUnavailableException {
        // Get properties from the system about samples rates, etc.
// AudioSystem is a class from the Java standard library.
        Clip c = AudioSystem.getClip(); // Note, this is different from our AudioClip class.

// This is the format that we're following, 44.1 KHz mono audio, 16 bits per sample.
        AudioFormat format16 = new AudioFormat( 44100, 16, 1, true, false );


        // Generate clips to test different AudioComponents


        // sineWave

        // make sineWave
        AudioComponent sine = new SineWave(440);
        // getClip of sineWave
        AudioClip sineClip = sine.getClip();

        // volume adjuster : change scale to change volume

        // set scale to adjust volume to
        double scale = 3;
        // make a volume object. Connect the sine wave as the input for your volume object
        AudioComponent volume = new VolumeAdjuster(1);
        volume.connectInput(sine);
        // Get the audio from your volume object and play it.
        AudioClip volumeClip = volume.getClip();

        // Square wave

        // make a squareWave
        AudioComponent square = new SquareWave(220);
        // getClip from squareWave
        AudioClip squareClip = square.getClip();

        // white noise

        // make a whiteNoise audioComponent
        AudioComponent whiteNoise = new WhiteNoise();
        // get Clip from whiteNoise
        AudioClip whiteNoiseClip = whiteNoise.getClip();

        // mixer

        // make a mixer audioComponent
        Mixer mixer = new Mixer();
        // connect an input audiocomponent to mix
        mixer.connectInput(sine);
        // connect an input audiocomponent to mix
        mixer.connectInput(square);
        // connect an input audiocomponent to mix
        mixer.connectInput(whiteNoise);
        // getClip from mixer
        AudioClip mixedClip  = mixer.getClip();

        // multiplicative filter

        // make a multiplicativeFilter
        MultiplicativeFilter filter = new MultiplicativeFilter();
        // connect some audioComponents to multiply
        filter.connectInput(sine);
        filter.connectInput(square);
        // get clip from filter
        AudioClip multipliedClip = filter.getClip();

        //

        // make linear ramp for VFSinewave
        LinearRamp linearRamp = new LinearRamp(50, 2000);
        // make VFSineWave
        VFSineWave vfs = new VFSineWave();
        // Connect VFSinewave to linearRamp
        vfs.connectInput(linearRamp);
        // get clip to play
        AudioClip vfsClip = vfs.getClip();


        // play the clip below
        AudioClip clipToPlay = volumeClip;


        // code to play clip
        c.open( format16, clipToPlay.getData(), 0, clipToPlay.getData().length ); // Reads data from our byte array to play it.

        System.out.println( "About to play..." );
        c.start(); // Plays it.
        //c.loop( 2 ); // Plays it 2 more times if desired, so 6 seconds total

        // Makes sure the program doesn't quit before the sound plays.
        while( c.getFramePosition() < AudioClip.TOTAL_SAMPLES || c.isActive() || c.isRunning() ){
            // Do nothing while we wait for the note to play.
        }

        System.out.println( "Done." );
        c.close();
    }
}
