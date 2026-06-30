package com.example.synthesizer2;

// All of the components of our synthesizer will have a single output (an AudioClip) and an optional input
// (something that produces an AudioClip). We will define an interface which will allow us to connect our components
// together and make fun noises.

public interface AudioComponent {

    // The AudioComponent interface should have the following methods:

    // return the current sound produced by this component
    AudioClip getClip();

    // can you connect something to this as an input?
    boolean hasInput();

    // connect another device to this input. For most classes implementing this
    // interface, this method will just store a reference to the AudioComponent parameter. If the component doesn't accept
    // inputs, you can assert( false ) in here.
    void connectInput(AudioComponent ac /*, int index*/);

    void removeInput(AudioComponent ac);
}

// With this interface defined, we can make a concrete AudioComponent, meaning a class that implements the AudioComponent
// interface.
