package controller;

import view.MainMenuView;
import view.Scenario;

public class MainMenuController {
    private MainMenuView view;
    private FileManager fileManager;

    public MainMenuController(MainMenuView view) {
        this.view = view;
        // model ..?
    }

    public void checkFileInput(String filename){
        fileManager = new FileManager(filename, new Scenario());

        // parse the file and check that all data is correct
        Boolean isFileInput = fileManager.processFilePath();
        Boolean isRegionInput = fileManager.processRegion();
        Boolean isDronesInput = fileManager.processDrones();
        Boolean isSunsInput = fileManager.processSuns();
        Boolean isTurnsInput = fileManager.processTurns();
        Boolean isStrategyInput = Boolean.TRUE;
        String isLocationTaken = null;
        System.out.println("Region " + isRegionInput);
        if (!isFileInput){
            view.setMessage("The file path is incorrect, please try again");
        }
        else if (!isRegionInput){
            view.setMessage("Region input is out of the bound width 1-20, height 1-15");
        }
        else if (!isDronesInput){
            view.setMessage("Drones");
        }
        else if (!isSunsInput){
            view.setMessage("Suns input is out of the bound");
        }
        else if (!isTurnsInput){
            view.setMessage("Turns input is out of the bound 1-200");
        }
        else if (!isStrategyInput){
            view.setMessage("model.Drone Strategy is incorrect, we only support 0 - random and 1 - best");
        }
        else if (isLocationTaken != null){
            view.setMessage("Location " + isLocationTaken + "is taken, please update the scenario");
        }
        else{
            view.setMessage("Success!");
        }
    }

    public Scenario getScenario() {
        return fileManager.getScenario();
    }
}
