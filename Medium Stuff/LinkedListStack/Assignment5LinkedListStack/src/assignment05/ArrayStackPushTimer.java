package assignment05;

import java.io.*;
import java.util.ArrayList;

public class ArrayStackPushTimer extends assignment05.TimerTemplate {

    // data structure to test method on:
    ArrayStack<Integer> testArrayStack;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public ArrayStackPushTimer(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }


    @Override
    protected void setup(int n) {
        testArrayStack = new ArrayStack<>();
    }

    @Override
    protected void timingIteration(int n) {
        testArrayStack.push(n);
    }

    @Override
    protected void compensationIteration(int n) {

    }

    public static void main(String[] args) throws IOException {

        ArrayList<Integer> ns = new ArrayList<>();
        for(double n = 10; n < 1000000; n *= 2){
            ns.add((int)n);
        }

        //convert to int[]
        int[] problemSizes = new int[ns.size()];
        for(int i = 0; i < problemSizes.length; i++){
            problemSizes[i] = ns.get(i);
        }

        var timer = new ArrayStackPushTimer(problemSizes, 10000);
        var results = timer.run();

        System.out.println("n, time");
        for(var result: results){
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}
