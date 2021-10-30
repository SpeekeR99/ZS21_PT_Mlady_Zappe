import java.util.function.Predicate;

@Deprecated
public interface PathFindingAlgorithm {

    /**
     * calculates the shortest paths from all nodes to all nodes
     * stores the data in an inner structure
     * the data can be accessed via other methods of this interface
     * @param graph the graph on which to start the algorithm (passed in the graph constructor)
     */
    void start(AGraph<?> graph);

    /**
     * Returns a queue/stack of the vertices on the shortest path.
     * to get the vertices in order, call <code>.pop()</code> on the returned collection, until <code>.pop()</code> returns null
     * @param start the starting vertex
     * @param end   the end vertex
     * @return      queue or stack of vertices on the path
     * @throws RuntimeException if the path does not exist (cannot reach <code>end</code> from <code>start</code>
     */
    IntQueue reconstructPath(int start, int end) throws RuntimeException;

    /**
     * Returns the distance between two vertices
     * may return positive infinity, if the path between the vertices does not exist
     * will return 0 for a pair of the same vertices (<code>start==end</code>)
     * @param start the starting vertex
     * @param end   the ending vertex
     * @return distance between start and end
     */
    double getDistance(int start,int end);
}
@Deprecated
class FloydWarshall implements PathFindingAlgorithm{

    private double[][] dist;
    private int[][] next;

    @Override
    public void start(AGraph<?> graph) {
        int N = graph.getNumOfVertices();
        //dist = distance matrix, next = for path reconstruction
        dist = new double[N][N];
        next = new int[N][N];

        //Matrices init
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                dist[i][j] = graph.getWeight(i,j);
                next[i][j] = -1;
            }
            next[i][i] = i;
        }

        //F-W algo
        FW_algo(N);
        //dist[i][j]: len of shortest path from i to j
        //next[i][j]: the vertex k, through which leads the shortest path from i to j
    }

    @Override
    public IntQueue reconstructPath(int start, int end) throws RuntimeException {
        RuntimeException noPath = new RuntimeException("Path does not exist");
        IntQueue path = new IntQueue();
        if(next[start][end]==-1) throw noPath;

        for(;start!=end;start=next[start][end]){
            if(start==-1) throw noPath;
            path.push(start);
        }

        if(next[start][end]==-1) throw noPath;
        path.push(end);
        return path;
    }

    @Override
    public double getDistance(int start, int end) {
        return dist[start][end];
    }

    private void FW_algo(int N){
        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
    }

}


class ClosestNeighbourPath{

    int[][] closest;
    boolean[] visited;
    final int PLANE = MetricsGraph.PLANE_INDEX, PARIS = MetricsGraph.PARIS_INDEX, HORSE_OFFSET = 2;
    IntQueue path;

    @Deprecated
    public void start(MetricsGraph graph) {
        //----------------prep
        int n = graph.getNumOfHorses();
        path = new IntQueue();
        visited = new boolean[n]; //the index i of this array refers to the (i+2)th node in the graph array!
        closest = new int[n][]; //the first index i of this matrix refers to the (i+2)th node in the graph array!
        int closest = 0;

        //find closest horse to plane
        for (int i = 3; i < n; i++)
            closest = graph.getWeight(PLANE,closest+2) > graph.getWeight(PLANE,i) ?
                    i :
                    closest;


        final int NUM_OF_CLOSEST = 5;
        IntQueue neighbours;
        double min, dist;
        int curMin;
        int[] prevMins = new int[NUM_OF_CLOSEST];

        //populate neighbours
        for (int i = 0; i < n; i++) {
            //this.closest[i] = new int[NUM_OF_CLOSEST];
            for (int j = 0; j < NUM_OF_CLOSEST; j++) {
                prevMins[j] = -1;
            }

            for (int j = 0; j < NUM_OF_CLOSEST; j++) {
                min = Double.MAX_VALUE;
                curMin = -1;
                for (int k = 0; k < n; k++) {
                    if(i==k) continue;
                    for (int prevMin :prevMins) {
                        if(k==prevMin) continue;
                    }
                    dist = graph.getWeight(i+2,k+2); //i+2 and k+2 are indexes of the horses in the graph
                    if(dist<min){
                        min = dist;
                        curMin = k;
                    }
                }
                prevMins[j] = curMin;
            }

            this.closest[i] = prevMins;
        }

        //---------------the algorithm
        //TODO: make this class only preapare the this.closest array and let something else do the following (which will include masses)
        int curNode = closest;
        path.push(closest+2); //+2 to get the index of the horse in the graph's node array
        visited[closest] = true;
        int next = 0;
        int goneThrough = 1; //already "gone through" the first node

        //we gotta go through all nodes
        while(goneThrough<n){
            closest = this.closest[curNode][next];
            if(visited[closest]){
                //if already visited, get another closest
                next++;
                if(next>=this.closest[curNode].length){
                    //all closest neighbours already visited, continue to a random unvisited node
                    for (curNode = 0; visited[curNode]; curNode++)
                        ;
                    closest = curNode;
                }
                else continue;
            }
            path.push(closest+2); //+2 to get the index of the horse in the graph's node array
            visited[closest] = true;
            curNode = closest;
            next = 0;
            ++goneThrough;
        }
    }
    @Deprecated
    public IntQueue getPath() throws RuntimeException {
        return path;
    }

    /**
     * Finds the closest horse to the plane in the graph
     * this is the same as calling <code>findClosestHorseToNode(graph,MetricsGraph.PLANE_INDEX)</code>
     * @param graph the graph
     * @return index of the closest horse to the node, or -1 if no horse
     */
    public int findNextClosestHorse(MetricsGraph graph){
        return findClosestHorseToNode(graph,PLANE);
    }


    /**
     * Finds the closest Horse meeting some requirements (see below) in the graph to the node on the specified index in the graph
     * note that horses start at index = 2!
     *
     * the requirements: the plane can load it (capacity-wise)<br>
     *                   the horse is at most 1000 units far <br>
     *                   the horse has not yet been visited
     *
     * @param graph the graph
     * @param node the node index in the graph's node array
     * @return index of the closest horse to the node, or -1 if no horse
     */
    public int findClosestHorseToNode(MetricsGraph graph, int node){
        double dist, minDist = Double.MAX_VALUE;
        int closest = -1;

        for (int i = HORSE_OFFSET; i < graph.getNumOfHorses()+HORSE_OFFSET; i++) {
            if(i==node) continue;
            dist = graph.getWeight(node,i);
            if(dist<minDist && canGetHorse(graph,i)){
                minDist = dist;
                closest = i;
            }
        }
        return closest;
    }

    /**
     * All the conditions for a plane to be able to get the horse
     * @param graph the graph
     * @param horseIndex the checked horse
     * @return true, if the horse is a good candidate
     */
    private boolean canGetHorse(MetricsGraph graph, int horseIndex){
        Aircraft plane = graph.getAirplane();
        Horse horse = graph.getHorse(horseIndex);
        double MAX_DIST = 1000.0;

        return (
                (!graph.isVisited(horseIndex)) &&
                plane.canLoadHorse(horse.weight) //&&
                //graph.getWeight(MetricsGraph.PLANE_INDEX,horseIndex)<MAX_DIST
        );
    }
}
