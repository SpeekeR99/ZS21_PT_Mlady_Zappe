import java.util.AbstractList;

/**
 * This singleton class handles the whole simulation
 */
public class FlightSimulator {

    /**
     * static instance
     */
    private static final FlightSimulator SIM = new FlightSimulator();

    /**
     * private constructor
     */
    private FlightSimulator() {
    }

    /**
     * All the "subspaces" - each for one plane
     */
    MetricsGraph[] graphs;
    /**
     * Finished
     */
    boolean[] graphFinished;
    /**
     * The algorithm for calculating the closest nodes
     */
    ClosestNeighbourPath algorithm;

    /**
     * Getter for FlightSimulator instance
     *
     * @param graphs    Graphs
     * @param algorithm Algorithm
     * @return instance of FlightSimulator class
     */
    public static FlightSimulator getSimulator(
            MetricsGraph[] graphs, ClosestNeighbourPath algorithm) {
        SIM.reset();
        SIM.graphs = graphs;
        SIM.algorithm = algorithm;
        SIM.graphFinished = new boolean[graphs.length];

        return SIM;
    }

    /**
     * Resets the simulator
     */
    private void reset() {
        graphs = null;
        algorithm = null;
        graphFinished = null;
    }

    //the state variables of graphs

    /**
     * Indicates what happens now and next
     */
    private FlightState event = FlightState.Start;
    /**
     * Current time
     */
    private double curTime = 0.0;
    /**
     * Departure of the plane
     */
    private double departure = -1.0;
    /**
     * The index of the horse currently at
     */
    private int curHorse = -1;
    /**
     * The closest horse and the next horse to fly to
     */
    private int closest = -1, next = -1;
    /**
     * Index of Paris
     */
    private final int PARIS = MetricsGraph.PARIS_INDEX;

    /**
     * Runs the simulation
     *
     * @param nodesInOrder ArrayList to be filled in for visualization
     */
    public void simulate(AbstractList<GraphNode> nodesInOrder) {

        // Unused and causing PMD troubles :)
//        int PLANE = MetricsGraph.PLANE_INDEX, HORSE_OFFSET = 2;

        boolean notDone = true;

        while (notDone) {
            notDone = false;

            for (int i = 0; i < graphs.length; i++) {
                if (graphFinished[i]) {
                    continue;
                }

                if (graphs[i].atHorse()) {
                    atHorse(i, nodesInOrder);
                } else if (graphs[i].atParis()) {
                    atParis(i, nodesInOrder);
                } else if (graphs[i].atStart()) {
                    atStart(i, nodesInOrder);
                }

                //  if(curHorse_isHorseIndex) curHorse = graphs[i].getHorse(curHorse).index;

                System.out.println(formatOutput(event, Math.round(curTime), i, curHorse, Math.round(departure), next));


                notDone = true;

            }
        }
    }

    /**
     * Plane is at Horse
     *
     * @param i            Iteration of for
     * @param nodesInOrder Nodes in order for visuals
     */
    private void atHorse(int i, AbstractList<GraphNode> nodesInOrder) {
        curTime = graphs[i].getTime();
        curHorse = graphs[i].getCurrentlyAt();
        graphs[i].load(curHorse);
        departure = graphs[i].getTime(); //the .load() method above changed the time

        closest = algorithm.findNextClosestHorse(graphs[i]);

        if (closest == -1) {
            //in process and full ->
            //fly to unload in Paris
            event = FlightState.Naklad_A_Francie;
            next = PARIS;
            graphs[i].flyTo(PARIS);

            nodesInOrder.add(graphs[i].getParis());
        } else {
            //in process and can load moar ->
            //fly to the next one
            event = FlightState.Naklad_A_Dalsi;
            next = closest;
            graphs[i].flyTo(next);

            nodesInOrder.add(graphs[i].getHorse(next));
        }

        curHorse = graphs[i].getHorse(curHorse).index;

    }

    /**
     * Plane is at Paris
     *
     * @param i            Iteration of for
     * @param nodesInOrder Nodes in order for visuals
     */
    private void atParis(int i, AbstractList<GraphNode> nodesInOrder) {
        curTime = graphs[i].getTime();
        curHorse = graphs[i].getCurrentlyAt(); //should be Paris
        graphs[i].unloadInParis();
        departure = graphs[i].getTime(); //method above updated the time

        if (graphs[i].allVisited()) {
            //finished
            event = FlightState.Konec;
            next = -1;
            graphFinished[i] = true;
        } else {
            //stopped in France to unload
            //-> fly to next closest horse
            event = FlightState.Francie_A_Dalsi;
            next = graphs[i].getNextClosestToParis();
            graphs[i].flyTo(next);

            nodesInOrder.add(graphs[i].getHorse(next));
        }
    }

    /**
     * Plane is at Start
     *
     * @param i            Iteration of for
     * @param nodesInOrder Nodes in order for visuals
     */
    private void atStart(int i, AbstractList<GraphNode> nodesInOrder) {
        //at start -> only fly to a horse
        closest = algorithm.findNextClosestHorse(graphs[i]);

        event = FlightState.Start;
        curTime = graphs[i].getTime();
        curHorse = (int) graphs[i].getAirplane().x; //using curHorse and next variables to store planes position
        next = (int) graphs[i].getAirplane().y;

        if (closest == -1) {
            //no horses in graph, the airplane does not have to fly at all
            graphFinished[i] = true;
            return;
        }

        graphs[i].flyTo(closest);

        nodesInOrder.add(graphs[i].getHorse(closest));
    }

    /**
     * Formatted output into console
     *
     * @param event      event
     * @param curTime    current time
     * @param planeIndex index of airplane
     * @param curHorse   current horse
     * @param departure  departure
     * @param next       next
     * @return formatted string
     */
    private String formatOutput(FlightState event, long curTime, int planeIndex, int curHorse, long departure, int next) {
        return switch (event) {
            case Start -> String.format(event.pat, curTime, planeIndex, curHorse, next);

            case Naklad_A_Dalsi -> String.format(event.pat, curTime, planeIndex, curHorse, departure, next);

            case Naklad_A_Francie -> String.format(event.pat, curTime, planeIndex, curHorse, departure);

            case Francie_A_Dalsi -> String.format(event.pat, curTime, planeIndex, departure, next);

            case Konec -> String.format(event.pat, curTime, planeIndex, departure);

        };
    }
}

/**
 * Enum of possible formatted outputs
 */
enum FlightState {
    /**
     * start
     */
    Start("Cas: %d, Letoun: %d, Start z mista: %d, %d"),
    /**
     * taking horse and going to next horse
     */
    Naklad_A_Dalsi("Cas: %d, Letoun: %d, Naklad kone: %d, Odlet v: %d, Let ke koni: %d"),
    /**
     * taking horse and going to paris
     */
    Naklad_A_Francie("Cas: %d, Letoun: %d, Naklad kone: %d, Odlet v: %d, Let do Francie"),
    /**
     * goign from paris to horse
     */
    Francie_A_Dalsi("Cas: %d, Letoun: %d, Pristani ve Francii, Odlet v: %d, Let ke koni: %d"),
    /**
     * end
     */
    Konec("Cas: %d, Letoun: %d, Pristani ve Francii, Vylozeno v: %d");

    /**
     * pattern
     */
    public final String pat;

    /**
     * Constructor sets the pattern
     *
     * @param pat pattern
     */
    FlightState(String pat) {
        this.pat = pat;
    }
}
