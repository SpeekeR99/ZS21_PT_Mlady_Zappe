import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

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
    /**
     * All the nodes: Paris, Plane and horses
      */
   final GraphNode[] nodes;
    /**
     * Priority Queue for closest horses to paris
     */
   MinHeap parisClosest; //the priority queue of closest horses to paris
    /**
     * The function used to calculate the distance
     */
   DistFunction dist;
    /**
     * Array for keeping track of already visited nodes
     */
   boolean[] visited;
    /**
     * How many horses already visited
     */
   int visitedNum,
    /**
     * Index of the node the Airplane is currently at<br>
     * can be: Horse Index, Paris index (0) or Plane index (1) at the beginning
     */
            currentlyAt;
    /**
     * The Queue of horse indices, that are currently on the plane
      */
   IntQueue loaded;
    /**
     * The current time
     */
   double time;

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

        visited = new boolean[horses.length+2];
        visited[0] = visited[1] = true; //plane and paris "already visited"
        loaded = new IntQueue();
        visitedNum = 0;
        currentlyAt = PLANE_INDEX;
        time = 0.0;
    }

    /**
     * Gets the distance between nodes
     * Note that horses start at index = 2! (excl. mark not factorial)
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
    public GraphNode getParis(){
        return nodes[PARIS_INDEX];
    }

    /**
     * @return the next closest horse index to paris
     */
    public int getNextClosestToParis(){
        int closest;
        do{
            closest = parisClosest.pop().node();
        }while(visited[closest]);
        return closest;
    }

    /**
     * Returns the horse at the given index<br>
     * note that horses start at index = 2<br>
     * @param index index of the horse
     * @return horse at the given index
     */
    public Horse getHorse(int index){
        //index+=2; //Horses start at index 2
        return (Horse) nodes[index];
    }

    public boolean isVisited(int i){
        return visited[i];
    }

    /**
     * Returns whether all horses are visited
     * @return true if all horses are visited, else return false
     */
    public boolean allVisited(){
        return visitedNum==getNumOfHorses();
    }

    /**
     * @return true, if the plane is currently at a horse's location
     */
    public boolean atHorse(){
        return currentlyAt>=2; //plane is at horse
    }
    /**
     * @return true, if the plane is currently in paris
     */
    public boolean atParis(){
        return currentlyAt == PARIS_INDEX;
    }
    /**
     * @return true, if the plane is at its starting position
     */
    public boolean atStart(){
        return currentlyAt == PLANE_INDEX;
    }
    public double getTime(){
        return time;
    }
    /**
     * @return the index of the node the plane is currently at
     */
    public int getCurrentlyAt(){
        return currentlyAt;
    }

    /**
     * Unloads all horses in Paris and for all loaded horses, increases the time
     * This method does not fly the plane to paris!
     */
    public void unloadInParis(){
        while(loaded.count()>0){
            time += getHorse(loaded.pop()).time;
        }
        getAirplane().unload();
    }
    /**
     * Loads a single horse and increases the time
     * @param i horse index
     */
    public void load(int i){
        if(getAirplane().loadHorse(getHorse(i).weight)) {
            loaded.push(i);
            time += getHorse(i).time;
        }
    }
    /**
     * Flies the plane to the node and increases the time according to the speed and distance
     * also marks the node as visited and updates the currentlyAt position to the node index
     * @param i the node to fly to
     * @throws IllegalArgumentException when i == PLANE_INDEX
     * @throws RuntimeException when flying to a horse already visited
     */
    public void flyTo(int i){
        if(i==PLANE_INDEX) throw new IllegalArgumentException("Plane cannot fly to itself");
        if(i>=2 && visited[i]) throw new RuntimeException("Flying to a horse already visited");

        time += timeFromVelAndDist(getAirplane().speed,getWeight(PLANE_INDEX,i));
        getAirplane().flyTo(nodes[i].x,nodes[i].y);
        visit(i);
        currentlyAt = i;
    }

    /**
     * If the node is not already visited:<br>
     * marks the node as visited and increases the number of visited nodes
     * @param i node
     */
    private void visit(int i){
        if(visited[i]) return;
        visited[i] = true;
        visitedNum++;
    }
    /**
     * A simple function to calculate the time knowing the velocity and distance
     * @param velocity the velocity
     * @param dist the distance
     * @return the time: time = distance/velocity
     */
    private double timeFromVelAndDist(double velocity, double dist){
        return dist/velocity;
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


