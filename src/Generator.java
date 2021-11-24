package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * src.Generator of input data
 */
public class Generator {

    /**
     * Entry point for src.Generator
     *
     * @param args arguments from cmd
     *             first argument is expected to be the wanted name of the file
     *             second argument is expected to be the wanted number of horses
     *             third argument is expected to be the wanted number of airplanes
     */
    public static void main(String... args) {
        int numberOfHorses;
        int numberOfAirplanes;
        String filepath;
        if (args.length != 3) {
            filepath = "data/generated_data.txt";
            numberOfHorses = 1000;
            numberOfAirplanes = 5;
        } else {
            filepath = args[0];
            numberOfHorses = Integer.parseInt(args[1]);
            numberOfAirplanes = Integer.parseInt(args[2]);
        }
        generate(filepath, numberOfHorses, numberOfAirplanes);
    }

    /**
     * This method generates the input data based on 3 params
     * Normal distribution is used for speed
     * Uniform distribution is used for everything else
     *
     * @param filepath          Name of the file
     * @param numberOfHorses    Number of horses
     * @param numberOfAirplanes Number of airplanes
     */
    private static void generate(String filepath, int numberOfHorses, int numberOfAirplanes) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filepath)))) {
            Random rand = new Random();
            int max = 1_000_000; // Maximum coordinate possible (Minimum is 0)

            // Comment :-) for funzies
            pw.println(":-) Generated data by Zappe and Mlady");
            // Paris X and Y
            pw.println(max * rand.nextDouble() + " " + max * rand.nextDouble());
            // How many horses
            pw.println(numberOfHorses);
            // Horses X, Y, Weight and Time to pickup
            for (int i = 0; i < numberOfHorses; i++) {
                double X = max * rand.nextDouble();
                double Y = max * rand.nextDouble();
                int weight = 1 + rand.nextInt(100); // src.Horse can weight from 1 to 100
                int time = 1 + rand.nextInt(20); // src.Horse can be picked up in 1 to 20 time units
                pw.println(X + " " + Y + " " + weight + " " + time);
            }
            // How many airplanes
            pw.println(numberOfAirplanes);
            // Airplanes X, Y, Capacity and Speed
            for (int i = 0; i < numberOfAirplanes; i++) {
                double X = max * rand.nextDouble();
                double Y = max * rand.nextDouble();
                int capacity = 101 + rand.nextInt(100); // Capacity must be 100 and more, to pickup even the heaviest horse, so the range is 100 to 200
                double gauss;
                do {
                    gauss = 1 + rand.nextGaussian(); // Cutting the number to be between 0 and 2 (around 1 has more probability)
                } while (!(gauss > 0) || !(gauss < 2)); // Cutting the number again if it was over 2 or below 0
                double speed = (1 + gauss) * 100; // Speed could be anywhere from 1 to any number, but since our coordinates are up to a million, we should make rather quicker airplanes
                pw.println(X + " " + Y + " " + capacity + " " + speed);
            }
        } catch (IOException e) {
            System.out.println("Error occured while trying to write into a file.");
            e.printStackTrace();
        }

    }

}
