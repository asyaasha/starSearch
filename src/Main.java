import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dennis Eddington
 *
 * Main class for Star Search Simulation. Drives logic for the program through calls to Simulation and FileParser
 * @see FileParser
 * @see Simulation
 */
public class Main {

    public static void main(String[] args) {
        String filePath = args[0];

        FileParser fileParser = new FileParser(filePath);
        fileParser.generateInstructions();

        HashMap<String, String[]> globalInstructionSet = fileParser.getInstructionSet();

        //Start Simulation


        Simulation simulation = new Simulation(globalInstructionSet);
        simulation.runSimulation();
        ArrayList<String> fullLog = simulation.endSimulation();

        fileParser.writeToFile(fullLog);
    }
}
