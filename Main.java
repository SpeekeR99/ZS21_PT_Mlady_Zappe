

import java.io.FileNotFoundException;
import java.util.ArrayList;

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

    public static void main(String[] args) {
        String[] nodes = {"a","b","c","d","e","f","g","h","i"};
        AGraph<String> graph = new AdjMatrixGraph<>(nodes, new FloydWarshall());
        //graph: https://d2vlcm61l7u1fs.cloudfront.net/media%2F7a1%2F7a1276dd-60b9-4d39-a33e-ef0efc52d63c%2FphpdlMnsA.png

        graph.addEdge("a","b",4);
        graph.addEdge("b","d",9);
        graph.addEdge("b","c",11);
        graph.addEdge("c","a",8);
        graph.addEdge("d","e",2);
        graph.addEdge("d","f",6);
        graph.addEdge("d","c",7);
        graph.addEdge("e","b",8);
        graph.addEdge("e","g",7);
        graph.addEdge("e","h",4);
        graph.addEdge("f","e",5);
        graph.addEdge("f","c",1);
        graph.addEdge("g","i",9);
        graph.addEdge("g","h",14);
        graph.addEdge("h","f",2);
        graph.addEdge("h","i",10);

        graph.startAlgorithm();
        System.out.println(graph.getDistance("a","i"));


    }
}
