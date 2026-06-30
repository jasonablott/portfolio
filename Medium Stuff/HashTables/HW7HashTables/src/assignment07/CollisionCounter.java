package assignment07;

import jdk.swing.interop.SwingInterOpUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


/**
 * Class that tests to find number of collisions and lambda value for each hash functor
 */
public class CollisionCounter {
    /**
     * Source: HW 6 assignment SpellChecker class
     * Returns a list of the words contained in the specified file. (Note that
     * symbols, digits, and spaces are ignored.)
     *
     * @param file
     *          - the File to be read
     * @return a List of the Strings in the input file
     */
    public static ArrayList<String> readFromFile(File file) {
        ArrayList<String> words = new ArrayList<>();

        try (Scanner fileInput = new Scanner(file)) {
            /*
             * Java's Scanner class is a simple lexer for Strings and primitive types (see
             * the Java API, if you are unfamiliar).
             */

            /*
             * The scanner can be directed how to delimit (or divide) the input. By default,
             * it uses whitespace as the delimiter. The following statement specifies
             * anything other than alphabetic characters as a delimiter (so that punctuation
             * and such will be ignored). The string argument is a regular expression that
             * specifies "anything but an alphabetic character". You need not understand any
             * of this for the assignment.
             */
            fileInput.useDelimiter("\\s*[^a-zA-Z]\\s*");

            while (fileInput.hasNext()) {
                String s = fileInput.next();
                if (!s.equals("")) {
                    words.add(s.toLowerCase());
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("File " + file + " cannot be found.");
        }

        //System.out.println("Document is " + words);

        return words;
    }

    public static void collisionTest(int startSize, int endSize){

        for (int i = startSize; i < endSize; i*=2) {
            // set table size and num words to read in for tests here
            int tableSize = i;
            int numWordsToAdd = i;

            // set up 3 different functors, one of each type
            GoodHashFunctor goodHashFunctor = new GoodHashFunctor();
            MediocreHashFunctor mediocreHashFunctor = new MediocreHashFunctor();
            BadHashFunctor badHashFunctor = new BadHashFunctor();

            // set up 3 tables one using each functor
            ChainingHashTable goodTestTable = new ChainingHashTable(tableSize, goodHashFunctor);
            ChainingHashTable mediocreTestTable = new ChainingHashTable(tableSize, mediocreHashFunctor);
            ChainingHashTable badTestTable = new ChainingHashTable(tableSize, badHashFunctor);

            // get a bunch of words to add to hash table from a file
            ArrayList<String> wordsToHash = readFromFile(new File("/Users/jasonablott/MSD/6012/Week3/Day15/HW7HashTables/src/mobydick.txt"));

            // add the words to the tables
            for (int j = 0; j < numWordsToAdd; j++) {
                goodTestTable.add(wordsToHash.get(j));
                mediocreTestTable.add(wordsToHash.get(j));
                badTestTable.add(wordsToHash.get(j));
            }

            // print out the results of adding the words to the tables to compare effectiveness of functors
            System.out.println("Table Size: " + tableSize);
            System.out.println("Good Hash Results:");
            System.out.println("Number of Collisions: " + goodTestTable.Collisions);
            System.out.println("Lambda of good set: " + goodTestTable.getLambda());
            System.out.println("");
            System.out.println("Mediocre Hash Results:");
            System.out.println("Number of Collisions: " + mediocreTestTable.Collisions);
            System.out.println("Lambda of mediocre set: " + mediocreTestTable.getLambda());
            System.out.println("");
            System.out.println("Bad Hash Results:");
            System.out.println("Number of Collisions: " + badTestTable.Collisions);
            System.out.println("Lambda of bad set: " + badTestTable.getLambda());
            System.out.println("");
        }
    }
    public static void main(String[] args) {
        CollisionCounter.collisionTest(10,100000);
    }
}


