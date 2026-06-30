package assignment06;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ContainsTimerBalanced extends TimerTemplate {

    // data structure to test method on:
    BinarySearchTree<Integer> testTree;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public ContainsTimerBalanced(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }


    @Override
    protected void setup(int n) {
        testTree = new BinarySearchTree<>();
        ArrayList<Integer> randomizedList = new ArrayList<>();
        for (int i = 0; i < n; i+=2) {
            randomizedList.add(i);
        }
        Collections.shuffle(randomizedList);
        testTree.addAll(randomizedList);
    }

    @Override
    protected void timingIteration(int n) {
        testTree.contains(n);
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

        var timer = new ContainsTimerBalanced(problemSizes, 10000);
        var results = timer.run();

        System.out.println("n, time");
        for(var result: results){
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}
