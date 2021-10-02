import java.util.Arrays;

public abstract class AGraph<N> {

   // NodeIDTable<N> nodeIDs; TODO: UNCOMMENT THIS ONLY IF THE ID TABLE IS NECESSARY (see the end of this .java file)

    /**
     * array of the nodes
     */
    N[] nodes;
    PathFindingAlgorithm algo;

    /**
     * Creates a graph with verteces only from the specified array of nodes
     * the graph will use the specified <code>PathFindingAlgoritm</code> to calculate the shortest distance
     * from all nodes to all nodes (with the ability to reconstruct the path)
     * @param nodes the array of the node objects
     * @param algorithm the pathfinding algorithm to be used
     */
    AGraph(N[] nodes, PathFindingAlgorithm algorithm){
        /* TODO: UNCOMMENT THIS ONLY IF THE ID TABLE IS NECESSARY
        this.nodeIDs = new NodeIDTable<N>(nodes.length);
        for (int i = 0; i < nodes.length; i++) {
            this.nodeIDs.add(nodes[i],i);
        }
        */
        this.nodes = nodes;
        algo = algorithm;
    }

    //GRAPH METHODS
    abstract IntADT getNeighbours(int node);
    abstract void addEdge(int start, int end, double weight);
    abstract void removeNode(int node);

    /**
     * Returns the weight of the edge, +infinity, if the edge does not exist, and 0 between the same node
     * @param start begginig node of the edge
     * @param end   ending node of the edge
     * @return      weight of the edge (start, end), infinity (edge does not exist) or 0 if start==end
     */
    abstract double getWeight(int start, int end);

    /**
     * @return number of vertices in the graph
     */
    int getNumOfVertices(){
         return nodes.length;
     }


    //PATH FINDING RESULTS
    void startAlgoritm(){algo.start(this);}
    IntADT reconstructPath(int start, int end) { //TODO: pick & implement better container than an array
        return algo.reconstructPath(start,end);
    }
    double getDistance(int start, int end){
        return algo.getDistance(start,end);
    }

    //TABLE USAGE
    //TODO: fill in the methods, only if the ID table is necessary
}

class AdjMatrixGraph<N> extends AGraph<N>{

    double[][] adjMatrix;

    /**
     * Creates a graph with verteces only from the specified array of nodes
     * the graph will use the specified <code>PathFindingAlgoritm</code> to calculate the shortest distance
     * from all nodes to all nodes (with the ability to reconstruct the path)
     *
     * @param nodes     the array of the node objects
     * @param algorithm the pathfinding algorithm to be used
     */
    AdjMatrixGraph(N[] nodes, PathFindingAlgorithm algorithm) {
        super(nodes, algorithm);

        //init the adjacency matrix and populete with infinities (no edges yet)
        adjMatrix = new double[nodes.length][nodes.length];
        for (double[] matrix : adjMatrix) {
            Arrays.fill(matrix, Double.POSITIVE_INFINITY);
        }
    }

    @Override
    IntADT getNeighbours(int node) {
        double[] neibsArr = adjMatrix[node]; //neighbours in the adj matrix
        IntStack neibs = new IntStack();     //the to be returned structure

        //for all verteces except this one (node), check whether there exists an egde (the weight is less than infinity)
        //and if so, add it to the stack of neighbours
        for (int i = 0; i < neibsArr.length; i++) {
            if(i!=node && neibsArr[i]<Double.POSITIVE_INFINITY)
                neibs.push(i);
        }

        return neibs;
    }

    @Override
    void addEdge(int start, int end, double weight) {
        adjMatrix[start][end] = weight;
    }

    @Override
    void removeNode(int node) {
        //simply set weight of all incident edges to inf (remove the edges)
        //the node is still there, but there's no way how to get to it
        for (int i = 0; i < nodes.length; i++) {
            adjMatrix[node][i] = Double.POSITIVE_INFINITY;
            adjMatrix[i][node] = Double.POSITIVE_INFINITY;
        }
        //generally, we would have to recalculate the shortest paths in case this node
        //was in one of the shortest paths
        //but since this application only considers Euclidean space and the graph is a full graph,
        //there are no paths with middle points
    }

    @Override
    double getWeight(int start, int end) {
        return adjMatrix[start][end];
    }
}

/* TODO: UNCOMMENT THIS ONLY IF THE ID TABLE IS NECESSARY

/**
 * A HashTable for a graph that returns the number of the specified node object in the graph
 * graphs only see vertices as numbers -> from that number, we must quickly get the node object
 *
 * Node -> Node number in graph
 *
 * @param <N> the type of the node object

class NodeIDTable<N>{
     static class TableEntry<T>{
        T node;  //key
        int id;  //value
        TableEntry<T> next;

        public TableEntry(T node, int id) {
            this.node = node;
            this.id = id;
        }
    }

    TableEntry<N>[] table;

    NodeIDTable(int size){
        table = new TableEntry[size];
    }

    void add(N node, int id){
        TableEntry<N> n = new TableEntry(node, id);
        int poz = n.hashCode()%table.length;
        n.next = table[poz];
        table[poz] = n;
    }
    int getId(N node){
        int poz = node.hashCode()%table.length;
        TableEntry<N> t = table[poz];

        while(t!=null){
            if(t.node.equals(node)) return t.id;
            t=t.next;
        }
        throw new RuntimeException("No such node in graph");

    }


}

*/
