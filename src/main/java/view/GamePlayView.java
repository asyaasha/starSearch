package view;
import controller.GamePlayController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static com.sun.javafx.fxml.expression.Expression.add;


public class GamePlayView extends JFrame implements ActionListener  {
    private String commandNext = "NEXT";
    private String commandBack = "BACK";
    private String commandStop = "STOP";
    private String commandForward = "FORWARD";

    private JButton btnBack;
    private JButton btnStop;
    private JButton btnForward;
    private JPanel pnlProgress;
    private JPanel pnlGameControlls;
    private JPanel space = new JPanel();
    private GamePlayController controller;
    private Simulation sim;
    private SpaceRegion baseMap;

    public GamePlayView(Simulation sim, Database db, String user) throws IOException {
        super("Start Search");
        controller = new GamePlayController(this, db, user, sim);
        this.sim = sim;
        this.baseMap = sim.getBaseMap();
        this.sim.visualizeVirtualizedMap();

        // Setting the main layout type
        setLayout(new BorderLayout());
        controller.renderInitialMap(space);
        add(space, BorderLayout.EAST);

        // TABLE PANEL TO DISPLAY PROGRESS
        pnlProgress = new JPanel();
        pnlProgress.setLayout(new GridLayout(10, 1));
        JLabel lblTable = new JLabel("<html>Progress...<br/>Region size: <br/></html>");
        JLabel lblTable1 = new JLabel(baseMap.getWidth() + " * " + baseMap.getLength());

        pnlProgress.add(lblTable);
        pnlProgress.add(lblTable1);
        add(pnlProgress, BorderLayout.WEST);

        // GAME PLAY CONTROL PANEL
        pnlGameControlls = new JPanel();
        pnlGameControlls.setLayout(new GridLayout(1, 3));

        btnBack = new JButton(commandStop);
        btnBack.addActionListener((ActionListener) this);
        pnlGameControlls.add(btnBack);

        btnBack = new JButton(commandBack);
        btnBack.addActionListener((ActionListener) this);
        pnlGameControlls.add(btnBack);

        btnStop = new JButton(commandNext);
        btnStop.addActionListener((ActionListener) this);
        pnlGameControlls.add(btnStop);

        btnForward = new JButton(commandForward);
        btnForward.addActionListener((ActionListener) this);
        pnlGameControlls.add(btnForward);

        add(pnlGameControlls, BorderLayout.SOUTH);

        revalidate();

        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setSimulation(Simulation sim){
        this.sim = sim;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        System.out.println(command);

        // Perform next step
        if (command.equals(commandNext)) {
            try {
                controller.nextStep();
                controller.renderMap(space);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandForward)) {
            try {
                controller.stepForward();
                controller.renderMap(space);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandBack)) {
            try {
                controller.previousStep();
                controller.renderMap(space);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandStop)) {
            //save state and upload
            try {
                controller.saveAndUpload();
                controller.renderMap(space);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //close window and open login
            dispose();
            new LoginView();
        }

        revalidate();
    }
}