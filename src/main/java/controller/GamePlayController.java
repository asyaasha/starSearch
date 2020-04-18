package controller;

import model.Database;
import model.FileParser;
import model.Simulation;
import view.GamePlayView;

import java.io.IOException;
import java.util.HashMap;

public class GamePlayController {
    private Simulation simulation;
    private Database db;
    private String user;
    private GamePlayView view;

    public  GamePlayController(GamePlayView view, Database db, String user, Simulation simulation) throws IOException {
        this.db = db;
        this.user = user;
        this.view = view;
        this.simulation = simulation;
    }

    public void nextStep() throws IOException {
        simulation.stepSimulation();
        db.saveAndUploadState(simulation, user);
        simulation = db.loadSimulationState(user, false);
        view.setSimulation(simulation);
    }

    public void previousStep() throws IOException {
        simulation = db.loadSimulationState(user, true);
    }

    public void stepForward() throws IOException {
        while (!simulation.status.equals("END_SIMULATION")) {
            simulation.stepSimulation();
            db.saveAndUploadState(simulation, user);

            simulation = db.loadSimulationState(user, false);
        }
    }

    public void saveAndQuit() throws IOException {
        db.saveAndUploadState(simulation, user);
    }
}
