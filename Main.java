import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;

public class Main {

    /** sum of velocity */
    private static double velocitySum;
    /** dist function represents cartesian or polar calculation of distance */
    private static DistFunction distFunction;

    /**
     * Creates instance of class Parser based on args
     * if arguments exist, it takes the first argument as filepath
     * if arguments weren't put in, it has some default filepath set
     * @param args arguments from cmd
     * @return instance of Parser or NULL, based on if the file exists or not
     */
    private static Parser createParser(String[] args) {
        String filepath;
        if (args.length != 0) filepath = args[0];
        else filepath = "data/random10000.txt";

        Parser parser;
        try {
            parser = new Parser(filepath);
            return parser;
        } catch (FileNotFoundException e) {
            System.out.println("Chyba při načítání souboru:");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates array of Horses, then fills the array with corresponding data
     * based on input file (X, Y, Weight and Time attributes of horses)
     * @param data input data from file
     * @param numberOfHorses the number of horses that are present
     * @return array of horses based on input file
     */
    private static Horse[] fillHorses(ArrayList<Double> data, int numberOfHorses) {
        Horse[] horses = new Horse[numberOfHorses];

        for(int i = 0; i < numberOfHorses*4; i += 4) {
            double x = data.get(i + 3);
            double y = data.get(i + 4);
            double weight = data.get(i + 5);
            double time = data.get(i + 6);
            Horse horse = new Horse(x, y, weight, time);
            horse.index = i/4;
            // Do something with the horse here | add to the graph or something
            horses[i / 4] = horse;
        }

        return horses;
    }



    /**
     * Creates array of Aircrafts, then fills the array with corresponding data
     * based on input file (X, Y, WeightCapacity and Speed attributes of aircrafts)
     * @param data input data from file
     * @param numberOfAircrafts the number of aircrafts that are present
     * @param numberOfHorses the number of horses that are present
     * @return array of aircrafts based on input file
     */
    private static Aircraft[] fillAirplanes(ArrayList<Double> data, int numberOfAircrafts, int numberOfHorses) {
        Aircraft[] airplanes = new Aircraft[numberOfAircrafts];

        for(int i = 0; i < numberOfAircrafts*4; i += 4) {
            double x = data.get(i + 4 + numberOfHorses * 4);
            double y = data.get(i + 5 + numberOfHorses * 4);
            double weightCapacity = data.get(i + 6 + numberOfHorses * 4);
            double speed = data.get(i + 7 + numberOfHorses * 4);
            velocitySum+=speed;
            Aircraft aircraft = new Aircraft(x, y, weightCapacity, speed);
            // Do something with the aircraft here | add to the graph or something
            airplanes[i / 4] = aircraft;
        }

        return airplanes;
    }

    /**
     * Basic primitive visualization of airplane's flight
     * @param nodesInOrder nodes in order of simulation
     * @param visualization true if window should open up, false otherwise
     */
    private static void visulize(ArrayList<GraphNode> nodesInOrder, boolean visualization) {
        if (!visualization) return;
        java.util.Timer timer = new java.util.Timer();

        JFrame window = new JFrame();
        window.setTitle("Visuals");
        window.setSize(1000, 1000);
        DrawingPanel panel = new DrawingPanel(nodesInOrder,false);
        window.add(panel);
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        Graphics winGraph = window.getGraphics();

        //( (Graphics2D)winGraph ).translate((double)panel.getWidth()/2 - paris.x,  (double)panel.getHeight()/2 - (paris.y));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(panel.drawFlight(winGraph)) timer.cancel();
            }
        }, 0L, 5L);
    }

    /**
     * Entry point, start the app
     * @param args arguments from cmd
     *             Expecting first argument to be filepath to input file
     */
    public static void main(String[] args) {
        // ini
        Parser parser = createParser(args);
        if (parser == null) return;
        velocitySum = 0.0;
        distFunction = new CartesianDist();
        boolean visualization = true; // Visualization window

        // data and paris
        ArrayList<Double> data = parser.getInput();
        GraphNode paris = new GraphNode(data.get(0), data.get(1));

        // horses
        double temp = data.get(2);
        int numberOfHorses = (int) temp;
        Horse[] horses = fillHorses(data, numberOfHorses);
        ArrayList<Horse> horsesList = fillHorsesList(data,numberOfHorses);

        // aircrafts
        temp = data.get(3 + numberOfHorses * 4);
        int numberOfAircrafts = (int) temp;
        Aircraft[] airplanes = fillAirplanes(data, numberOfAircrafts, numberOfHorses);

        //sort planes by speed
        Arrays.sort(airplanes, (a,b) -> {
            double epsilon = 1e-7;
            return Math.abs(a.speed - b.speed) < epsilon ? 0 : (int)(a.speed - b.speed);
        });

        //numberOfAircrafts = Math.min(1,numberOfAircrafts); //FOR 1 PLANE ONLY

        // graph
        MetricsGraph[] graph = new MetricsGraph[numberOfAircrafts];

        graph = assignHorsesToAirplanes(airplanes,horsesList, distFunction,paris);

        ArrayList<GraphNode> nodesInOrder = new ArrayList<>();
        ClosestNeighbourPath algorithm = new ClosestNeighbourPath();
        /*
        for (int i = 0; i < numberOfAircrafts; i++) {
            graph[i] = new MetricsGraph(horses,airplanes[i],paris,distFunction);
        }
        */
        graph[0] = new MetricsGraph(horses,airplanes[0],paris,distFunction);
        FlightSimulator sim = FlightSimulator.getSimulator(graph,algorithm);
        sim.simulate(nodesInOrder);
        System.out.printf("Celkem prepraveno %d koni.",graph[0].getNumOfHorses());

        // visualization
        visulize(nodesInOrder, visualization);


/////////////////// WORKING CODE ENDS HERE /////////////////////////////////////////////////////////////////////////////

    }

    /**
     * Creates a graph for each plane that holds the closest horses
     * the number of the horses is derived by each plane's speed
     * in other words, returns division of horses assigned to airplanes wrapped in graphs
     *
     * This method clears the horsesList list!
     *
     * @param airplanes the array of all airplanes
     * @param horsesList the list of all horses (Will contain nothing after this method is done)
     * @param fun the distance function used by all graphs
     * @param paris paris
     * @return array of MetricsGraphs which act like division of horses assigned to an airplane
     */
    private static MetricsGraph[] assignHorsesToAirplanes(Aircraft[] airplanes, ArrayList<Horse> horsesList, DistFunction fun, GraphNode paris) {
        MetricsGraph graphs[] = new MetricsGraph[airplanes.length];
        int[] horseIndices; ArrayList<Horse> horses;

        for (int i = 0; i < airplanes.length-1; i++) {
            horses = new ArrayList<>();
            horseIndices = getClosestHorses(horsesList,airplanes[i]);

            for (int index : horseIndices)
                horses.add(horsesList.get(index));

            //so that we dont get the same horse twice
            for (int index : horseIndices)
                horsesList.remove(index);

            graphs[i] = new MetricsGraph(horses.toArray(Horse[]::new),airplanes[i],paris,distFunction);
        }
        //to the last plane, assign the rest of the horses
        //last plane should be the fastest
        graphs[airplanes.length-1] = new MetricsGraph(horsesList.toArray(Horse[]::new),airplanes[airplanes.length-1],paris,distFunction);
        horsesList.clear();
        return graphs;
    }


    /**
     * Creates array list of Horses, then fills the list with corresponding data
     * based on input file (X, Y, Weight and Time attributes of horses)
     * @param data input data from file
     * @param numberOfHorses the number of horses that are present
     * @return array list of horses based on input file
     */
    private static ArrayList<Horse> fillHorsesList(ArrayList<Double> data, int numberOfHorses) {
        ArrayList<Horse> horses = new ArrayList<>();

        for(int i = 0; i < numberOfHorses*4; i += 4) {
            double x = data.get(i + 3);
            double y = data.get(i + 4);
            double weight = data.get(i + 5);
            double time = data.get(i + 6);
            Horse horse = new Horse(x, y, weight, time);
            horse.index = i/4;
            // Do something with the horse here | add to the graph or something
            horses.add(horse);
        }

        return horses;
    }

    /**
     * gets the indices of Horse objects in the horses list that are the closest from the plane
     * based on its speed
     * @param nodes array list of all yet unassigned horses
     * @param plane the current plane
     * @return array of indices of the <code>horses</code> array, on which are the closest Horse objects
     */
    private static int[] getClosestHorses(ArrayList<Horse> nodes, Aircraft plane) {
        int numberOfHorses = nodes.size();
        //calculate how much horses to assign to this plane
        int[] indeces = new int[numOfHorsesBasedOnVelocity(plane.speed, numberOfHorses)];
        MinHeap horseHeap = new MinHeap(numberOfHorses);

        //calculate distance from plane to each horse
        int i = 0;
        for (Horse h : nodes) {
            horseHeap.push(i, distFunction.dist(h,plane));
            i++;
        }
        //get only that much horses, as needed for this plane
        for (i = 0; i < indeces.length; i++) {
            indeces[i] = horseHeap.pop().node();
        }

        return indeces;
    }

    /**
     * Distributes horses among planes based on their velocity.
     * A faster airplane can take more horses. If slower planes took the same amount of horses,
     * faster planes would be done much earlier. This will give more work to faster planes.
     *
     * @param velocity the current plane's velocity
     * @param numberOfHorses total number of all horses
     * @return a distribution of horses to the plane based on its velocity
     */
    private static int numOfHorsesBasedOnVelocity(double velocity, int numberOfHorses) {
        return (int)Math.ceil(numberOfHorses*velocity/velocitySum);
    }

}