package com.example.synthesizer;

/*
With the  AudioComponent interface defined, we can make a concrete AudioComponent, meaning a class that implements the
AudioComponent interface. Create a class SineWave that implements your interface that can produce an audioData clip
containing a sine wave.
*/

public class SineWave implements AudioComponent {
    @Override
    public AudioClip getClip() {
        // create and fill in an AudioClip with sample values from a sine wave. The math isn't too
        // ugly. In pseudocode:
        // sample[ i ] = maxValue * sine( 2*pi*frequency * i / sampleRate );
        // Note: for testing, a frequency of 440 is the "A" (note) frequency that most orchestras tune to, so it's a good test.
        // maxValue controls the "loudness" of your sine wave. Setting this to Short.Max would make it as loud as possible.
        return null;
    }

    @Override
    public boolean hasInput() {
        // define method here
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        // define method here
    }

    // This class should take the desired frequency (pitch) as a constructor parameter (you'll eventually want getters and
    // setters for the frequency as well, but we'll save that for later).

    SineWave(int frequency) {
        // define constructor here
    }
}
