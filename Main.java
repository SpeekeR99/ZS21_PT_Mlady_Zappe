import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
        else filepath = "data/pi4.txt";

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

        // aircrafts
        temp = data.get(3 + numberOfHorses * 4);
        int numberOfAircrafts = (int) temp;
        Aircraft[] airplanes = fillAirplanes(data, numberOfAircrafts, numberOfHorses);

        numberOfAircrafts = Math.min(1,numberOfAircrafts); //FOR 1 PLANE ONLY

        // graph
        ArrayList<GraphNode> nodesInOrder = new ArrayList<>();
        MetricsGraph[] graph = new MetricsGraph[numberOfAircrafts];
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


        /* vypis testovaci, ze v nodes je vse jak ma byt
        * to jest - na 0 indexu je paris, hned potom jsou letadla a
        * az nakonec jsou kone (+ indexy v nodes, takze napr kun1 = index prvniho kone - pocet letadel) */
        /*
        for(int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof Horse) {
                System.out.println("kun " + i);
                System.out.println(nodes[i].x + " " + nodes[i].y + " " + ((Horse) nodes[i]).weight + " " + ((Horse) nodes[i]).time);
            } else if (nodes[i] instanceof Aircraft) {
                System.out.println("letadlo " + i);
                System.out.println(nodes[i].x + " " + nodes[i].y + " " + ((Aircraft) nodes[i]).weightCapacity + " " + ((Aircraft) nodes[i]).speed);
            } else {
                System.out.println("paris " + i);
                System.out.println(nodes[i].x + " " + nodes[i].y);
            }
        }

        int len = 0;
        for (int i = 1; nodes[i] instanceof Aircraft ; i++) {
            len+= getClosestHorses(nodes,numberOfAircrafts+1,(Aircraft) nodes[i]).length;
        }
        System.out.println("horse counts: "+len + " " + horsesCount);
        */
/*      OLD PATH FINDING

        MetricsGraph graph[] = new MetricsGraph[numberOfAircrafts];
        ClosestNeighbourPath algorithm = new ClosestNeighbourPath();
        for(int i = 0; i < numberOfAircrafts; i++) {
            System.out.printf("\tgraf %d: \n",i);
            graph[i] = new MetricsGraph(horses,airplanes[i],paris, distFunction);
            System.out.println("\tgraf done, starting algorithm");
            algorithm.start(graph[i]);
            IntQueue path = algorithm.getPath();
            Horse cur;
            while (path.count() != 0) {
                cur = graph[i].getHorse(path.pop());
                System.out.print(cur.index + "   ");
                horsesInOrder.add(cur);
            }
            System.out.println();
        }
        System.out.printf("%s %f %d",distFunction.getClass().getSimpleName(),velocitySum,horsesCount);

*/
/*      TREE_SET

       Comparator<GraphNode> comparator = new CartesianDistComparator();
        ((CartesianDistComparator)comparator).setReferenceNode(new GraphNode(0,0));
        TreeSetGraph treeGraph = new TreeSetGraph(horses,airplanes[0],paris,comparator);
        Horse cur;
        int iters = 0;
        while(!treeGraph.goneThroughAll()){
            cur = treeGraph.closestToPlane();
            horsesInOrder.add(cur);
            treeGraph.getPlane().flyTo(cur.x,cur.y);
            System.out.println(cur.index);
            treeGraph.delete(cur);
            iters++;
        }

        System.out.println(iters);
*/
    }

    /**
     * gets the indices of Horse objects in the horses array that are the closest from the plane
     * @param nodes array of all nodes (paris + planes + horses)
     * @param firstHorseIndex index of the first horse in nodes array
     * @param plane the current plane
     * @param numberOfHorses total number of all horses
     * @return array of indices of the <code>horses</code> array, on which are the closest Horse objects
     */
    private static int[] getClosestHorses(GraphNode[] nodes, int firstHorseIndex, Aircraft plane, int numberOfHorses) {
        int[] indeces = new int[numOfHorsesBasedOnVelocity(plane.speed, numberOfHorses)];
        MinHeap horseHeap = new MinHeap(numberOfHorses);
        int lastHorseIndex = firstHorseIndex+numberOfHorses;

        //calculate distance from plane to each horse
        for (int i = firstHorseIndex; i < lastHorseIndex; i++) {
            horseHeap.push(i, distFunction.dist(nodes[i],plane));
        }
        //get only that much horses, as needed for this plane
        for (int i = 0; i < indeces.length; i++) {
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

/*
class GraphTest{
    static Random r = new Random(1);

    public static void main(String[] args) {
        int val = 1_051_442_000;
        char[] a = new char[val];
        System.out.println(val);
    }

    public static void testHeap(){
        MinHeap h = new MinHeap(10_000);
        for (int i = 0; i < h.size(); i++) {
            h.push(i,r.nextDouble());
        }
        double[] p = new double[h.size()];
        for (int i = 0; i < h.size(); i++) {
            p[i] = h.pop().weight;
        }

        for (int i = 1; i < p.length; i++) {
            if(p[i-1]>p[i]) {
                System.out.println("NE "+i);

            }
        }

        for (double v :p) {
            System.out.println(v);
        }
    }
    public static void testGraph() {

        Place[] nodes = new Place[7000];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Place(100*r.nextDouble(), 100*r.nextDouble());
        }

        AdjMatrixGraph<Place> graph = new AdjMatrixGraph<>(nodes, new ClosestNeighbourPath(0));

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes.length; j++) {
                graph.addEdge(i,j,nodes[i].dist(nodes[j]));
            }
        }

      //  System.out.println(Arrays.deepToString(graph.adjMatrix));
        double time = System.nanoTime();
        graph.startAlgorithm();
        time = System.nanoTime()-time;
        System.out.println(time/1000000);
        graph.reconstructPath(0,0).foreach(System.out::println);
        time = System.nanoTime()-time;
        System.out.println(time);


    }

    static class Place{
        double x,y;

        public Place(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double dist(Place b){
            double difX = this.x-b.x;
            double difY = this.y-b.y;
            return Math.sqrt(difX*difX+difY*difY);
        }
    }
}
 */
