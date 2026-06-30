package assignment08;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class runs a variety of mazes through the mazeSolver and compares the result with the expected
 * result, failing an assertion any time a row of the output result does not match the corresponding
 * row of the expected result file.
 */
class PathFinderTest {
    @Test
    public void mazeSolverTests() throws FileNotFoundException {

        // test maze with no solution
        testSolveMaze("TestMaze1.txt", "TestMaze1Result.txt", "TestMaze1Expected.txt");

        // test maze with one node as path
        testSolveMaze("TestMaze2.txt", "TestMaze2Result.txt", "TestMaze2Expected.txt");

        // Make sure we can't go diagonally, no solution
        testSolveMaze("TestMaze3.txt", "TestMaze3Result.txt", "TestMaze3Expected.txt");

        // Test short path with one turn
        testSolveMaze("TestMaze4.txt", "TestMaze4Result.txt", "TestMaze4Expected.txt");

        // Test big maze from provided files
        testSolveMaze("bigMaze.txt", "bigMazeResult.txt", "bigMazeSol.txt");

        // test unsolvable from provided files
        testSolveMaze("unsolvable.txt", "unsolvableResult.txt", "unsolvable.txt");

        // test classic from provided files
        testSolveMaze("classic.txt", "classicResult.txt", "classicSol.txt");

        // test medium maze from provided files
        testSolveMaze("mediumMaze.txt", "mediumMazeResult.txt", "mediumMazeSol.txt");

        // Test straight maze from provided files
        testSolveMaze("straight.txt", "straightResult.txt", "straightSol.txt");

        // test tiny maze from provided files
        testSolveMaze("tinyMaze.txt", "tinyMazeResult.txt", "tinyMazeSol.txt");

        // test turn maze from provided files
        testSolveMaze("turn.txt", "turnResult.txt", "turnSol.txt");

        // test demo from provided files *This has more than one correct path so it sometimes fails
        // visual inspection confirms the result path still has the same length of path
        // testSolveMaze("demoMaze.txt", "demoMazeResult.txt", "demoMazeSol.txt");

        // test randomMaze from provided files *This has more than one correct path so it sometimes fails
        // visual inspection confirms the result path still has the same length of path
        //testSolveMaze("randomMaze.txt", "randomMazeResult.txt", "randomMazeSol.txt");

    }

    /**
     * Method to test the PathFinder.solveMaze method on various maze files
     *
     * @param input the unique filename of an input text file representing an unsolved maze
     * @param result the unique filename of an output text file representing a solved maze
     * @param expected the unique filename of a text file representing the expected solution of a solved maze
     */
    private static void testSolveMaze(String input, String result, String expected) {
        // run maze solver
        PathFinder.solveMaze("/Users/jasonablott/MSD/6012/Week4/Day17/HW8Pacman/src/assignment08/" + input, "/Users/jasonablott/MSD/6012/Week4/Day17/HW8Pacman/src/assignment08/" + result);
        // check result is expected by scanning result and expected and checking that contents are equal
        Scanner myResult = null;
        Scanner ExpectedResult = null;
        try {
            myResult = new Scanner(new File("/Users/jasonablott/MSD/6012/Week4/Day17/HW8Pacman/src/assignment08/" + result));
            ExpectedResult = new Scanner(new File("/Users/jasonablott/MSD/6012/Week4/Day17/HW8Pacman/src/assignment08/" + expected));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        // compare contents
        while (myResult.hasNextLine() && ExpectedResult.hasNextLine()) {
            assertEquals(myResult.nextLine(), ExpectedResult.nextLine());
        }
    }
}