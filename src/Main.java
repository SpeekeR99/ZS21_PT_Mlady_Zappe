package src;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * src.Main class, entry point of the app
 * Parses the input data, then uses them to create
 * graph structures and simulate flights
 * Outputs the results into console
 */
public class Main {

    /**
     * sum of velocity
     */
    private static double velocitySum;
    /**
     * dist function represents cartesian or polar calculation of distance
     */
    private static DistFunction distFunction;
    /**
     * number of horses
     */
    private static int NUMBER_OF_HORSES;
    /**
     * Keyboard input
     */
    public static Scanner sc;

    /**
     * Creates instance of class src.Parser based on args
     * if arguments exist, it takes the first argument as filepath
     * if arguments weren't put in, it has some default filepath set
     *
     * @param filepath path to the file
     * @return instance of src.Parser or NULL, based on if the file exists or not
     */
    private static Parser createParser(String filepath) {
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

    // Commented, because PMD :)
//    /**
//     * Creates array of Horses, then fills the array with corresponding data
//     * based on input file (X, Y, Weight and Time attributes of horses)
//     * @param data input data from file
//     * @param numberOfHorses the number of horses that are present
//     * @return array of horses based on input file
//     */
//    private static src.Horse[] fillHorses(ArrayList<Double> data, int numberOfHorses) {
//        src.Horse[] horses = new src.Horse[numberOfHorses];
//
//        for(int i = 0; i < numberOfHorses*4; i += 4) {
//            double x = data.get(i + 3);
//            double y = data.get(i + 4);
//            double weight = data.get(i + 5);
//            double time = data.get(i + 6);
//            src.Horse horse = new src.Horse(x, y, weight, time);
//            horse.index = i/4;
//            // Do something with the horse here | add to the graph or something
//            horses[i / 4] = horse;
//        }
//
//        return horses;
//    }

    /**
     * Creates array of Aircrafts, then fills the array with corresponding data
     * based on input file (X, Y, WeightCapacity and Speed attributes of aircrafts)
     *
     * @param data              input data from file
     * @param numberOfAircrafts the number of aircrafts that are present
     * @param numberOfHorses    the number of horses that are present
     * @return array of aircrafts based on input file
     */
    private static Aircraft[] fillAirplanes(AbstractList<Double> data, int numberOfAircrafts, int numberOfHorses) {
        Aircraft[] airplanes = new Aircraft[numberOfAircrafts];

        for (int i = 0; i < numberOfAircrafts * 4; i += 4) {
            double x = data.get(i + 4 + numberOfHorses * 4);
            double y = data.get(i + 5 + numberOfHorses * 4);
            double weightCapacity = data.get(i + 6 + numberOfHorses * 4);
            double speed = data.get(i + 7 + numberOfHorses * 4);
            velocitySum += speed;
            Aircraft aircraft = new Aircraft(x, y, weightCapacity, speed);
            // Do something with the aircraft here | add to the graph or something
            airplanes[i / 4] = aircraft;
        }

        return airplanes;
    }

    /**
     * Basic primitive visualization of airplane's flight
     *
     * @param nodesInOrder  nodes in order of simulation
     * @param visualization true if window should open up, false otherwise
     */
    private static void visualize(AbstractList<GraphNode> nodesInOrder, boolean visualization) {
        if (!visualization) {
            return;
        }
        java.util.Timer timer = new java.util.Timer();

        JFrame window = new JFrame();
        window.setTitle("Visuals");
        window.setSize(1000, 1000);
        DrawingPanel panel = new DrawingPanel(nodesInOrder, false);
        window.add(panel);
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        Graphics winGraph = panel.getGraphics();

        //( (Graphics2D)winGraph ).translate((double)panel.getWidth()/2 - paris.x,  (double)panel.getHeight()/2 - (paris.y));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (panel.drawFlight(winGraph)) {
                    timer.cancel();
                }
            }
        }, 0L, 5L);
    }

    /**
     * Creates array list of Horses, then fills the list with corresponding data
     * based on input file (X, Y, Weight and Time attributes of horses)
     *
     * @param data           input data from file
     * @param numberOfHorses the number of horses that are present
     * @return array list of horses based on input file
     */
    private static AbstractList<Horse> fillHorsesList(AbstractList<Double> data, int numberOfHorses) {
        AbstractList<Horse> horses = new ArrayList<>();

        for (int i = 0; i < numberOfHorses * 4; i += 4) {
            double x = data.get(i + 3);
            double y = data.get(i + 4);
            double weight = data.get(i + 5);
            double time = data.get(i + 6);
            if (time < 0 || weight < 0) {
                System.out.println("ERROR: There is a Horse with negative time or weight!");
                System.exit(1);
            }
            Horse horse = new Horse(x, y, weight, time);
            horse.index = i / 4;
            // Do something with the horse here | add to the graph or something
            horses.add(horse);
        }

        return horses;
    }

    /**
     * Creates a graph for each plane that holds the closest horses
     * the number of the horses is derived by each plane's speed
     * in other words, returns division of horses assigned to airplanes wrapped in graphs
     * <p>
     * This method clears the horsesList list!
     *
     * @param airplanes  the array of all airplanes
     * @param horsesList the list of all horses (Will contain nothing after this method is done)
     * @param paris      paris
     * @return array of MetricsGraphs which act like division of horses assigned to an airplane
     */
    private static MetricsGraph[] assignHorsesToAirplanes(Aircraft[] airplanes, AbstractList<Horse> horsesList, GraphNode paris) {
        MetricsGraph[] graphs = new MetricsGraph[airplanes.length];
        int[] horseIndices;
        ArrayList<Horse> horses;

        for (int i = 0; i < airplanes.length - 1; i++) {
            horses = new ArrayList<>();
            horseIndices = getClosestHorses(horsesList, airplanes[i]);

            for (int index : horseIndices) {
                horses.add(horsesList.get(index));
            }

            //so that we dont get the same horse twice
            for (Horse h : horses) {
                horsesList.remove(h);
            }

            graphs[i] = new MetricsGraph(horses.toArray(Horse[]::new), airplanes[i], paris, distFunction);
        }
        //to the last plane, assign the rest of the horses
        //last plane should be the fastest
        graphs[airplanes.length - 1] = new MetricsGraph(horsesList.toArray(Horse[]::new), airplanes[airplanes.length - 1], paris, distFunction);
        horsesList.clear();
        return graphs;
    }

    /**
     * gets the indices of src.Horse objects in the horses list that are the closest from the plane
     * based on its speed
     *
     * @param nodes array list of all yet unassigned horses
     * @param plane the current plane
     * @return array of indices of the <code>horses</code> array, on which are the closest src.Horse objects
     */
    private static int[] getClosestHorses(AbstractList<Horse> nodes, Aircraft plane) {
        int numberOfUnassignedHorses = nodes.size();
        //calculate how much horses to assign to this plane
        int[] indeces = new int[numOfHorsesBasedOnVelocity(plane.speed)];
        MinHeap horseHeap = new MinHeap(numberOfUnassignedHorses);

        //calculate distance from plane to each horse
        int i = 0;
        for (Horse h : nodes) {
            horseHeap.push(i, distFunction.dist(h, plane));
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
     * @return a distribution of horses to the plane based on its velocity
     */
    private static int numOfHorsesBasedOnVelocity(double velocity) {
        return (int) (NUMBER_OF_HORSES * velocity / velocitySum);
    }

    /**
     * Reads arguments from command line
     * Checks if their in proper order
     * Also checks if breakpoints are set or not
     *
     * @param args arguments from cmd
     * @return Object array consisting of bp_index on index 0 and filepath on index 1
     */
    private static Object[] readArguments(String[] args) {
        int bp_index = 2;
        String filepath = "";
        if (args.length > 0) {

            try {

                if (args[0].equals("F")) {
                    filepath = args[1];
                } else if (args[0].equals("G")) {
                    filepath = args[1];
                    Generator.main(filepath, args[2], args[3]);
                    bp_index = 4;
                }

            } catch (IndexOutOfBoundsException ex) {
                System.out.println("Wrong arguments format. Terminating.");
                System.exit(1);
            }
        } else {
            filepath = "generated_data.txt";
            Generator.main(filepath, "100000", "100");
            bp_index = 0;
        }
        return new Object[]{bp_index, filepath};
    }

    /**
     * Sets up the breakpoints, if there were any, as input from user from cmd
     *
     * @param breakpoints Queue of breakpoints
     * @param bp_index    index of breakpoint
     * @param args        arguments from cmd
     */
    private static void addBreakPoints(IntQueue breakpoints, int bp_index, String[] args) {
        if (args.length > bp_index && args[bp_index].equals("-bp")) {
            for (int i = bp_index + 1; i < args.length; i++) {
                try {
                    breakpoints.push(Integer.parseInt(args[i]));
                } catch (Exception x) {
                    System.out.printf("\"%s\" is not a whole number. Skipping.\n", args[i]);
                }
            }
        }
    }

    /**
     * Entry point, start the app
     *
     * @param args arguments from cmd
     *             Expecting first argument to be F or G
     *             Next argument after F has to be filepath
     *             Next arguments after G have to be filepath, number of horses, number of airplanes
     *             (optional -br and breakpoints as integer values of time)
     */
    public static void main(String[] args) {
        //init: arguments and breakpoints
        Object[] array = readArguments(args);
        int bp_index = (int) array[0];
        String filepath = (String) array[1];
        IntQueue breakpoints = new IntQueue();
        addBreakPoints(breakpoints, bp_index, args);

        // Parse
        Parser parser = createParser(filepath);
        if (parser == null) {
            return;
        }
        velocitySum = 0.0;
        distFunction = new CartesianDist();
        boolean visualization = true; // Visualization window

        // data and paris
        AbstractList<Double> data = parser.getInput();
        GraphNode paris = new GraphNode(data.get(0), data.get(1));

        // horses
        double temp = data.get(2);
        int numberOfHorses = NUMBER_OF_HORSES = (int) temp;
        // src.Horse[] horses = fillHorses(data, numberOfHorses);
        AbstractList<Horse> horsesList = fillHorsesList(data, numberOfHorses);

        // aircrafts
        temp = data.get(3 + numberOfHorses * 4);
        int numberOfAircrafts = (int) temp;
        Aircraft[] airplanes = fillAirplanes(data, numberOfAircrafts, numberOfHorses);

        //sort planes by speed
        Arrays.sort(airplanes, (a, b) -> {
            double epsilon = 1e-7;
            return Math.abs(a.speed - b.speed) < epsilon ? 0 : (int) (a.speed - b.speed);
        });

        //numberOfAircrafts = Math.min(1,numberOfAircrafts); //FOR 1 PLANE ONLY

        // graph
        MetricsGraph[] graph;

        graph = assignHorsesToAirplanes(airplanes, horsesList, paris);
        horsesList = null; //the method above emptied the List, setting to null to avoid mem leak

        ArrayList<GraphNode> nodesInOrder = new ArrayList<>();
        ClosestNeighbourPath algorithm = new ClosestNeighbourPath();

        FlightSimulator sim = FlightSimulator.getSimulator(graph, algorithm);
        sim.setBreakpoints(breakpoints.count() == 0 ? null : breakpoints);
        double olympicsStartTime;
        sc = new Scanner(System.in);
        System.out.println("Welcome!\nWould you like to run the whole simulation at once, or go step by step?\n(1 = step by step | 0 = at once)");
        do {
            String input = sc.nextLine();
            if (input.equals("1")) {
                olympicsStartTime = sim.simulate(nodesInOrder, true);
                break;
            } else if (input.equals("0")) {
                olympicsStartTime = sim.simulate(nodesInOrder, false);
                break;
            } else {
                System.out.println("Unknown input!\nPlease input \"1\" or \"0\"");
            }
        } while (true);
        sc.close();
        System.out.printf("Celkem prepraveno %d koni.", numberOfHorses);

        // visualization
        visualize(nodesInOrder, visualization);

        //statistics
        Statistics stats = new Statistics(airplanes, nodesInOrder, olympicsStartTime, filepath);
        stats.generateStats();
    }

}