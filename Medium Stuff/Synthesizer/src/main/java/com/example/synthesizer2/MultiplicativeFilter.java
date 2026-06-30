package com.example.synthesizer2;

import java.util.ArrayList;

public class  MultiplicativeFilter implements AudioComponent{

    @Override
    public AudioClip getClip() {
        // new clip to put mixed samples into
        AudioClip adjusted = new AudioClip();
        // arrayList to store clips from input audioComponents
        ArrayList<AudioClip> clips = new ArrayList<>();
        // go through audioComponents and get clips
        for (AudioComponent ac : audioComponents) {
            AudioClip clip = ac.getClip();
            clips.add(clip);
        }
        for (int i = 0; i < adjusted.audioData.length / 2; i++) {
            // for each sample get the int to set it to
            int sample = 1;
            // go through each clip we are mixing and add to the new sample value
            for (AudioClip clip : clips) {
                sample *= clip.getSample(i);
            }
            // set the new sample in the new clip, clamping to max and min values
            int maxValue = adjusted.MaxValue;
            int minValue = adjusted.MinValue;
            if (sample < minValue) {
                adjusted.setSample(i, minValue);
            } else if (sample > maxValue) {
                adjusted.setSample(i, maxValue);
            } else {
                adjusted.setSample(i, sample);
            }
        }
        // return the new, filled clip
        return adjusted;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent ac) {
        audioComponents.add(ac);
    }

    public void removeInput(AudioComponent ac) {
        audioComponents.remove(ac);
    }

    private ArrayList<AudioComponent> audioComponents = new ArrayList<>();

}
