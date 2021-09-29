import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Parser {

    private final BufferedReader br;

    public Parser(String filepath) throws FileNotFoundException {
        this.br = new BufferedReader(new FileReader(filepath));
    }

    public ArrayList<Double> getInput() {
        ArrayList<String> stringData = processInput();
        ArrayList<Double> doubleData = new ArrayList<>();
        for(String s : stringData) {
            doubleData.add(Double.parseDouble(s));
        }
        return doubleData;
    }

    private ArrayList<String> processInput() {
        ArrayList<String> inputData = new ArrayList<>();
        try {
            String line = br.readLine();
            while(line != null) {
                if(line.length() == 0) {
                    line = br.readLine();
                    continue;
                }
                String[] lineSplit = line.trim().split("\\s+");
                boolean hasComment = false;
                int index = 0;
                for(int i = 0; i < lineSplit.length; i++) {
                    String comment = ":-)";
                    if(lineSplit[i].equals(comment)) {
                        if(!hasComment) {
                            hasComment = true;
                            index = i;
                        }
                    }
                }
                if(hasComment) {
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
