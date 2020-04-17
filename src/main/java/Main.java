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

    public static void main(String[] args) throws Exception {
        //FOR TESTING
        final String USER_ID = "test-user-id";

        String userId = USER_ID;

        String filePath = args[0];

        FileParser fileParser = new FileParser(filePath);
        fileParser.generateInstructions();

        HashMap<String, String[]> globalInstructionSet = fileParser.getInstructionSet();

        //Start Simulation
        Database db = new Database();

        Simulation simulation = new Simulation(globalInstructionSet);
        simulation.stepSimulation();
        db.saveAndUploadState(simulation, userId);

        simulation = db.loadSimulationState(userId, false);

        while (!simulation.status.equals("END_SIMULATION")) {
            simulation.stepSimulation();
            db.saveAndUploadState(simulation, userId);

            simulation = db.loadSimulationState(userId, false);
        }

        ArrayList<String> fullLog = simulation.endSimulation();

        fileParser.writeToFile(fullLog);
    }
}
