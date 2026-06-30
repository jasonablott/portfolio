package assignment06;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class TreeSetAddAllTimer extends TimerTemplate {

    // data structure to test method on:
    TreeSet<Integer> testTreeSet = new TreeSet<>();
    ArrayList<Integer> randomizedList;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public TreeSetAddAllTimer(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }


    @Override
    protected void setup(int n) {
        randomizedList = new ArrayList<>();
        for (int i = 0; i < n; i+=2) {
            randomizedList.add(i);
        }
        Collections.shuffle(randomizedList);
    }

    @Override
    protected void timingIteration(int n) {
        testTreeSet.addAll(randomizedList);
        testTreeSet.clear();
    }

    @Override
    protected void compensationIteration(int n) {
        testTreeSet.clear();
    }

    public static void main(String[] args) throws IOException {

        ArrayList<Integer> ns = new ArrayList<>();
        for(double n = 10; n < 50000; n *= 2){
            ns.add((int)n);
        }

        //convert to int[]
        int[] problemSizes = new int[ns.size()];
        for(int i = 0; i < problemSizes.length; i++){
            problemSizes[i] = ns.get(i);
        }

        var timer = new TreeSetAddAllTimer(problemSizes, 1000);
        var results = timer.run();

        System.out.println("n, time");
        for(var result: results){
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}
