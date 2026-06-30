package com.example.synthesizer2;

import static java.lang.Math.sin;

public class LinearRamp implements AudioComponent{
    int frequency;


    @Override
    public AudioClip getClip() {
        AudioClip linearRamp = new AudioClip();
        for (int i = 0; i < linearRamp.audioData.length / 2; i++) {
            int sampleValue = (int)((( start * (float)( linearRamp.audioData.length - i )) + (stop * i) ) / (float)linearRamp.audioData.length);
            linearRamp.setSample(i, sampleValue);
        }
        return linearRamp;
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


    LinearRamp(float start, float stop) {
        this.start = start;
        this.stop = stop;
    }

    public float start;
    public float stop;

}
