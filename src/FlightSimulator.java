import java.util.AbstractList;
import java.util.Scanner;

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
     * Getter for src.FlightSimulator instance
     *
     * @param graphs    Graphs
     * @param algorithm Algorithm
     * @return instance of src.FlightSimulator class
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
        breakpoints = null;
    }

    /**
     * Passes the breakpoints into the simulator
     *
     * @param breakpoints the breakpoints queue
     */
    public void setBreakpoints(IntQueue breakpoints) {
        this.breakpoints = breakpoints;
    }

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
     * The closest horse
     */
    private int closest = -1;
    /**
     * the next horse to fly to
     */
    private int next = -1;
    /**
     * Index of Paris
     */
    private final int PARIS = MetricsGraph.PARIS_INDEX;
    /**
     * Keyboard input
     */
    private Scanner keyboard;
    /**
     * Queue of the user-defined break points
     */
    private IntQueue breakpoints;

    /**
     * Runs the simulation
     *
     * @param nodesInOrder                  ArrayList to be filled in for statistics
     * @param nodesInOrderOrderedByAirplane Array of horses in order of being picked up, indexed by airplane
     * @param stepping                      If user wants to go step by step in simulation
     * @return Starting time of Olympic games
     */
    public double simulate(AbstractList<GraphNode> nodesInOrder, AbstractList<GraphNode>[] nodesInOrderOrderedByAirplane, boolean stepping) {
        boolean localStep = stepping;
        if (localStep) {
            System.out.println("Welcome in simulation step by step!\nPlease input \"Next\" for next step or \"Finish\" to stop going step by step.");
        }

        long next_bp = avoidCyclomaticComplexity();

        keyboard = new Scanner(System.in);
        boolean notDone = true;
        while (notDone) {
            notDone = false;

            for (int i = 0; i < graphs.length; i++) {
                if (localStep) {
                    localStep = whatNowStep();
                }

                if (graphFinished[i]) {
                    graphs[i].getAirplane().endOfFlightTime = Math.round(graphs[i].getTime());
                    continue;
                }

                if (doNextStep(i, nodesInOrder, nodesInOrderOrderedByAirplane)) {
                    continue;
                }

                //  if(curHorse_isHorseIndex) curHorse = graphs[i].getHorse(curHorse).index;
                System.out.println(formatOutput(event, Math.round(curTime), i, curHorse, Math.round(departure), next));
                notDone = true;

                // proc and adjust breakpoints
                if (Math.round(curTime) >= next_bp) {
                    localStep = true;
                    System.out.println("Breakpoint found - switching to stepping mode. Type \"Next\" for next step or \"Finish\" to stop going step by step.");
                    next_bp = breakpoints.count() == 0 ? Long.MAX_VALUE : (long) breakpoints.pop();
                }
            }
        }
        keyboard.close();
        return curTime;
    }

    /**
     * PMD Clean code, just to avoid Cyclomatic Complexity of 10
     *
     * @return Long MAX_VALUE or first breakpoint from breakpoints queue
     */
    private long avoidCyclomaticComplexity() {
        return breakpoints == null ? Long.MAX_VALUE : (long) breakpoints.pop();
    }

    /**
     * Step by step simulation
     *
     * @return true if step by step is continueing, false if not
     */
    private boolean whatNowStep() {
        do {
            String input = keyboard.nextLine();
            if (input.equalsIgnoreCase("Next")) {
                return true;
            } else if (input.equalsIgnoreCase("Finish")) {
                return false;
            } else {
                System.out.println("Unknown command.\nPlease input \"Next\" for next step or \"Finish\" to stop going step by step.");
            }
        } while (true);
    }

    /**
     * Decides what is the next step to be done by the plane
     *
     * @param i                             Iteration of for loop
     * @param nodesInOrder                  Nodes in order for visuals
     * @param nodesInOrderOrderedByAirplane Array of horses in order of being picked up, indexed by airplane
     * @return True if for loop is to be continued
     */
    private boolean doNextStep(int i, AbstractList<GraphNode> nodesInOrder, AbstractList<GraphNode>[] nodesInOrderOrderedByAirplane) {
        if (graphs[i].atHorse()) {
            atHorse(i, nodesInOrder, nodesInOrderOrderedByAirplane);
        } else if (graphs[i].atParis()) {
            atParis(i, nodesInOrder, nodesInOrderOrderedByAirplane);
        } else if (graphs[i].atStart()) {
            return atStart(i, nodesInOrder, nodesInOrderOrderedByAirplane);
        }
        return false;
    }

    /**
     * Plane is at src.Horse
     *
     * @param i                             Iteration of for
     * @param nodesInOrder                  Nodes in order for visuals
     * @param nodesInOrderOrderedByAirplane Array of horses in order of being picked up, indexed by airplane
     */
    private void atHorse(int i, AbstractList<GraphNode> nodesInOrder, AbstractList<GraphNode>[] nodesInOrderOrderedByAirplane) {
        curTime = graphs[i].getTime();
        curHorse = graphs[i].getCurrentlyAt();
        graphs[i].load(curHorse);
        graphs[i].getHorse(curHorse).timePickUp = Math.round(graphs[i].getTime());
        graphs[i].getAirplane().loadedHorses.add(graphs[i].getHorse(curHorse));
        for (Horse h : graphs[i].getAirplane().loadedHorses) {
            h.beenThrough.add(graphs[i].getHorse(curHorse));
        }
        graphs[i].getHorse(curHorse).transportedBy = graphs[i].getAirplane();
        departure = graphs[i].getTime(); //the .load() method above changed the time

        closest = algorithm.findNextClosestHorse(graphs[i]);

        if (closest == -1) {
            //in process and full ->
            //fly to unload in Paris
            event = FlightState.Naklad_A_Francie;
            next = PARIS;
            graphs[i].flyTo(PARIS);

            nodesInOrder.add(graphs[i].getParis());
            nodesInOrderOrderedByAirplane[i].add(graphs[i].getParis());
        } else {
            //in process and can load moar ->
            //fly to the next one
            event = FlightState.Naklad_A_Dalsi;
            next = closest;
            graphs[i].flyTo(next);

            nodesInOrder.add(graphs[i].getHorse(next));
            nodesInOrderOrderedByAirplane[i].add(graphs[i].getHorse(next));
            next = graphs[i].getHorse(next).index;
        }

        curHorse = graphs[i].getHorse(curHorse).index;

    }

    /**
     * Plane is at Paris
     *
     * @param i                             Iteration of for
     * @param nodesInOrder                  Nodes in order for visuals
     * @param nodesInOrderOrderedByAirplane Array of horses in order of being picked up, indexed by airplane
     */
    private void atParis(int i, AbstractList<GraphNode> nodesInOrder, AbstractList<GraphNode>[] nodesInOrderOrderedByAirplane) {
        curTime = graphs[i].getTime();
        curHorse = graphs[i].getCurrentlyAt(); //should be Paris
        for (Horse h : graphs[i].getAirplane().loadedHorses) {
            h.timeDroppedInParis = Math.round(graphs[i].getTime());
        }
        graphs[i].getAirplane().loadedHorses.clear();
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
            nodesInOrderOrderedByAirplane[i].add(graphs[i].getHorse(next));
            next = graphs[i].getHorse(next).index;
        }
    }

    /**
     * Plane is at Start
     *
     * @param i                             Iteration of for
     * @param nodesInOrder                  Nodes in order for visuals
     * @param nodesInOrderOrderedByAirplane Array of horses in order of being picked up, indexed by airplane
     * @return true if continue was in the original for loop
     */
    private boolean atStart(int i, AbstractList<GraphNode> nodesInOrder, AbstractList<GraphNode>[] nodesInOrderOrderedByAirplane) {
        //at start -> only fly to a horse
        closest = algorithm.findNextClosestHorse(graphs[i]);

        event = FlightState.Start;
        curTime = graphs[i].getTime();
        curHorse = (int) graphs[i].getAirplane().x; //using curHorse and next variables to store planes position
        next = (int) graphs[i].getAirplane().y;

        if (closest == -1) {
            //no horses in graph, the airplane does not have to fly at all
            graphFinished[i] = true;
            return true;
        }

        graphs[i].flyTo(closest);

        nodesInOrder.add(graphs[i].getHorse(closest));
        nodesInOrderOrderedByAirplane[i].add(graphs[i].getHorse(closest));
        return false;
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
        String ret = "";
        switch (event) {
            case Start:
                ret = String.format(event.pat, curTime, planeIndex, curHorse, next);
                break;

            case Naklad_A_Dalsi:
                ret = String.format(event.pat, curTime, planeIndex, curHorse, departure, next);
                break;

            case Naklad_A_Francie:
                ret = String.format(event.pat, curTime, planeIndex, curHorse, departure);
                break;

            case Francie_A_Dalsi:
                ret = String.format(event.pat, curTime, planeIndex, departure, next);
                break;

            case Konec:
                ret = String.format(event.pat, curTime, planeIndex, departure);
                break;

            default:
                break;
        }
        return ret;
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
