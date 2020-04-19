package controller;

import javax.swing.*;

import model.Content;
import model.Database;
import model.Simulation;
import model.SpaceRegion;
import view.GamePlayView;

import static model.Simulation.END_STATUS;

public class GamePlayController {
    private Simulation simulation;
    private Database db;
    private String user;
    private GamePlayView view;
    private SpaceRegion virtualizedMap;


    public  GamePlayController(GamePlayView view, Database db, String user, Simulation simulation) {
        this.db = db;
        this.user = user;
        this.view = view;
        this.simulation = simulation;
        this.simulation.visualizeVirtualizedMap();
        this.virtualizedMap = simulation.getVirtualizedMap();
        try {
            db.uploadNewSimulation(user);
        } catch (Exception ignored) {}
    }

    public void nextStep() throws Exception {
        if (!simulation.status.equals(END_STATUS)) {
            simulation.stepSimulation();
            db.saveAndUploadState(simulation, user);
            simulation = db.loadSimulationState(user, false);
            view.setSimulation(simulation);
        } else {
            System.out.println("ERROR: SIMULATION HAS ALREADY ENDED");
        }
    }

    public void previousStep() throws Exception {
        simulation = db.loadSimulationState(user, true);
    }

    public void stepForward() throws Exception {
        simulation = db.loadSimulationState(user, false);
        while (!simulation.status.equals(END_STATUS)) {
            simulation.stepSimulation();
            db.saveAndUploadState(simulation, user);
            simulation = db.loadSimulationState(user, false);
        }
    }

    public void reset() throws Exception {
        db.resetSimulationState(user);
        System.out.println(simulation.status);
    }

    public void saveAndUpload() throws Exception {
        db.saveAndUploadState(simulation, user);
    }

    public void renderMap(JButton[][] squares){
        for(int y = 1; y < squares.length; y++) {
            for(int x = 1; x < squares[1].length; x++) {
                if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.DRONE) {
                    String orientation = String.valueOf(simulation.getVirtualizedMap().getSpaceLayout()[y][x].getOccupantDrone().getDroneOrientation());
                    System.out.println("orientation");
                    System.out.println(orientation);
                    squares[y][x].setIcon(view.droneIconsMap.get(orientation));
                    squares[y][x].setText("");
                } else if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.EMPTY) {
                    squares[y][x].setIcon(null);
                    squares[y][x].setText("");
                } else if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.STARS) {
                    squares[y][x].setIcon(view.imgStar);
                    squares[y][x].setText("");
                } else if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.SUN) {
                    squares[y][x].setText("");
                    squares[y][x].setIcon(view.imgSun);
                } else if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.UNKNOWN) {
                    squares[y][x].setIcon(null);
                    squares[y][x].setText("?");
                }
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