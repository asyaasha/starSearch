package controller;

import javax.swing.*;

import model.Content;
import model.Database;
import model.Simulation;
import model.SpaceRegion;
import view.GamePlayView;
import model.FileParser;

import static model.Simulation.END_STATUS;

public class GamePlayController {
    private Simulation simulation;
    private Database db;
    private String user;
    private GamePlayView view;
    private SpaceRegion virtualizedMap;
    private String summaryReport;
    private FileParser fileParser = new FileParser("exported");


    public  GamePlayController(GamePlayView view, Database db, String user, Simulation simulation) {
        this.db = db;
        this.user = user;
        this.view = view;
        this.simulation = simulation;
        this.simulation.visualizeVirtualizedMap();
        this.virtualizedMap = simulation.getVirtualizedMap();
    }

    public void nextStep() throws Exception {
        if (!simulation.status.equals(END_STATUS)) {
            simulation.stepSimulation();
            db.saveAndUploadState(simulation, user);
            simulation = db.loadSimulationState(user, false);
            view.setSimulation(simulation);
            view.setStatusMessage(simulation.status);
        } else {
            System.out.println("ERROR: SIMULATION HAS ALREADY ENDED");
            fileParser.writeToFile(simulation.endSimulation());
            this.summaryReport = simulation.displaySummaryReport();
            view.statsDisplay(summaryReport);
        }
    }

    public void previousStep() throws Exception {
        simulation = db.loadSimulationState(user, true);
        view.setStatusMessage(simulation.status);
    }

    public void stepForward() throws Exception {
        simulation = db.loadSimulationState(user, false);
        System.out.println(simulation.status);
        while (!simulation.status.equals(END_STATUS)) {
            simulation.stepSimulation();
            db.saveAndUploadState(simulation, user);
            simulation = db.loadSimulationState(user, false);
            view.setStatusMessage(simulation.status);
        }
        fileParser.writeToFile(simulation.endSimulation());
        this.summaryReport = simulation.displaySummaryReport();
        view.statsDisplay(summaryReport);
    }

    public void reset() throws Exception {
        db.resetSimulationState(user);
        view.setStatusMessage(simulation.status);
    }

    public void saveAndUpload() throws Exception {
        db.saveAndUploadState(simulation, user);
        System.out.println(simulation.status);
    }

    public void renderMap(JButton[][] squares){
        for(int y = 1; y < squares.length; y++) {
            for(int x = 1; x < squares[1].length; x++) {
                if (simulation.getBaseMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.DRONE) {
                    String orientation = String.valueOf(simulation.getBaseMap().getSpaceLayout()[y][x].getOccupantDrone().getDroneOrientation());
                    squares[y][x].setIcon(view.droneIconsMap.get(orientation));
                    squares[y][x].setText(String.valueOf(simulation.getBaseMap().getSpaceLayout()[y][x].getOccupantDrone().getDroneID()));
                } else if (simulation.getBaseMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.EMPTY) {
                    squares[y][x].setText("");
                    if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getExplorationStatus() == true) {
                        squares[y][x].setIcon(null);
                    } else {
                        squares[y][x].setIcon(view.imgStar);
                        if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.UNKNOWN) {
                            squares[y][x].setText("?");
                        }
                    }
                } else if (simulation.getBaseMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.STARS) {
                    squares[y][x].setIcon(view.imgStar);
                    squares[y][x].setText("");
                } else if (simulation.getBaseMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.SUN) {
                    squares[y][x].setText("");
                    squares[y][x].setIcon(view.imgSun);
                }
                /*
                else if (simulation.getBaseMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.UNKNOWN) {
                    squares[y][x].setIcon(null);
                    //squares[y][x].setText("?");
                }
                */
            }
        }
    }

    public void setProgress(JLabel lblAction) {
        // Action
        String row = "";

        for (int i = 0; i < simulation.getStepProgress().size(); i++) {
            row += simulation.getStepProgress().get(i) + "<br/>";
        }
        String progressFull = "<html>" + row + "<html>";
        lblAction.setText(progressFull);
    }

}