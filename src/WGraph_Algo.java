package ex1.src;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class represents an Undirected (positive) Weighted Graph Theory algorithms including:
 * 0. clone(); (copy)
 * 1. init(graph);
 * 2. isConnected();
 * 3. double shortestPathDist(int src, int dest);
 * 4. List<node_data> shortestPath(int src, int dest);
 * 5. Save(file);
 * 6. Load(file);
 *
 */
public class WGraph_Algo implements weighted_graph_algorithms{
    /**
     * The graph on which this set of algorithms will operate on.
     */
    private weighted_graph g;

    /**
     * Default constructor
     */
    public WGraph_Algo(){
        this.g = new WGraph_DS();
    }

    /**
     * Initialize this set of algorithms on the given graph g.
     * @param g - the given graph.
     */
    @Override
    public void init(weighted_graph g) {
        this.g = g;
    }

    /**
     * Returns the graph on which this set of algorithm operates on.
     * @return
     */
    @Override
    public weighted_graph getGraph() {
        return this.g;
    }

    /**
     * Performs a deep copy of this graph.
     * @return - a new Copy of this graph.
     */
    @Override
    public weighted_graph copy() {
        return new WGraph_DS(g); // Using internal copy constructor of the graph.
    }

    /**
     * Checks if this graph is connected.
     * @return true/false depending on if it's connected or not.
     */
    @Override
    public boolean isConnected() {
        if(g.getV().isEmpty()) // If it's an empty graph return true.
            return true;
        node_info startKey = g.getV().iterator().next(); // Get the first key.
        BFS(startKey); // Apply BFS traversal on startKey node.
        for(node_info n : g.getV()) { // For each vertex
            if (n.getInfo().equals("WHITE")) // If vertex color is WHITE it means that this vertex is unreachable (not connected).
                return false;
        }
        return true;
    }

    /**
     * Calculates the shortest path from src node to dest node.
     * @param src - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        node_info n1 = g.getNode(src), n2 = g.getNode(dest);
        if(n1 != null && n2 != null) { // If both src and dest exist.
            if(n1 == n2) // If it's the same node return zero distance.
                return 0;
            dijkstra(n1, n2); // Perform dijkstra algorithm.
            return n2.getTag(); // After dijkstra the shortest path weight will be stored in the destination node.
        }else {
            return -1; // If one or both of the nodes are null, it means there is not path between these nodes.
        }
    }

    /**
     * returns the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * @param src - start node
     * @param dest - end (target) node
     * @return - a List representation of the shortest path from src node to dest node.
     */
    @Override
    public List<node_info> shortestPath(int src, int dest) {
        node_info n1 = g.getNode(src), n2 = g.getNode(dest);
        LinkedList<node_info> list = new LinkedList<>(); // Empty list which will contain the shortest path.
        if(n1 != null && n2 != null) { // If both src and dest exist.
            if (n1 == n2) { // If it's the same node then it will be the only node in the returned list.
                list.add(n1); // add src nodes to the list.
                return list;
            }
            HashMap<Integer, Integer> parentMap = dijkstra(g.getNode(src), g.getNode(dest)); // Perform dijkstra.
            node_info parent = n2;
            while (parent.getKey() != src) { // get all parents list from dest to src.
                list.addFirst(parent); // Add this nodes in reverse order to the list.
                parent = g.getNode(parentMap.get(parent.getKey())); // Update the next parent.
            }
            list.addFirst(g.getNode(src)); // Finally add the src node to the beginning of list.
            return list; // Return the list containing the path.
        }else {
            return null;
        }
    }

