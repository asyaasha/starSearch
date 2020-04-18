package controller;

import java.io.IOException;
import model.Database;
import model.Simulation;
import view.GamePlayView;

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
}
