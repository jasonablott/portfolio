package assignment06;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class TreeSetAddTimer extends TimerTemplate {

    // data structure to test method on:
    TreeSet<Integer> testTree = new TreeSet<Integer>();

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public TreeSetAddTimer(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }


    @Override
    protected void setup(int n) {
        ArrayList<Integer> randomizedList = new ArrayList<>();
        for (int i = 0; i < n; i+=2) {
            randomizedList.add(i);
        }
        Collections.shuffle(randomizedList);
        testTree.addAll(randomizedList);
    }

    @Override
    protected void timingIteration(int n) {
        testTree.add((n/2)+1);
        testTree.remove((n/2)+1);
    }

    @Override
    protected void compensationIteration(int n) {
        testTree.remove((n/2)+1);
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

        var timer = new TreeSetAddTimer(problemSizes, 10000);
        var results = timer.run();

        System.out.println("n, time");
        for(var result: results){
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}
