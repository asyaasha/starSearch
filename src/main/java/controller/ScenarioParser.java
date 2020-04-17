package controller;


import view.Scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScenarioParser {
    private HashMap<String, String[]> instructionSet;
    private Scenario scenario;

    /**
     * Constructor for the ScenarioParser object
     *
     * @param newScenario  Scenario contains new scenario configurations
     * List<String> dronesList;
     * List<String> sunsList;
     * int numOfDrones;
     * int numOfSuns;
     * String width;
     * String height;
     * String turns;
     */
    public ScenarioParser(Scenario newScenario)
    {
        this.instructionSet = new HashMap<>();
        this.scenario = newScenario;
    }

    /**
     * Generates instruction set
     */
    public void generateInstructions() {
        String[] dimensions = new String[]{String.valueOf(scenario.getWidth()), String.valueOf(scenario.getHeight())};
        instructionSet.put("Dimensions", dimensions);

        String[] droneLocations = new String[scenario.getNumOfDrones()];
        List<String> drones = scenario.getDronesList();

        for (int i = 0; i < droneLocations.length; i++) {
            droneLocations[i] = drones.get(i);
        }
        instructionSet.put("DroneLocations", droneLocations);

        if (scenario.getNumOfSuns() > 0) {
            String[] obstacleLocations = new String[scenario.getNumOfSuns()];
            List<String> suns = scenario.getDronesList();

            for (int i = 0; i < obstacleLocations.length; i++) {
                obstacleLocations[i] = suns.get(i);
            }
            instructionSet.put("ObstacleLocations", obstacleLocations);
            instructionSet.put("MaxTurns", new String[]{scenario.getTurns()});
        }
    }

    /**
     * Returns instruction set
     * @return
     */
    public HashMap<String, String[]> getInstructionSet() {
        return this.instructionSet;
    }

    /**
     * Write all information to CSV
     * @param logInfo
     */
    public void writeToFile(ArrayList<String> logInfo) {
        /*try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.substring(0, filePath.length() - 4) + "_results.csv"));
            for (String line:logInfo) {
                writer.write(line);
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Something went horribly wrong :).");
        }*/
        for (String line:logInfo) {
            System.out.println(line);
        }
    }
}