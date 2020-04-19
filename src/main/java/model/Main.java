package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dennis Eddington
 *
 * model.Main class for model.Star Search model.Simulation. Drives logic for the program through calls to model.Simulation and model.FileParser
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

        //Start model.Simulation
        Database db = new Database();

        Simulation simulation = new Simulation(globalInstructionSet);
        simulation.stepSimulation();
        db.saveAndUploadState(simulation, userId);

        simulation = db.loadSimulationState(userId, false);

//        while (!simulation.status.equals(Simulation.END_STATUS)) {
//            simulation.stepSimulation();
//            db.saveAndUploadState(simulation, userId);
//
//            simulation = db.loadSimulationState(userId, false);
//        }

        ArrayList<String> fullLog = simulation.endSimulation();

        fileParser.writeToFile(fullLog);
    }
}
