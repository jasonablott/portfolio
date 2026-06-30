package assignment08;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a graph, or a collection of nodes/vertices
 */
public class Graph {


    // Member variables


    // integers to store dimensions of the graph
    int Height;
    int Width;
    // set to store nodes of the graph
    Set<Node> vertices;


    // Constructor


    /**
     * Default constructor to create a new, empty graph
     */
    Graph() {
        vertices = new HashSet<>();
    }


    // Other methods


    /**
     * method to add a vertex to the graph
     *
     * @param node a node to add to the vertices of the graph
     */
    public void addVertex(Node node) {
        vertices.add(node);
    }

    /**
     * method to set the dimensions of the graph
     *
     * @param height the height of the graph in number of nodes
     * @param width the width of the graph in number of nodes
     */
    public void setDimensions(int height, int width) {
        Height = height;
        Width = width;
    }


}
