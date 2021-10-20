/**
 * This singleton class handles the whole simulation
 */
public class FlightSimulator {

    //singleton
    private static final FlightSimulator SIM = new FlightSimulator();
    private FlightSimulator(){}

    /**
     * All the "subspaces" - each for one plane
     */
    MetricsGraph[] graphs;
    /**
     * The current times in each graph.
     * each graph is independent, in time as well
     * outputs don't have to be sorted in any way
     * only restriction(s):
     *       time = 0 is the same global moment for all the graphs
     *       time flies in all graphs with the same "velocity" - this one is clear
     */
    int[] curTimes;

    public static FlightSimulator getSimulator(
            MetricsGraph[] graphs)
    {
        SIM.reset();
        SIM.graphs = graphs;
        SIM.curTimes = new int[graphs.length];

        return SIM;
    }

    private void reset(){
        graphs = null;
        curTimes = null;
    }
}
