package ex1.src;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents an undirected weighted graph.
 * It support a large number of nodes (over 10^6, with average degree of 10).
 * The implementation is based on a compact and efficient representation using HashMap data structure.
 */
public class WGraph_DS implements weighted_graph, Serializable {
    private HashMap<Integer, node_info> V; // HashMap representation of the graph vertices.
    private HashMap<Integer, HashMap<Integer, Double>> E; // HashMap representation of the graph edges.

    private int nSize, eSize, MC; // node size, edge size and mode count.

    /**
     * Default constructor
     */
    public WGraph_DS(){
        V = new HashMap<>(); // Initialize vertices HashMap.
        E = new HashMap<>(); // Initialize edges HashMap.
        nSize = eSize = MC = 0; // Initialize counter to zero.
    }

    /**
     * Copy constructor
     * Performs a deep copy of this graph..
     * @param graph_ds - the other graph.
     */
    public WGraph_DS(weighted_graph graph_ds){
        if(graph_ds != null) { // null graphs are not accepted.
            V = new HashMap<>(); // Initialize vertices HashMap.
            E = new HashMap<>(); // Initialize edges HashMap.
            nSize = eSize = MC = 0; // Initialize counters to zero.
            for (node_info v : graph_ds.getV()) { // For each node from old graph.
                int key1 = v.getKey(); // Copy it's key.
                addNode(key1); // Create exactly the same node in new the graph.
                for (node_info e : graph_ds.getV(key1)) { // For each neighbor e of v copy the key.
                    int key2 = e.getKey();
                    addNode(key2); // Create exactly the same node in new the graph.
                    connect(key1, key2, graph_ds.getEdge(key1, key2)); // If key1 and key2 were connected in the old graph, then connect them in the new graph.
                }
            }
        }
    }

    /**
     * Returns the node with the specified key or null if none.
     * @param key - an integer key greater or equal to zero.
     * @return - node_info object's pointer.
     */
    @Override
    public node_info getNode(int key) {
        return this.V.get(key);
    }

    /**
     * Checks if there is an edged between node1 to node2.
     * @param node1 - first node key.
     * @param node2 - second node key.
     * @return true/false depending on if the edge exist or not.
     */
    @Override
    public boolean hasEdge(int node1, int node2) {
        node_info n1 = V.get(node1), n2 = V.get(node2); // get node1 object pointer.
        if(n1 == null || n2 == null) // If it's null. means the node doesn't exist.
            return false;
        if(node1 == node2) // Every node is connected to itself (trivial), means there is no such edge.
            return false;
       return E.get(node1).get(node2) != null; // Should return true if the weight between this nodes exist.
    }

    /**
     * Returns the weight of the edged which connects node1 to node2.
     * @param node1 - first node key.
     * @param node2 - second node key.
     * @return - double value which represents the weight between node1 to node2.
     */
    @Override
    public double getEdge(int node1, int node2) {
        if(hasEdge(node1,node2)) { // First check if there is an edge from node1 to node2.
            if(node1 == node2) // The weight from node to itself is 0.
                return 0;
            return E.get(node1).get(node2); // Finally return the weight.
        }
        return -1; // If there is no edge like this, return -1.
    }

    /**
     * Adds a new node to the graph with the specified key.
     * @param key - the key to insert.
     */
    @Override
    public void addNode(int key) {
        if(key < 0 || getNode(key) != null) // If key is negative or the node is already exist, simply do nothing.
            return;
        V.put(key, new NodeInfo(key)); // Put key to the vertices HashMap.
        E.put(key,new HashMap<>()); // Put key to edges HashMap.
        nSize++; // Count node addition (+1).
        MC++; // Count modification (+1).
    }

    /**
     * Connects two vertices from the graph with a given weight.
     * @param node1 - first node.
     * @param node2 - second node.
     * @param w - desired weight.
     */
    @Override
    public void connect(int node1, int node2, double w) {
        if(w < 0.0 || (node1 == node2)) // Negative weights are illegal. Also no need to update if node1 == node2.
            return;
        if (hasEdge(node1,node2)){ // If the edge is already exist, then only need to update weight.
            E.get(node1).put(node2,w);
            MC++;
        }else { // If the edge is not already exist.
            node_info n1,n2;
            if ((n1 = V.get(node1)) != null && (n2 = V.get(node2)) != null) { // If both vertices exist.
                E.get(node1).put(node2, w); // Put node2 as a neighbor of node1.
                E.get(node2).put(node1, w); // Put node1 as a neighbor of node2.
                eSize++; // Count edge size (+1).
                MC++; // Count modification (+1).
            }
        }
    }

    /**
     * Returns the collection of vertices.
     * @return - the collection of all vertices in the graph.
     */
    @Override
    public Collection<node_info> getV() {
        return V.values(); // use HashMap's built in values() function. Time complexity O(1).
    }

