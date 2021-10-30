import java.util.ArrayList;

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
    boolean[] graphFinished;
    /**
     * The algorithm for calculating the closest nodes
     */
    ClosestNeighbourPath algorithm;

    public static FlightSimulator getSimulator(
            MetricsGraph[] graphs, ClosestNeighbourPath algorithm)
    {
        SIM.reset();
        SIM.graphs = graphs;
        SIM.algorithm = algorithm;
        SIM.graphFinished = new boolean[graphs.length];

        return SIM;
    }

    private void reset(){
        graphs = null;
        algorithm = null;
        graphFinished = null;
    }

    public void simulate(ArrayList<GraphNode> nodesInOrder){
        int PLANE = MetricsGraph.PLANE_INDEX, PARIS = MetricsGraph.PARIS_INDEX, HORSE_OFFSET = 2;

        //the state variables of graphs
        FlightState event = FlightState.Start; //indicates what happens now and next
        double curTime = 0.0, departure = -1.0; //current time and departure of the plane
        int curHorse = -1; //the index of the horse currently at
        int closest = -1, next = -1; //the closest horse and the next horse to fly to

        boolean notDone = true;

        while(notDone){
            notDone = false;

            for (int i = 0; i < graphs.length; i++) {
                if(graphFinished[i]) continue;

                if(graphs[i].atHorse()){
                    curTime = graphs[i].getTime();
                    curHorse = graphs[i].getCurrentlyAt();
                    graphs[i].load(curHorse);
                    departure = graphs[i].getTime(); //the .load() method above changed the time

                    closest = algorithm.findNextClosestHorse(graphs[i]);

                    if(closest==-1){
                        //in process and full ->
                        //fly to unload in Paris
                        event = FlightState.Naklad_A_Francie;
                        next = PARIS;
                        graphs[i].flyTo(PARIS);

                        nodesInOrder.add(graphs[i].getParis());
                    }
                    else{
                        //in process and can load moar ->
                        //fly to the next one
                        event = FlightState.Naklad_A_Dalsi;
                        next = closest;
                        graphs[i].flyTo(next);

                        nodesInOrder.add(graphs[i].getHorse(next));
                    }

                }
                else if(graphs[i].atParis()){
                    curTime = graphs[i].getTime();
                    curHorse = graphs[i].getCurrentlyAt(); //should be Paris
                    graphs[i].unloadInParis();
                    departure = graphs[i].getTime(); //method above updated the time

                    if(graphs[i].allVisited()){
                        //finished
                        event = FlightState.Konec;
                        next = -1;
                        graphFinished[i] = true;
                    }
                    else{
                        //stopped in France to unload
                        //-> fly to next closest horse
                        event = FlightState.Francie_A_Dalsi;
                        next = graphs[i].getNextClosestToParis();
                        graphs[i].flyTo(next);

                        nodesInOrder.add(graphs[i].getHorse(next));
                    }
                }
                else if(graphs[i].atStart()){
                    //at start -> only fly to a horse
                    closest = algorithm.findNextClosestHorse(graphs[i]);

                    event = FlightState.Start;
                    curTime = graphs[i].getTime();
                    curHorse = (int)graphs[i].getAirplane().x; //using curHorse and next variables to store planes position
                    next = (int)graphs[i].getAirplane().y;
                    graphs[i].flyTo(closest);

                    nodesInOrder.add(graphs[i].getHorse(closest));
                }

                System.out.println(formatOutput(event, (long)curTime,i,curHorse,(long)departure,next));


                notDone = true;

            }
        }
    }

    private String formatOutput(FlightState event,long curTime, int planeIndex, int curHorse, long departure, int next) {
       return switch (event){
            case Start ->  String.format(event.pat,curTime,planeIndex,curHorse,next);

            case Naklad_A_Dalsi ->  String.format(event.pat,curTime,planeIndex,curHorse,departure,next);

            case Naklad_A_Francie ->  String.format(event.pat,curTime,planeIndex,curHorse,departure);

            case Francie_A_Dalsi ->  String.format(event.pat,curTime,planeIndex,departure,next);

            case Konec ->  String.format(event.pat,curTime,planeIndex,departure);

        };
    }
}

enum FlightState{
    Start("Cas: %d, Letoun: %d, Start z mista: %d, %d"),
    Naklad_A_Dalsi("Cas: %d, Letoun: %d, Naklad kone: %d, Odlet v: %d, Let ke koni: %d"),
    Naklad_A_Francie("Cas: %d, Letoun: %d, Naklad kone: %d, Odlet v: %d, Let do Francie"),
    Francie_A_Dalsi("Cas: %d, Letoun: %d, Pristani ve Francii, Odlet v: %d, Let ke koni: %d"),
    Konec("Cas: %d, Letoun: %d, Pristani ve Francii, Vylozeno v: %d");


    public final String pat;

    FlightState(String pat) {
        this.pat = pat;
    }
}
