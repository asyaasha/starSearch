package view;
import controller.GamePlayController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class GamePlayView extends JFrame implements ActionListener  {
    private String commandNext = "NEXT";
    private String commandBack = "BACK";
    private String commandStop = "STOP";
    private String commandForward = "FORWARD";

    private JButton[][] squares;
    private JButton btnBack;
    private JButton btnStop;
    private JButton btnForward;
    private JPanel pnlProgress;
    private JPanel pnlGameControlls;
    private String filePath;
    private Scenario scenario;
    private GamePlayController controller;
    private Simulation sim;
    private SpaceRegion baseMap;
    private SpaceRegion virtualizedMap;

    public GamePlayView(Simulation sim, Database db, String user) throws IOException {
        super("Start Search");
        controller = new GamePlayController(this, db, user, sim);
        this.sim = sim;
        this.baseMap = this.sim.getBaseMap();
        this.sim.visualizeVirtualizedMap();
        this.virtualizedMap = this.sim.getVirtualizedMap();


        // Setting the main layout type
        setLayout(new BorderLayout());

        // SPACE REGION PANEL  --> check the logic from simulation visualize function
        JPanel space = new JPanel();
        space.setLayout(new GridLayout(this.virtualizedMap.getLength(), this.virtualizedMap.getWidth()));
        squares = new JButton[this.virtualizedMap.getWidth()][this.virtualizedMap.getLength()];


        for(int y = 0; y < squares.length; y++) {
            for(int x = 0; x < squares[y].length; x++) {
                if (this.virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.SUN) {
                    squares[y][x] = new JButton("S");
                } else {
                    squares[y][x] = new JButton("o");
                }
                space.add(squares[y][x]);
            }
        }

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandForward)) {
            try {
                controller.stepForward();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandBack)) {
            try {
                controller.previousStep();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandStop)) {
        }

        revalidate();
    }
}