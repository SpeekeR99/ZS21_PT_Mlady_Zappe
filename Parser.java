import java.io.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Parser object for parsing the input data into wanted structures
 * with wanted values
 */
public class Parser {

    /**
     * Buffered Reader for reading the file
     */
    private final BufferedReader br;

    /**
     * Constructor simply initializes the buffered reader
     *
     * @param filepath Path to a file that is wanted to be read
     * @throws FileNotFoundException File might not exist
     */
    public Parser(String filepath) throws FileNotFoundException {
        this.br = new BufferedReader(new FileReader(filepath));
    }

    /**
     * This method can be called from the outside to get the data from the input file
     * as ArrayList of Doubles
     *
     * @return ArrayList of doubles
     */
    public AbstractList<Double> getInput() {
        AbstractList<String> stringData = processInput();
        AbstractList<Double> doubleData = new ArrayList<>();
        for (String s : stringData) {
            doubleData.add(Double.parseDouble(s));
        }
        return doubleData;
    }

    /**
     * Processes the input file
     * Ignores everything after comments ":-)"
     * Skips lines with nothing on them
     *
     * @return ArrayList of data from the input file as Strings
     */
    private AbstractList<String> processInput() {
        AbstractList<String> inputData = new ArrayList<>();
        try {
            String line = br.readLine();
            while (line != null) {
                if (line.length() == 0) {
                    line = br.readLine();
                    continue;
                }
                String[] lineSplit = line.trim().split("\\s+");
                boolean hasComment = false;
                int index = 0;
                for (int i = 0; i < lineSplit.length; i++) {
                    String comment = ":-)";
                    if (lineSplit[i].equals(comment) && !hasComment) {
                        hasComment = true;
                        index = i;
                    }
                }
                if (hasComment) {
                    String[] temp = new String[index];
                    inputData.addAll(Arrays.asList(lineSplit).subList(0, temp.length));
                } else {
                    Collections.addAll(inputData, lineSplit);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Nastal problém při čtení ze souboru.");
            e.printStackTrace();
        }
        return inputData;
    }

}
