package com.example.synthesizer2;

import static java.lang.Math.sin;

public class VFSineWave implements AudioComponent {

    @Override
    public AudioClip getClip() {

        AudioClip adjusted = ac.getClip();
        double frequency = 0.0;
        int maxValue = adjusted.MaxValue;
        for (int i = 0; i < (adjusted.audioData.length / 2); i++){
                frequency += ((2.0 * Math.PI * (double)adjusted.getSample(i) )/ (float)(adjusted.audioData.length/2));
                adjusted.setSample(i, (int)(maxValue * sin( frequency )));
            }
        return adjusted;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent ac) {
        this.ac = ac;
    }

    public void removeInput(AudioComponent ac) {
        this.ac = null;
    }

    private AudioComponent ac;
}

