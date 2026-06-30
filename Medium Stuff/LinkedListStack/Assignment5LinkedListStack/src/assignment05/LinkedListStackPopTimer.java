package assignment05;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class LinkedListStackPopTimer extends TimerTemplate {

    // data structure to test method on:
    LinkedListStack<Integer> testListStack;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public LinkedListStackPopTimer(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }


    @Override
    protected void setup(int n) {
        testListStack = new LinkedListStack<>();
        for (int i = 0; i < n; i++) {
            testListStack.push(i);
        }
    }

    @Override
    protected void timingIteration(int n) {
        testListStack.pop();
        testListStack.push(n);
    }

    @Override
    protected void compensationIteration(int n) {
        testListStack.push(n);
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

        var timer = new LinkedListStackPopTimer(problemSizes, 10000);
        var results = timer.run();

        System.out.println("n, time");
        for(var result: results){
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}
