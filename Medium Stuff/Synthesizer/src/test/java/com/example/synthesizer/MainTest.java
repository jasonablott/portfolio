package com.example.synthesizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class MainTest {

    @Test
    public void runAllTests() {
        // write tests here
        AudioClip test = new AudioClip();
        // smallest short is -32768 and largest is 32767
        for (int i = 0; i < (44100); i++){
            test.setSample(i, (i-32768));
        }
        int [] samples = new int[44100];
        for (int i = 0; i < 44100; i++){
            samples[i] = test.getSample(i);
        }

        for (int i = 0; i <=44100; i+=100){
            System.out.println("index: " + i + " ");
            System.out.println("sample gotten with getSample");
            System.out.println(samples[i]);
            System.out.println();
        }
    }
}