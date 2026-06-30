package com.example.synthesizer2;

import static java.lang.Math.sin;

public class WhiteNoise implements AudioComponent {

    @Override
    public AudioClip getClip() {
        AudioClip whiteNoise = new AudioClip();
        for (int i = 0; i < whiteNoise.audioData.length / 2; i++) {
            int maxValue = 15000;
            int minValue = -15000;
            // get a random value in range
            int random = (int) (Math.random() * (maxValue - minValue) + minValue);
            whiteNoise.setSample(i, random);
        }
        return whiteNoise;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent ac) {
        assert false;
    }

    @Override
    public void removeInput(AudioComponent ac) {
        assert false;
    }
}
