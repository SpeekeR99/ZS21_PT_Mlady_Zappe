import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Parser parser;
        try {
            parser = new Parser("data/grid2000.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Chyba při načítání souboru:");
            e.printStackTrace();
            return;
        }

        double time = System.nanoTime();
        ArrayList<Double> data = parser.getInput();
        time = (System.nanoTime()-time)/1_000_000;
        System.out.println(time);
        /*for(Double d : data) {
            System.out.println(d);
        }*/
        double parisX = data.get(0);
        double parisY = data.get(1);
        double temp = data.get(2);
        int numberOfHorses = (int) temp;
    }


}

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
