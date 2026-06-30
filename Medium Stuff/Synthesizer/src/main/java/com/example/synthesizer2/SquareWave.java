package com.example.synthesizer2;

import static java.lang.Math.sin;

public class SquareWave implements AudioComponent{
    int frequency;


    @Override
    public AudioClip getClip() {
        // create and fill in an AudioClip with sample values from a square wave.

        // if(  ( frequency * i / sampleRate) % 1 > 0.5) {
        //   sample = maxValue
        // }
        // else {
        //   sample = -maxValue
        // }
        AudioClip squareWave = new AudioClip();
        int sampleValue;
        for (int i = 0; i < squareWave.audioData.length / 2; i++) {
            int maxValue = 15000;
            if ((((double) frequency * i / squareWave.audioData.length) % 1 > 0.5)) {
                sampleValue = maxValue;
            } else {
                sampleValue = maxValue * -1;
            }
            squareWave.setSample(i, sampleValue);
        }
        return squareWave;
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
        assert false;
    }

    // This class should take the desired frequency (pitch) as a constructor parameter (you'll eventually want getters and
    // setters for the frequency as well, but we'll save that for later).

    SquareWave(int frequency) {
        this.frequency = frequency;
    }
}
