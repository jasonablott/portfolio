package assignment07;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ContainsBadHashTimer extends TimerTemplate {

    // functor to make table with
    HashFunctor badHash = new BadHashFunctor();
    // list to store items for adding to table
    ArrayList<String> wordsToAdd;
    // data structure to test method on:
    ChainingHashTable testTable;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public ContainsBadHashTimer(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }


    @Override
    protected void setup(int n) {
        testTable = new ChainingHashTable(n, badHash);
        wordsToAdd = CollisionCounter.readFromFile(new File("/Users/jasonablott/MSD/6012/Week3/Day15/HW7HashTables/src/mobydick.txt"));
        for (int i = 0; i < n; i++) {
            testTable.add(wordsToAdd.get(i));
        }
    }

    @Override
    protected void timingIteration(int n) {
            testTable.contains(wordsToAdd.get(n));
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

        var timer = new ContainsBadHashTimer(problemSizes, 1000);
        var results = timer.run();

        System.out.println("n, time");
        for(var result: results){
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}
