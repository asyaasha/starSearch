package view;
import controller.GamePlayController;
import model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
    private JPanel space;
    private GamePlayController controller;
    private Simulation sim;
    private SpaceRegion baseMap;

    private JLabel lblAction;
    private JLabel lblDetail;
    private JLabel lblDrone;
    private JLabel lbStatus;

    public GamePlayView(Simulation sim, Database db, String user) throws IOException {
        super("Start Search");
        controller = new GamePlayController(this, db, user, sim);
        this.sim = sim;
        this.baseMap = sim.getBaseMap();
        this.sim.visualizeVirtualizedMap();
        getContentPane().setVisible(false);
        getContentPane().setVisible(true);
        Border emptyBorder = BorderFactory.createEmptyBorder(40, 70, 20 , 70);

        // Setting the main layout type
        setLayout(new BorderLayout());

        space = new JPanel();
        space.setBackground(new Color(133, 185, 230));
        space.setBorder(emptyBorder);
        controller.renderMap(space);
        add(space, BorderLayout.EAST);

        // TABLE PANEL TO DISPLAY PROGRESS
        pnlProgress = new JPanel();
        pnlProgress.setBorder(emptyBorder);
        pnlProgress.setBackground(new Color(133, 185, 230));
        pnlProgress.setLayout(new GridLayout(10, 1));

        JLabel lblTitle = new JLabel("--- CURRENT PROGRESS --- ");

        lblAction = new JLabel("");
        pnlProgress.add(lblTitle);
        pnlProgress.add(lblAction);
        controller.setProgress(lblAction);

        add(pnlProgress, BorderLayout.WEST);

        // GAME PLAY CONTROL PANEL
        pnlGameControlls = new JPanel();
        pnlGameControlls.setBackground(new Color(158, 178, 178));
        pnlGameControlls.setBorder(emptyBorder);
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
        getContentPane().setBackground(new Color(133, 185, 230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setSimulation(Simulation sim){
        this.sim = sim;
    }

    /* Calls updateUI on all sub-components of the JFrame */
    private void updateUI() {
        SwingUtilities.updateComponentTreeUI(this);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        System.out.println(command);

        // Perform next step
        if (command.equals(commandNext)) {
            try {
                controller.nextStep();
                controller.setProgress(lblAction);
                //controller.renderMap(space);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandForward)) {
            try {
                controller.stepForward();
                //controller.renderMap(space);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandBack)) {
            try {
                controller.previousStep();
                controller.setProgress(lblAction);
                //controller.renderMap(space);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandStop)) {
            //save state and upload
            try {
                controller.saveAndUpload();
                //controller.renderMap(space);
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