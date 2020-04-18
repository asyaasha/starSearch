package controller;

import java.awt.*;
import java.io.IOException;

import model.Content;
import model.Database;
import model.Simulation;
import model.SpaceRegion;
import view.GamePlayView;

import javax.swing.*;

public class GamePlayController {
    private Simulation simulation;
    private Database db;
    private String user;
    private GamePlayView view;
<<<<<<< HEAD
    //private SpaceRegion virtualizedMap;
=======
    private SpaceRegion virtualizedMap;

>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7

    public  GamePlayController(GamePlayView view, Database db, String user, Simulation simulation) throws IOException {
        this.db = db;
        this.user = user;
        this.view = view;
        this.simulation = simulation;
        this.simulation.visualizeVirtualizedMap();
        //this.virtualizedMap = simulation.getVirtualizedMap();
    }

    public void nextStep() throws IOException {
        if (!simulation.status.equals("END_SIMULATION")) {
            simulation.stepSimulation();
            db.saveAndUploadState(simulation, user);
            simulation = db.loadSimulationState(user, false);
            view.setSimulation(simulation);
        } else {
            System.out.println("ERROR: SIMULATION HAS ALREADY ENDED");
        }
    }

    public void previousStep() throws IOException {
        simulation = db.loadSimulationState(user, true);
    }

    public void stepForward() throws IOException {
        simulation = db.loadSimulationState(user, false);
        System.out.println(simulation.status);
        while (!simulation.status.equals("END_SIMULATION")) {
            simulation.stepSimulation();
            db.saveAndUploadState(simulation, user);
            simulation = db.loadSimulationState(user, false);
        }
    }

    public void saveAndUpload() throws IOException {
        db.saveAndUploadState(simulation, user);
    }

<<<<<<< HEAD
    public void renderInitialMap(JPanel space, JButton[][] squares, SpaceRegion virtualizedMap){
        for(int y = 1; y < squares.length; y++) {
            for(int x = 1; x < squares[1].length; x++) {
                if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.BARRIER) {
                    squares[y][x] = new JButton("B");
                } else if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.DRONE) {
                    squares[y][x] = new JButton(String.valueOf(virtualizedMap.getSpaceLayout()[y][x].getOccupantDrone().getDroneID()));
                } else if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.EMPTY) {
                    squares[y][x] = new JButton("");
                } else if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.STARS) {
                    squares[y][x] = new JButton("+");
                } else if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.SUN) {
                    squares[y][x] = new JButton("*");
                } else if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.UNKNOWN) {
                    squares[y][x] = new JButton("?");
                }
                space.add(squares[y][x]);
            }
        }
    }

    public void renderMap(JPanel space, JButton[][] squares, SpaceRegion virtualizedMap){
        //space.setLayout(new GridLayout(virtualizedMap.getLength(), virtualizedMap.getWidth()));
        //squares = new JButton[virtualizedMap.getLength() + 1][virtualizedMap.getWidth() + 1];

=======
    public void renderMap(JButton[][] squares){
>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7
        for(int y = 1; y < squares.length; y++) {
            for(int x = 1; x < squares[1].length; x++) {
                if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.BARRIER) {
                    squares[y][x].setText("B");
                } else if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.DRONE) {
                    squares[y][x].setText("D");
                } else if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.EMPTY) {
                    squares[y][x].setText("");
                } else if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.STARS) {
                    squares[y][x].setText("+");
                } else if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.SUN) {
                    squares[y][x].setText("*");
                } else if (simulation.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.UNKNOWN) {
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
