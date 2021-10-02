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
    IntADT reconstructPath(int start, int end) throws RuntimeException;

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
}

interface IntADT{
    /**
     * adds the number to the collection
     * @param i the number to add
     */
    void push(int i);

    /**
     * gets the current number
     * @return the current number in the collection
     */
    int pop();
}
class IntStack implements IntADT{


    private IntADTEntry head;

    @Override
    public void push(int i){
        head = new IntADTEntry(i, head);
    }
    @Override
    public int pop(){
        int r = head.val;
        head = head.next;
        return r;
    }
}
class IntQueue implements IntADT{
    private IntADTEntry head;
    private IntADTEntry tail;

    @Override
    public void push(int i) {
        IntADTEntry n = new IntADTEntry(i,tail);
        tail.next = n;
        tail = n;
    }

    @Override
    public int pop() {
        int r = head.val;
        head = head.next;
        return r;
    }
}

class IntADTEntry {
    int val;
    IntADTEntry next;

    public IntADTEntry(int val, IntADTEntry next) {
        this.val = val;
        this.next = next;
    }
}
