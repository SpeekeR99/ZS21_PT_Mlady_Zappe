

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
