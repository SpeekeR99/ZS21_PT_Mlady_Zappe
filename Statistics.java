import java.io.*;
import java.util.AbstractList;

/**
 * Generator for statistics
 * Report from all airplanes, which horses when and where it took, statistics of weight capacity, time of flying and waiting
 * Report from all horses, where and how it travelled, how soon it was in Paris
 * Total time of all flights, all waitings and total time of moving
 */
public class Statistics {

    /** Nodes in order of being flown to */
    private final AbstractList<GraphNode> nodesInOrder;
    /** Statistics Directory */
    private final String STATISTICS_DIR = "statistics/";
    /** Starting time of olympic games */
    private final long olympicsStartTime;
    /** Name of input file */
    private final String inputFileName;

    /**
     * Constructor sets the arraylist
     * @param nodesInOrder arraylist of nodes in order of being flown to
     * @param olympicsStartTime starting time of olympic games
     */
    public Statistics(AbstractList<GraphNode> nodesInOrder, double olympicsStartTime, String inputFile) {
        this.nodesInOrder = nodesInOrder;
        this.olympicsStartTime = Math.round(olympicsStartTime);
        this.inputFileName = inputFile.replace(".txt", "").replace("data/", "");
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
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(STATISTICS_DIR+inputFileName+"_airplanes_statistics.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * Report from all horses, where and how it travelled, how soon it was in Paris
     */
    private void generateHorsesStats() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(STATISTICS_DIR+inputFileName+"_horses_statistics.txt")));

            for(GraphNode h : nodesInOrder) {
                if (h instanceof Horse) {
                    pw.println("Kun "+((Horse) h).index);
                    String coords = "";
                    for(GraphNode beenAt : ((Horse) h).beenThrough) {
                        coords = coords + " | X = " + beenAt.x + " Y = " + beenAt.y;
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
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * Total time of all flights, all waitings and total time of moving
     */
    private void generateTimeStats() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(STATISTICS_DIR+inputFileName+"_times_statistics.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

}
