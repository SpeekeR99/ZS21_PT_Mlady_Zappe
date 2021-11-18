/**
 * ClosestNeighbourPath
 */
class ClosestNeighbourPath{
    final int PLANE = MetricsGraph.PLANE_INDEX, HORSE_OFFSET = 2;

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
            if(i==node) {
                continue;
            }
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
        // Unused, so commented, PMD :)
//        double MAX_DIST = 1000.0;

        return (
                (!graph.isVisited(horseIndex)) &&
                plane.canLoadHorse(horse.weight) //&&
                //graph.getWeight(MetricsGraph.PLANE_INDEX,horseIndex)<MAX_DIST
        );
    }
}