    /**
     * Returns the collection of neighbors related to the specified node (key).
     * @param node_id - node key.
     * @return - a collection representing the neighbors of the specified node (key).
     * This method runs in time complexity of O(K), where k is the degree of the neighbors of node_id.
     */
    @Override
    public Collection<node_info> getV(int node_id) {
        HashMap<Integer,Double> neighbors; // pointer to the inner HashMap of edges HashMap.
        Collection<node_info> collection = new LinkedHashSet<>(); // The collection to be returned.
        if((neighbors = E.get(node_id)) != null) // If node_id has neighbors at all.
        for(Integer key : neighbors.keySet()){ // Get all neighbors keys
            collection.add(getNode(key)); // Add the nodes associated with these keys to the collection.
        }
        return collection; // Finally return thr collection.
    }

    /**
     * Removes the node with the specified key.
     * @param key - the node (key) to remove.
     * @return - the removed node pointer or null if it's not exist.
     */
    @Override
    public node_info removeNode(int key) {
        node_info toRemove = getNode(key);
        if(toRemove != null){ // If there is node to remove.
            int removedEdges = getV(key).size(); // How many edges to be removed (used for edge count tracking).
            for(node_info n : getV(key)){ // For each neighbor of the specified node (key).
                E.get(n.getKey()).remove(key); // Remove the specified node from the neighbors list of the neighbor.
            }
            E.remove(key); // Finally remove the specified node from edges HashMap.
            V.remove(key); // Finally remove the specified node from vertices HashMap.
            nSize--; // Update the node size.
            eSize -= removedEdges; // Update edge size.
            MC++; // Update mode count.
        }
        return toRemove; // Return a pointer to the removed object.
    }

    /**
     * Removes the edge between node1 to node2.
     * @param node1 - first node.
     * @param node2 - second node.
     */
    @Override
    public void removeEdge(int node1, int node2) {
        if(hasEdge(node1,node2)){ // If the edge between these two exist.
            if(node1 == node2) // If node1 and node2 are the same. no need to do anything.
                return;
            E.get(node1).remove(node2); // Remove the edge data between node1 to node2.
            E.get(node2).remove(node1); // Remove the edge data between node2 to node1.
            eSize--; // Update edge size.
            MC++; // Update mode count.
        }
    }

    /**
     * returns the current number of nodes.
     * @return - the number of nodes in this graph.
     */
    @Override
    public int nodeSize() {
        return nSize;
    }

    /**
     * returns the current number of edges.
     * @return - the number of edges in this graph.
     */
    @Override
    public int edgeSize() {
        return eSize;
    }

    /**
     * returns the current mode count.
     * @return - the number of modifications that was performed on this graph.
     */
    @Override
    public int getMC() {
        return MC;
    }

    @Override
    public String toString() {
        String s = "\n";
        for(node_info n : getV()){
            s += "("+n.getKey()+") -> " + getV(n.getKey()).toString() + "\n";
        }
        return s;
    }

    /**
     * This class represents a single vertex of an undirected weighted graph.
     */
    private class NodeInfo implements node_info, Serializable{

        private int key; // Node's key
        private String info; // Used for coloring purposes (by algorithms).
        private double tag; // Used to store temporal weight.

        /**
         * Default constructor.
         * @param key
         */
        public NodeInfo(int key){
            this.key = key;
            this.info = "";
            this.tag = 0.0;
        }

        /**
         * Returns the key of this node.
         * @return - int key.
         */
        @Override
        public int getKey() {
            return this.key;
        }

        @Override
        public String getInfo() {
            return this.info;
        }

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        @Override
        public double getTag() {
            return this.tag;
        }

        @Override
        public void setTag(double t) {
            this.tag = t;
        }

        @Override
        public String toString() {
            return ""+getKey();
        }

        /**
         * Override of equals method.
         * The equality is determined by keys.
         * @param o - the object to compare with this node.
         * @return - true/false - depending on equality.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeInfo nodeInfo = (NodeInfo) o;
            return key == nodeInfo.key;
        }

        /**
         * Override of the default hashCode method for node.
         * This hash method applies hash function only on the key.
         * @return - the hash sum.
         */
        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    /**
     * Override of equals method.
     * The equality is determined by graph variables (except MC).
     * @param o - the object to compare with this graph.
     * @return - true/false - depending on equality.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WGraph_DS wGraph_ds = (WGraph_DS) o;
        return nSize == wGraph_ds.nSize &&
                eSize == wGraph_ds.eSize &&
                Objects.equals(V, wGraph_ds.V) &&
                Objects.equals(E, wGraph_ds.E);
    }

    /**
     * Override of the default hashCode method for graph.
     * This hash method applies hash function only on the key.
     * @return - the hash sum of all variables (except MC).
     */
    @Override
    public int hashCode() {
        return Objects.hash(V, E, nSize, eSize);
    }

    /**
     * This class implements the Comparator interface.
     * It is used mainly by the dijkstra algorithm to compare
     * nodes by weight.
     */
    public static class CompareByWeight implements Comparator<node_info> {
        /**
         * Override of compare method (part of the Comparator interface).
         * The comparison result depends on the temporal tag of the node (temporal weight).
         * @param o1 - first node
         * @param o2 - second node
         * @return - -1,0,1 depending on if o1 is less, equal or greater than o2.
         */
        @Override
        public int compare(node_info o1, node_info o2) {
            return Double.compare(o1.getTag(),o2.getTag());
        }
    }
}
