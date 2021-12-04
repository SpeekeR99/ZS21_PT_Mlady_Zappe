import java.io.*;
import java.util.AbstractList;

/**
 * src.Generator for statistics
 * Report from all airplanes, which horses when and where it took, statistics of weight capacity, time of flying and waiting
 * Report from all horses, where and how it travelled, how soon it was in Paris
 * Total time of all flights, all waitings and total time of moving
 */
public class Statistics {

    /**
     * Airplanes
     */
    private final Aircraft[] airplanes;
    /**
     * Nodes in order of being flown to
     */
    private final AbstractList<GraphNode> nodesInOrder;
    /**
     * src.Statistics Directory
     */
    private final String STATISTICS_DIR = "statistics/";
    /**
     * Starting time of olympic games
     */
    private final long olympicsStartTime;
    /**
     * Name of input file
     */
    private final String inputFileName;
    /**
     * Sum of all flight times
     */
    private long sumOfAllFlightTimes;
    /**
     * Sum of all waiting times
     */
    private long sumOfAllWaitingTimes;

    /**
     * Constructor sets the arraylist
     *
     * @param airplanes         array of all airplanes
     * @param nodesInOrder      arraylist of nodes in order of being flown to
     * @param olympicsStartTime starting time of olympic games
     * @param inputFile         inputFile name, for name generation of statistics file
     */
    public Statistics(Aircraft[] airplanes, AbstractList<GraphNode> nodesInOrder, double olympicsStartTime, String inputFile) {
        this.airplanes = airplanes;
        this.nodesInOrder = nodesInOrder;
        this.olympicsStartTime = Math.round(olympicsStartTime);
        this.inputFileName = inputFile.replace(".txt", "").replace("data/", "");
        this.sumOfAllFlightTimes = 0;
        this.sumOfAllWaitingTimes = 0;
    }

    /**
     * Generates all three statistic files
     */
    public void generateStats() {
        generateAirplaneStats();
        generateHorsesStats();
        generateTimeStats();
    }

    /**
     * Report from all airplanes, which horses when and where it took, statistics of weight capacity, time of flying and waiting
     */
    private void generateAirplaneStats() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(STATISTICS_DIR + inputFileName + "_airplanes_statistics.txt")))) {

            for (int i = 0; i < airplanes.length; i++) {
                Aircraft airplane = airplanes[i];
                pw.println("Letoun " + i);
                int weight = 0;
                long prostoje = 0;
                for (int j = 0; j < nodesInOrder.size() - 1; j++) {
                    GraphNode h = nodesInOrder.get(j);
                    if (h instanceof Horse && ((Horse) h).transportedBy == airplane) {
                        if (weight + ((Horse) h).weight > airplane.weightCapacity) {
                            weight = 0;
                            pw.println("\tTed vylozil vsechny predchazejici kone v parizi.");
                        }
                        weight += ((Horse) h).weight;
                        prostoje += ((Horse) h).time * 2; // * 2 because src.Horse is loaded AND unloaded with the same time needed

                        pw.println("\tNabral kone " + ((Horse) h).index + " v case: " + ((Horse) h).timePickUp +
                                " a pokracoval v ceste na: X = " + nodesInOrder.get(j + 1).x + " | Y = " + nodesInOrder.get(j + 1).y +
                                " a zatizeni v tu chvili mel: " + weight);
                        if (weight >= airplane.weightCapacity) {
                            weight = 0;
                            pw.println("\tTed vylozil vsechny predchazejici kone v parizi.");
                        }
                    }
                }
                pw.println("\tDoba letu: " + airplane.endOfFlightTime + " a doba prostoju: " + prostoje);
                sumOfAllFlightTimes += airplane.endOfFlightTime;
                sumOfAllWaitingTimes += prostoje;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Report from all horses, where and how it travelled, how soon it was in Paris
     */
    private void generateHorsesStats() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(STATISTICS_DIR + inputFileName + "_horses_statistics.txt")))) {

            for (GraphNode h : nodesInOrder) {
                if (h instanceof Horse) {
                    pw.println("Kun " + ((Horse) h).index);
                    StringBuilder coords = new StringBuilder();
                    for (GraphNode beenAt : ((Horse) h).beenThrough) {
                        coords.append(" | X = ").append(beenAt.x).append(" Y = ").append(beenAt.y);
                    }
                    pw.println("\tCestoval pres: " + coords + " .");
                    long cas = ((Horse) h).timeDroppedInParis - ((Horse) h).timePickUp;
                    pw.println("\tByl na ceste: " + cas + " casovych jednotek.");
                    long cekal = olympicsStartTime - ((Horse) h).timeDroppedInParis;
                    pw.println("\tNa zacatek olympiady cekal v Parizi " + cekal + " casovych jednotek.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Total time of all flights, all waitings and total time of moving
     */
    private void generateTimeStats() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(STATISTICS_DIR + inputFileName + "_times_statistics.txt")))) {

            pw.println("Celkova doba vsech letu: " + sumOfAllFlightTimes);
            pw.println("Celkova doba prostoju: " + sumOfAllWaitingTimes);
            pw.println("Celkova doba prepravy: " + (sumOfAllFlightTimes - sumOfAllWaitingTimes));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
