import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

@Deprecated
public abstract class AGraph<N> {

    /**
     * Table that converts a node to its ID in the graph
     */
    NodeIDTable<N> nodeIDs;

    /**
     * array of the nodes
     */
    N[] nodes;
    /**
     * The algorithm
     */
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
@Deprecated
class AdjMatrixGraph<N> extends AGraph<N>{

    public double[][] adjMatrix;

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
        IntQueue neibs = new IntQueue();     //the to be returned structure

        //for all vertices except this one (node), check whether there exists an edge (the weight is less than infinity)
        //and if so, add it to the queue of neighbours
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

/**
 * A complete undirected weighted graph with the edge weights being equal to the planar or cartesian
 * distance of two nodes
 */
class MetricsGraph{
    /**
     * The index of paris in the graph's node array
     */
    public final static int PARIS_INDEX = 0,
    /**
     * The index of the plane in the graph's node array
     */
                            PLANE_INDEX = 1;

   final GraphNode[] nodes;
   MinHeap parisClosest; //the priority queue of closest horses to paris
   DistFunction dist;

    /**
     * Creates a complete undirected weighted graph with the edge weights being equal to the planar or cartesian
     * distance of two nodes
     * @param horses the array of the horses
     * @param plane the plane of this graph
     * @param paris paris
     * @param distanceFunction the distance function to be used
     */
    MetricsGraph(Horse[] horses, Aircraft plane, GraphNode paris, DistFunction distanceFunction) {
        this.nodes = new GraphNode[2+horses.length]; //2 = plane + paris
        nodes[0] = paris;
        nodes[1] = plane;
        System.arraycopy(horses, 0, nodes, 2, nodes.length - 2);
        this.dist = distanceFunction;
        parisClosest = new MinHeap(horses.length);

        for (int i = 2; i < nodes.length; i++) {
            parisClosest.push(i,getWeight(0,i));
        }
    }

    /**
     * removes a horse from the graph
     * might use later, dunno
     * @param node the horse's index
     */
    void removeNode(int node) {
        nodes[node] = null;
    }

    /**
     * Gets the distance between nodes
     * @param start one node's index
     * @param end second node's index
     * @return the distance
     * @throws NullPointerException dunno when, might happen someday
     */
    double getWeight(int start, int end) throws NullPointerException{
        return dist.dist(nodes[start], nodes[end]);
    }

    public int getNumOfHorses() {
        return nodes.length-2;
    }

    public Aircraft getAirplane(){
        return  (Aircraft) nodes[PLANE_INDEX];
    }

    /**
     * @return the next closest horse index to paris
     */
    public int getNextClosestToParis(){
        return parisClosest.pop().node();
    }
}

/**
 * A "graph" using the TreeSet collection to quickly find the closest horse to the plane's current location
 */
class TreeSetGraph{
    TreeSet<GraphNode> nodes;
    GraphNode paris;
    Aircraft plane;

    TreeSetGraph(Horse[] horses, Aircraft plane, GraphNode paris, Comparator<GraphNode> comp) {
        nodes = new TreeSet<>(comp);
        for (Horse horse :horses) {
            if(nodes.contains(horse))
                System.out.println(horse.x + " " + horse.y);
            nodes.add(horse);
        }

        this.plane = plane;
        this.paris = paris;
    }

    public Horse closestToPlane(){
        GraphNode closest = nodes.ceiling(plane);
        if(closest==null) closest = nodes.first();
        return (Horse) closest;
    }

    public Aircraft getPlane(){
        return  plane;
    }

    public boolean goneThroughAll(){
        return nodes.isEmpty();
    }
    public void delete(Horse h){
        nodes.remove(h);
    }
}


