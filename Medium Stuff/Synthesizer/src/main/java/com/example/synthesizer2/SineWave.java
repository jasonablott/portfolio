package com.example.synthesizer2;
import java.lang.Math;
import static java.lang.Math.sin;

/*
With the  AudioComponent interface defined, we can make a concrete AudioComponent, meaning a class that implements the
AudioComponent interface. Create a class SineWave that implements your interface that can produce an audioData clip
containing a sine wave.
*/

public class SineWave implements AudioComponent {


    public int frequency;


    @Override
    public AudioClip getClip() {
        // create and fill in an AudioClip with sample values from a sine wave. The math isn't too
        AudioClip sineWave = new AudioClip();
        for (int i = 0; i < sineWave.audioData.length / 2; i++) {
            int maxValue = sineWave.MaxValue;
            int sampleValue = (int) (maxValue * sin((2 * Math.PI * frequency * i / AudioClip.sampleRate)));
            sineWave.setSample(i, sampleValue);
        }
        return sineWave;
    }

    @Override
    public boolean hasInput() {
        // define method here
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        // define method here
        assert false;
    }

    @Override
    public void removeInput(AudioComponent ac) {

    }

    // This class should take the desired frequency (pitch) as a constructor parameter (you'll eventually want getters and
    // setters for the frequency as well, but we'll save that for later).

    SineWave(int frequency) {
        this.frequency = frequency;
    }
}
