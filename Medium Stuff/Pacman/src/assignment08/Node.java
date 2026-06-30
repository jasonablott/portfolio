package assignment08;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a node, which will be the vertices of a graph.
 * Each node stores variables needed for BFS of the graph
 */
public class Node {


    // Member variables


    // integers to store x and y coordinates of the node(vertex) in the maze(graph)
    int xPos;
    int yPos;
    // string to store the type of the node, i.e. "X". "S", "G", " ", or "."
    String type;
    // boolean to track if the node has been visited in the BFS
    boolean visited;
    // pointer to track the previous node in the BFS
    Node cameFrom;
    // list of nodes representing the up to 4 neighbors of the node. Diagonal doesn't
    // count
    List<Node> neighbors;


    // Constructors


    /**
     * Default constructor that sets a default node with no position data
     */
    Node(){
        visited = false;
        //isKnown = false;
        cameFrom = null;
        xPos = 0;
        yPos = 0;
        neighbors = new ArrayList<Node>();
    }

    /**
     * Constructor that takes the position and type data of the node and sets them
     * @param xPos integer representing the x position of the node in the maze
     * @param yPos integer representing the y position of the node in the maze
     * @param type string representing the type of the node
     */
    Node(int xPos, int yPos, String type){
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
        visited = false;
        cameFrom = null;
        neighbors = new ArrayList<>();
    }


    // Other methods


    /**
     * method to visit a node, setting the visited boolean to true
     */
    public void visit(){
        visited = true;
    }


}
