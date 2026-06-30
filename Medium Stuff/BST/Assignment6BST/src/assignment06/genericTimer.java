package assignment06;

import java.io.IOException;
import java.util.ArrayList;

public class genericTimer extends TimerTemplate {

    // data structure to test method on:
    BinarySearchTree<String> dictionary;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public genericTimer(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }


    @Override
    protected void setup(int n) {

    }

    @Override
    protected void timingIteration(int n) {

    }

    @Override
    protected void compensationIteration(int n) {

    }

    public static void main(String[] args) throws IOException {

        ArrayList<Integer> ns = new ArrayList<>();
        for(double n = 10; n < 100000; n *= 2){
            ns.add((int)n);
        }

        //convert to int[]
        int[] problemSizes = new int[ns.size()];
        for(int i = 0; i < problemSizes.length; i++){
            problemSizes[i] = ns.get(i);
        }

        var timer = new genericTimer(problemSizes, 1000);
        var results = timer.run();

        System.out.println("n, time");
        for(var result: results){
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}
