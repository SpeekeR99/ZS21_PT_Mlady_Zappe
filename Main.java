import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Parser parser;
        try {
            parser = new Parser("data/tutorial.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Chyba při načítání souboru:");
            e.printStackTrace();
            return;
        }
        /* input */
        ArrayList<Double> data = parser.getInput();
        /* paris X, Y location */
        GraphNode paris = new GraphNode(data.get(0), data.get(1));
        /* horses and aircrafts number */
        double temp = data.get(2);
        int numberOfHorses = (int) temp;
        temp = data.get(3 + numberOfHorses * 4);
        int numberOfAircrafts = (int) temp;
        /* nodes */
        GraphNode[] nodes = new GraphNode[1 + numberOfAircrafts + numberOfHorses]; // 1 + because of Paris
        nodes[0] = paris;
        /* aircrafts input */
        for(int i = 0; i < numberOfAircrafts*4; i += 4) {
            double x = data.get(i + 4 + numberOfHorses * 4);
            double y = data.get(i + 5 + numberOfHorses * 4);
            double weightCapacity = data.get(i + 6 + numberOfHorses * 4);
            double speed = data.get(i + 7 + numberOfHorses * 4);
            Aircraft aircraft = new Aircraft(x, y, weightCapacity, speed);
            // Do something with the aircraft here | add to the graph or something
            nodes[i / 4 + 1] = aircraft;
        }
        /* horses input */
        for(int i = 0; i < numberOfHorses*4; i += 4) {
            double x = data.get(i + 3);
            double y = data.get(i + 4);
            double weight = data.get(i + 5);
            double time = data.get(i + 6);
            Horse horse = new Horse(x, y, weight, time);
            // Do something with the horse here | add to the graph or something
            nodes[i / 4 + 1 + numberOfAircrafts] = horse;
        }
        /* vypis testovaci, ze v nodes je vse jak ma byt
        * to jest - na 0 indexu je paris, hned potom jsou letadla a
        * az nakonec jsou kone (+ indexy v nodes, takze napr kun1 = index prvniho kone - pocet letadel) */
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
        /* graph */
        MetricsGraph graph = new MetricsGraph(nodes, new CartesianDist());
        ClosestNeighbourPath algorithm = new ClosestNeighbourPath(0);
        for(int i = 1; i <= numberOfAircrafts; i++) {
            algorithm.setStart(i);
            algorithm.start(graph);
            IntQueue path = algorithm.getPath();
            while (path.count() != 0) {
                System.out.print(path.pop() + "   ");
            }
            System.out.println();
        }
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