    /**
     * Dijkstra shortest path algorithm implementation using minimum priority queue data structure.
     * This algorithm works on non negative undirected weighted graphs.
     * This algorithm implemented according to the pseudo code int the following video.
     * https://www.coursera.org/lecture/advanced-data-structures/core-dijkstras-algorithm-2ctyF
     * @param src - source node.
     * @param dest - destination node.
     * @return A HashMap which represents parents hierarchy.
     */
    private HashMap<Integer,Integer> dijkstra(node_info src, node_info dest){
        HashMap<Integer,Integer> parentMap = new HashMap<>(); // Parents hierarchy.
        PriorityQueue<node_info> pq = new PriorityQueue<>(16,new WGraph_DS.CompareByWeight()); // Create a minimum priority queue with comparator (by minimum weight).
        for(node_info n : g.getV()){
            n.setTag(Double.MAX_VALUE); // Initialise all weight to infinity.
            n.setInfo("WHITE"); // Mark all as not visited.
        }
        src.setTag(0.0); // Set src node weight to 0 (the weight of a node to himself is 0).
        pq.add(src); // Add src node to the priority queue.
        while (!pq.isEmpty()){ // While the is nodes to visit.
            node_info current = pq.poll(); // remove the node with the minimal weight. O(Log(n)) operation.
            if(current.getInfo().equals("WHITE")) // If the node is not visited.
                current.setInfo("GRAY"); // Mark the node as visited.
            if(current.getKey() == dest.getKey()) { // If with found the destination node. no need to continue searching.
                return parentMap; // At this point we have all the information we need. Complete function by return operation.
            }
            for(node_info neighbor : g.getV(current.getKey())){ // For all neighbors of current node.
                if(neighbor.getInfo().equals("WHITE")) { // If neighbor is not already. visited
                    double pathDist = current.getTag() + g.getEdge(current.getKey(), neighbor.getKey()); // Calculate the distance between current node to it's neighbor.
                    if (Double.compare(pathDist, neighbor.getTag()) == -1) { // If the distance is less than the current neighbor distance, then need to update it.
                        neighbor.setTag(pathDist); // Update neighbors distance to the new smallest distance.
                        parentMap.put(neighbor.getKey(),current.getKey()); // Set current node as parent of this neighbor.
                        pq.add(neighbor); // Add the neighbor to the priority queue for further checks.
                    }
                }
            }
        }
        return null; // If we get here, it means there is no such path.
    }

    /**
     * BFS algorithm helper function.
     * The algorithm is built according to OOP lecture 2 pseudo code (Elizabet's class).
     * This algorithm is used for isConnected().
     * @param src The source node.
     */
    private void BFS(node_info src){
        for (node_info u : g.getV()){ // For each vertex:
            u.setInfo("WHITE"); // Mark all nodes in WHITE (unvisited).
        }
        src.setInfo("GRAY"); // Set src node as visited.
        LinkedList<node_info> Q = new LinkedList<>(); // Create new LinkedList which will represent a queue data structure.
        Q.push(src); // Push src node to to the queue (FIFO principal).
        while (!Q.isEmpty()){ // While there is still nodes to treat.
            node_info u = Q.pop(); // Pop next node from queue (First time it will be src node).
            for(node_info v : g.getV(u.getKey())){ // For each neighbor v of u.
                if(v.getInfo().equals("WHITE")){ // It it's not already visited.
                    v.setInfo("GRAY"); // Set color to Gray (means visited).
                    Q.push(v); // Add neighbor to queue to continue exploring it's neighbors.
                }
            }
            u.setInfo("BLACK"); // Set parent node to black (means "Done").
        }
    }

    /**
     * Saves the current graph to a new file (with path).
     * @param file - the file name (may include a relative path).
     * @return - true/false according to success/fail.
     */
    @Override
    public boolean save(String file) {
        try {
            FileOutputStream fos = new FileOutputStream(file); // Open file output stream object with the given path.
            ObjectOutputStream oos = new ObjectOutputStream(fos); // Create object output stream.
            oos.writeObject(g); // write this graph's object to file.
            oos.close(); // Close the object output stream.
            fos.close(); // Close the file output stream.
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Loads a graph from a given file and initialize it.
     * @param file - file name.
     * @return - true/false according the success/fail.
     */
    @Override
    public boolean load(String file) {
        File f;
        if( (f = new File(file)).exists()) { // If file exist.
            weighted_graph graphFromFile;
            try {
                FileInputStream fis = new FileInputStream(f); // Open a file input stream from a given file path.
                ObjectInputStream ois = new ObjectInputStream(fis); // Create object input stream.
                graphFromFile = new WGraph_DS((WGraph_DS) ois.readObject()); // Create a deep copy of the loaded graph.
                ois.close(); // Close the object input stream.
                fis.close(); // Close the file input stream.
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                return false;
            }
            init(graphFromFile);
            return true;
        }
        return false;
    }
}
