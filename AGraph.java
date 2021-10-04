import java.util.Arrays;

public abstract class AGraph<N> {

    NodeIDTable<N> nodeIDs;

    /**
     * array of the nodes
     */
    N[] nodes;
    PathFindingAlgorithm algo;

    /**
     * Creates a graph with vertices only from the specified array of nodes
     * the graph will use the specified <code>PathFindingAlgorithm</code> to calculate the shortest distance
     * from all nodes to all nodes (with the ability to reconstruct the path)
     * @param nodes the array of the node objects
     * @param algorithm the pathfinding algorithm to be used
     */
    AGraph(N[] nodes, PathFindingAlgorithm algorithm){
        this.nodeIDs = new NodeIDTable<>(nodes.length);
        for (int i = 0; i < nodes.length; i++) {
            this.nodeIDs.add(nodes[i],i);
        }
        this.nodes = nodes;
        algo = algorithm;
    }

    //GRAPH METHODS
    abstract IntADT getNeighbours(int node);
    abstract void addEdge(int start, int end, double weight);
    void addEdge(N start, N end, double weight){
        addEdge(nodeIDs.getId(start),nodeIDs.getId(end),weight);
    }
    abstract void removeNode(int node);

    /**
     * Returns the weight of the edge, +infinity, if the edge does not exist, and 0 between the same node
     * @param start beginning node of the edge
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

     N getNodeByIndex(int i){
        return nodes[i];
     }


    //PATH FINDING RESULTS
    void startAlgorithm(){algo.start(this);}
    IntQueue reconstructPath(int start, int end) {
        return algo.reconstructPath(start,end);
    }
    double getDistance(int start, int end){
        return algo.getDistance(start,end);
    }
    double getDistance(N start, N end){
        return getDistance(nodeIDs.getId(start),nodeIDs.getId(end));
    }
}

class AdjMatrixGraph<N> extends AGraph<N>{

    double[][] adjMatrix;

    /**
     * Creates a graph with vertices only from the specified array of nodes
     * the graph will use the specified <code>PathFindingAlgorithm</code> to calculate the shortest distance
     * from all nodes to all nodes (with the ability to reconstruct the path)
     *
     * @param nodes     the array of the node objects
     * @param algorithm the pathfinding algorithm to be used
     */
    AdjMatrixGraph(N[] nodes, PathFindingAlgorithm algorithm) {
        super(nodes, algorithm);

        //init the adjacency matrix and populate with infinities (no edges yet)
        adjMatrix = new double[nodes.length][nodes.length];
        for (double[] matrix : adjMatrix) {
            Arrays.fill(matrix, Double.POSITIVE_INFINITY);
        }
    }

    @Override
    IntADT getNeighbours(int node) {
        double[] neibsArr = adjMatrix[node]; //neighbours in the adj matrix
        IntStack neibs = new IntStack();     //the to be returned structure

        //for all vertices except this one (node), check whether there exists an edge (the weight is less than infinity)
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

class PlexGraph<N> extends AGraph<N>{


    /**
     * Creates a graph with vertices only from the specified array of nodes
     * the graph will use the specified <code>PathFindingAlgorithm</code> to calculate the shortest distance
     * from all nodes to all nodes (with the ability to reconstruct the path)
     *
     * @param nodes     the array of the node objects
     * @param algorithm the pathfinding algorithm to be used
     */
    PlexGraph(N[] nodes, PathFindingAlgorithm algorithm) {
        super(nodes, algorithm);
    }

    @Override
    IntADT getNeighbours(int node) {
        return null;
    }

    @Override
    void addEdge(int start, int end, double weight) {

    }

    @Override
    void removeNode(int node) {

    }

    @Override
    double getWeight(int start, int end) {
        return 0;
    }
}


