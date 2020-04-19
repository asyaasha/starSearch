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
     * File Manager class that validates file inputs
     */
    private Scenario scenario;
    private String filePath;
    private List<String> data;
    private int nextValidLine;

    /**
     * Constructor for the FileManager object
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
        int droneNum = Integer.parseInt(data.get(nextValidLine));
        scenario.setNumOfDrones(droneNum);

        if (droneNum > 0 && scenario.checkDronesNum()) {
            //We have some number of drones
            String[] droneLocations = new String[droneNum];

            for (int i = 0; i < droneLocations.length; i++) {
                int droneLocationLine = nextValidLine + i + 1; // Add one to get to next line
                try {
                    scenario.addToDronesList(data.get(droneLocationLine));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return scenario.checkDronesNum();
    }

    public Boolean processSuns() {
        int num = Integer.parseInt(data.get(nextValidLine));
        scenario.setNumOfSuns(num);

        if (num == 0) {
            nextValidLine = 3;
        } else {
            nextValidLine = nextValidLine + num + 1;
        }

        if (num > 0 && scenario.checkSuns()) {
            //We have some number of obstacles(ONLY SUN FOR THIS PHASE OF PROJECT)
            //model.Obstacle Instruction Container
            String[] obstacleLocations = new String[Integer.parseInt(data.get(nextValidLine))];

            for (int i = 0; i < obstacleLocations.length; i++) {
                int obstacleLocationLine = nextValidLine + i + 1; // Add one to get to next line
                try {
                    scenario.addToSunsList(data.get(obstacleLocationLine));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        data.removeAll(Arrays.asList(""));
        return scenario.checkSuns();
    }

    public Boolean processTurns() {
        scenario.setTurns(data.get(data.size() - 1));
        return scenario.checkTurns();
    }
}
