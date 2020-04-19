package controller;

import view.Scenario;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileManager {

    /**
     * Created by Dennis Eddington updated by Asiya
     *
     * File Parser class that goes through the requested file and parses all pertinent information to test the requirements and pass to a new instance of Scenario class
     */
    private Scenario scenario;
    private String filePath;
    private List<String> data;
    private int nextValidLine;

    /**
     * Constructor for the FileParser object
     *
     * @param filePath  FilePath of file to parse
     */
    public FileManager(String filePath, Scenario newScenario) {
        this.filePath = filePath;
        this.scenario = newScenario;
    }

    /**
     * Check that path is correct
     */
    public Boolean processFilePath() {
        data = new ArrayList<>();

        System.out.println("HERE");
        System.out.println(this.filePath.length() < 1);
        System.out.println(this.filePath);

        if (this.filePath.length() < 1) {
            return Boolean.FALSE;
        }
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(this.filePath));
            String row;
            while ((row = fileReader.readLine()) != null) {
                data.add(row);
            }
            fileReader.close();
            return Boolean.TRUE;
        }
        catch (IOException exception) {
            System.out.println("IOException has occurred.");
            return Boolean.FALSE;
        }
    }

    public Boolean processRegion() {
        int width = Integer.parseInt(data.get(0));
        int height = Integer.parseInt(data.get(1));
        Boolean isVaild = scenario.checkRegion(width, height);

        if (isVaild) {
            scenario.setRegion(width, height);
        }

        return isVaild;
    }

    public Boolean processDrones() {
        nextValidLine = 2;

        if (Integer.parseInt(data.get(nextValidLine)) > 0) {
            //We have some number of drones
            String[] droneLocations = new String[Integer.parseInt(data.get(nextValidLine))];

            for (int i = 0; i < droneLocations.length; i++) {
                int droneLocationLine = nextValidLine + i + 1; // Add one to get to next line
                scenario.addToDronesList(data.get(droneLocationLine));
            }
        }

        return scenario.checkDrones();
    }

    public Boolean processSuns() {
        int num = Integer.parseInt(data.get(nextValidLine));
        scenario.setNumOfSuns(num);

        if (num == 0) {
            nextValidLine = 3;
        } else {
            nextValidLine = nextValidLine + num + 1;
        }

        if (Integer.parseInt(data.get(nextValidLine)) > 0) {
            //We have some number of obstacles(ONLY SUN FOR THIS PHASE OF PROJECT)
            //model.Obstacle Instruction Container
            String[] obstacleLocations = new String[Integer.parseInt(data.get(nextValidLine))];

            for (int i = 0; i < obstacleLocations.length; i++) {
                int obstacleLocationLine = nextValidLine + i + 1; // Add one to get to next line
                scenario.addToSunsList(data.get(obstacleLocationLine));
            }
        }
        data.removeAll(Arrays.asList(""));
        return scenario.checkSuns();
    }

    public Boolean processTurns() {
        scenario.setTurns(data.get(data.size() - 1));
        return scenario.checkTurns();
    }
    public Scenario getScenario() {
        return scenario;
    }
}
