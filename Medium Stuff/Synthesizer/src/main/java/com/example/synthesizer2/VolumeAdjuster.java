package com.example.synthesizer2;

public class VolumeAdjuster implements AudioComponent {
    @Override
    public AudioClip getClip() {
        AudioClip adjusted = input.getClip();
        // Modify the original clip. adjustedSample[i] = scale* sample[i]
        for (int i = 0; i < (adjusted.audioData.length / 2); i++){
            int maxValue = adjusted.MaxValue;
            int minValue = adjusted.MinValue;
            int value = adjusted.getSample(i);
            if (value < minValue) {
                adjusted.setSample(i, minValue);
            } else if (value > maxValue) {
                adjusted.setSample(i, maxValue);
            } else {
                adjusted.setSample(i, (int)(scale*value));
            }
        }
        return adjusted;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent ac) {
        this.input = ac;
    }

    // constructor sets input
    VolumeAdjuster(double scale) {
        this.scale = scale;
    }
    // method to remove the input
    public void removeInput(AudioComponent ac) {
        this.input = null;
    }

    private AudioComponent input;
    public double scale;
}
