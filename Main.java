import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Parser parser = new Parser("data/parser.txt");
        ArrayList<Double> data = parser.getInput();
        for(Double d : data) {
            System.out.println(d);
        }
        double parisX = data.get(0);
        double parisY = data.get(1);
        double temp = data.get(2);
        int numberOfHorses = (int) temp;
    }

}
