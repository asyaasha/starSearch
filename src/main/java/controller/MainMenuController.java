package controller;

import model.Database;
import model.FileParser;
import model.Simulation;
import view.MainMenuView;
import view.Scenario;

import java.util.HashMap;

public class MainMenuController {
    private MainMenuView view;
    private FileManager fileManager;
    private Database db;

    public MainMenuController(MainMenuView view) {
        this.view = view;
        db = new Database();
    }

    public void getStoredSim(String user) throws IllegalStateException {
        Simulation sim = db.loadSimulationState(user, false);
        view.setPrevSim(sim);
    }

    public Simulation getNewSimulation(String filename) {
        FileParser fileParser = new FileParser(filename);
        fileParser.generateInstructions();

        HashMap<String, String[]> globalInstructionSet = fileParser.getInstructionSet();
        return new Simulation(globalInstructionSet);
    }

    public Database getDb(){
        return this.db;
    }

    public void checkFileInput(String filename){
        fileManager = new FileManager(filename, new Scenario());

        // parse the file and check that all data is correct
        Boolean isStrategyInput = Boolean.TRUE;
        String isLocationTaken = null;

        if (!fileManager.processFilePath()){
            view.setMessage("The file path is incorrect, please try again");
        }
        else if (!fileManager.processRegion()){
            view.setMessage("Region input is out of the bound width 1-20, height 1-15");
        }
        else if (!fileManager.processDrones()){
            view.setMessage("Drone inputs are incorrect");
        }
        else if (!fileManager.processSuns()){
            view.setMessage("Suns input is out of the bound");
        }
        else if (!fileManager.processTurns()){
            view.setMessage("Turns input is out of the bound 1-200");
        }
        else if (!isStrategyInput){
            view.setMessage("Drone Strategy is incorrect, we only support 0 - random and 1 - best");
        }
        else if (isLocationTaken != null){
            view.setMessage("Location " + isLocationTaken + "is taken, please update the scenario");
        }
        else{
            view.setMessage("Success!");
        }
    }
}
