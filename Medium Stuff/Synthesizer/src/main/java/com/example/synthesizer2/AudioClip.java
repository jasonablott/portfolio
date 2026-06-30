package com.example.synthesizer2;

import java.util.Arrays;

import static java.util.List.copyOf;

// this class represents an audioData clip that will get passed throughout our system.
public class AudioClip {


    // Data


    // static constant for the duration (2.0 seconds)
    public static final double duration = 2.0;
    // static constant for sample rate (44100)
    public static final int sampleRate = 44100;
    public static final int TOTAL_SAMPLES = (int)(duration * sampleRate);
    public final int MaxValue = 15000;
    public final int MinValue = -15000;

    // member variable that contains the actual byte array of bytes over time
    byte [] audioData = new byte[sampleRate * (int)duration * 2];


    // Methods


    /*
    method int getSample( index ) that returns the sample passed as an int. You will need
    to use bitwise operators to perform these conversions! The ints that are returned should be in the range of
    shorts. These are the closest thing we can do in Java to overloading operator[].
     */

    public int getSample( int index ){
        // this method returns an int for a sample at index in this byte array audioData.
        byte byte1 = audioData[(2*index)];
        byte byte2 = audioData[(2*index)+1];
        return ((byte1) << 8) | ((byte2) & 0xFF);
    }

    // method setSample( index, value ) sets the sample passed as an int. You will need
    //    to use bitwise operators to perform these conversions! The ints that are passed should be in the range of
    //    shorts. These are the closest thing we can do in Java to overloading operator[].

    public void setSample(int index, int value){
        // this method sets a sample at an index from an int value
        audioData[(2*index)] = (byte)((value >> 8));
        audioData[(2*index)+1] = (byte)(value);
    }

    // A method byte[] getData() that returns our array (returning a copy isn't a bad idea to avoid issues of aliasing,
    // check out Arrays.copyOf). We need this method because the Java library that actually plays sounds expects our data
    // as an array of bytes. The values should be stored in Little Endian order. In other words, for the value of
    // sample i, the lower 8 bits should be stored at array[ 2*i ] and the upper 8 bits should be stored at
    // array[ (2*i) + 1 ].

    byte[] getData(){
        // this method returns a copy of our array of bytes representing the audioClip
        byte[] temp =  Arrays.copyOf(audioData, audioData.length);
        // swap every other index to make array little endian
        for (int i=0; i<temp.length; i+=2){
            byte tempByte = temp[i];
            temp[i] = temp[i+1];
            temp[i+1] = tempByte;
        }
        return temp;
    }

}


/*
Write a thorough set of JUnit tests that exercise the functionality of this class. In particular, make sure that your
getSample/setSample methods work properly for positive and negative numbers! I suggest assigning random numbers to an
Arraylist as well as an audioData clip and asserting that you get the right values when using your get method. There are
not that many shorts (only 2^16 which is small in computer terms), so you can actually make sure your class works with
every single one! I highly suggest that you create test cases where you set a sample value to the maximum value of a
short, and a sample value to the minimum value of a short.
*/
