package com.example.synthesizer2;

import javax.sound.sampled.*;


public class AudioListener implements LineListener {

    public AudioListener(Clip c) {clip = c;}

    @Override
    public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
            clip.close();
        }
    }
    private Clip clip;
}
