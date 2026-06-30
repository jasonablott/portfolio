package assignment08;

import java.io.*;
import java.util.*;

/**
 * This class contains methods required to take a text file representing an unsolved pacman
 * maze, construct a graph of nodes, perform a breadth first search of the graph to find the
 * shortest path from start to goal (if there is one), and return a new text file representing
 * the solved pacman maze.
 *
 * It is assumed that all input mazes will be rectangular. All border positions around the
 * perimeter of the grid will be walls.
 *
 */
public class PathFinder {


    // main method


    /**
     * read a maze from a file with the given input name, and output the solved maze
     * to a file with the given output name. This method uses a graph and graph pathfinding
     * to solve the problem.
     *
     * @param inputFile a text file representing a maze. Assumed to be rectangular, and all
     *                  the border positions around the perimeter of the grid will be walls
     * @param outputFile a text file representing a solved maze.
     */
    public static void solveMaze(String inputFile, String outputFile){
        // read input file into a graph
        Graph unsolvedMaze = readIntoGraph(inputFile);
        // perform BFS to find the shortest path to goal
        Graph solvedMaze = BFS(unsolvedMaze);
        // create and return output file of solved maze
        writeToOutput(solvedMaze, outputFile);
    }


    // Helper methods


    /**
     * Method to read a text file representing an unsolved maze into a graph.
     * The first line in the input file contains two numbers, separated by a space. The
     * first number is the height of the field, and the second is the width. The rest of
     * the lines contain the layout of the field.
     *
     * @param inputFile a string representing an input file which is a text file representing
     *                  an unsolved maze.
     * @return Graph, or collection of nodes, representing the unsolved maze read in from
     *                  the input file.
     */
    private static Graph readIntoGraph(String inputFile) {
        // make a graph to add nodes/vertices to
        Graph graph = new Graph();
        // get scanner to read from unsolved maze txt file
        try (Scanner fileInput = new Scanner( new File(inputFile))) {
            // read dimensions from first line
            String[] dimensions = fileInput.nextLine().split(" ");
            // extract height
            int height = Integer.parseInt(dimensions[0]);
            // extract width
            int width = Integer.parseInt(dimensions[1]);
            graph.setDimensions(height, width);
            // traverse the rest of the txt file to populate the graph
            for (int y = 0; y < height; y++) {
                // iterate through each row to height
                String row = fileInput.nextLine();
                // iterate through elements in the row
                for (int x = 0; x < width; x++) {
                    // create node with position and type based on iterations and string in the file at location
                    char item = row.charAt(x);
                    String itemChar = Character.toString(item);
                    Node newNode = new Node(x, y, itemChar);
                    // add the node to the graph
                    graph.addVertex(newNode);
                    // add as a neighbor to its neighbors
                    for (Node n : graph.vertices){
                        if (n != newNode){
                            if ((n.yPos == y-1 && n.xPos == x) || (n.yPos == y+1 && n.xPos == x) ||
                                (n.yPos == y && n.xPos == x-1) || (n.yPos == y && n.xPos == x+1)){
                                n.neighbors.add(newNode);
                                newNode.neighbors.add(n);
                            }
                        }
                    }
                }
            }
        // handle scanner error
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Error:" + e.getMessage());
        }
        return graph;
    }


    /**
     * Method to perform a breadth first search of the graph representing the unsolved
     * maze. The BFS will search for an open path to the goal node from the start node.
     * If the goal can be reached via empty nodes, the path to get there is traced back
     * using the cameFrom attribute of the nodes in the path, and the node types along
     * the path are changed from empty to "." to represent the path.
     *
     * @param unsolvedMaze a graph representing an unsolved pacman maze
     * @return a graph representing a solved pacman maze
     */
    private static Graph BFS(Graph unsolvedMaze) {
        // get start and goal from graph
        Node start = null;
        Node goal = null;
        for (Node n : unsolvedMaze.vertices){
            if (Objects.equals(n.type, "S")){
                start = n;
            }
            if (Objects.equals(n.type, "G")){
                goal = n;
            }
        }
        // make a queue to use while searching
        Queue<Node> queue = new LinkedList<>();
        // if there is no start maze is unsolvable
        if (start == null){
            return unsolvedMaze;
        }
        // perform bfs from start to goal
        start.visit();
        queue.add(start);
        while (!queue.isEmpty()){
            Node current = queue.remove();
            if (current == goal){
                // goal is found, reconstruct path from goal back to start
                List<Node> path = new ArrayList<>();
                // move back one as goal isn't part of the path
                current = current.cameFrom;
                // add the rest of the path
                while (current != null && current != start && Objects.equals(current.type, " ")){
                    path.add(current);
                    current = current.cameFrom;
                }
                // change item types in path
                for (Node n : path){
                    n.type = ".";
                }
            }
            // traverse graph breadth first if goal not yet found
            for (Node neighbor : current.neighbors){
                if (!neighbor.visited && !Objects.equals(neighbor.type, "X")){
                    neighbor.visit();
                    neighbor.cameFrom = current;
                    queue.add(neighbor);
                }
            }
        }
        // return the solved graph
        return unsolvedMaze;
    }


    /**
     * Method to write a graph representing a solved maze to a text file. Outputs the height and
     * width at the top, followed by the characters representing the maze. There is a single newline
     * character after the last 'X' in the output. If there is no path from 'S' to 'G', print the
     * original maze.
     *
     * @param solvedMaze a graph representing a solved pacman maze
     * @param outputFile a string representing an output file which is a text file representing
     *                  a solved maze.
     */
    private static void writeToOutput(Graph solvedMaze, String outputFile) {
        // get height and width from graph
        int height = solvedMaze.Height;
        int width = solvedMaze.Width;
        // write out the height and width
        try(PrintWriter output = new PrintWriter(new FileWriter(outputFile))) {
            output.println(height + " " + width);
            // make a 2d array to store types for printing to file
            String[][] maze = new String[height][width];
            // traverse the graph nodes to populate the 2d array
            for (Node n : solvedMaze.vertices) {
                int x = n.xPos;
                int y = n.yPos;
                maze[y][x] = n.type;
            }
            // iterate through each row and column of array and print lines
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    output.print(maze[y][x]);
                }
                // advance to next line
                output.println();
            }
        } catch (IOException e) {
             System.out.println("An IO error occurred: " + e.getMessage());
        }

    }


}
