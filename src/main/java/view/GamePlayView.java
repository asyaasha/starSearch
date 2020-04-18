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

<<<<<<< HEAD

=======
>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7
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
<<<<<<< HEAD
    private JPanel space = new JPanel();
    private JButton[][] squares;
=======
    private JPanel space;
>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7
    private GamePlayController controller;
    private Simulation sim;
    private SpaceRegion baseMap;
    private SpaceRegion virtualizedMap;
<<<<<<< HEAD
=======
    private JButton[][] squares;

    private JLabel lblAction;
    private JLabel lblDetail;
    private JLabel lblDrone;
    private JLabel lbStatus;
>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7

    public GamePlayView(Simulation sim, Database db, String user) throws IOException {
        super("Start Search");
        controller = new GamePlayController(this, db, user, sim);
        this.sim = sim;
        this.baseMap = sim.getBaseMap();
<<<<<<< HEAD
        this.sim.visualizeVirtualizedMap();
        this.virtualizedMap = sim.getVirtualizedMap();

        // Setting the main layout type
        setLayout(new BorderLayout());
        space.setLayout(new GridLayout(virtualizedMap.getLength(), virtualizedMap.getWidth()));
        squares = new JButton[virtualizedMap.getLength() + 1][virtualizedMap.getWidth() + 1];
        controller.renderInitialMap(space, squares, virtualizedMap);
=======
        //this.sim.visualizeVirtualizedMap();
        this.virtualizedMap = sim.getVirtualizedMap();

        getContentPane().setVisible(false);
        getContentPane().setVisible(true);
        Border emptyBorder = BorderFactory.createEmptyBorder(40, 70, 20 , 70);

        // Setting the main layout type
        setLayout(new BorderLayout());

        space = new JPanel();
        space.setLayout(new GridLayout(virtualizedMap.getLength(), virtualizedMap.getWidth()));

        space.setBackground(new Color(133, 185, 230));
        space.setBorder(emptyBorder);
        //controller.renderInitialMap(space);
        space.setLayout(new GridLayout(virtualizedMap.getLength(), virtualizedMap.getWidth()));
        squares = new JButton[virtualizedMap.getLength() + 1][virtualizedMap.getWidth() + 1];

        for(int y = 1; y < squares.length; y++) {
            for(int x = 1; x < squares[1].length; x++) {
                if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.BARRIER) {
                    squares[y][x] = new JButton("B");
                } else if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.DRONE) {
                    squares[y][x] = new JButton("D");
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
>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7
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

    //Calls updateUI on all sub-components of the JFrame
    private void updateUI(){ 
        SwingUtilities.updateComponentTreeUI(this); 
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
<<<<<<< HEAD
                controller.renderMap(space, squares, virtualizedMap);
                space.updateUI();
=======
                controller.setProgress(lblAction);
                controller.renderMap(squares);
>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandForward)) {
            try {
                controller.stepForward();
<<<<<<< HEAD
                controller.renderMap(space, squares, virtualizedMap);
=======
                controller.renderMap(squares);
>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandBack)) {
            try {
                controller.previousStep();
<<<<<<< HEAD
                controller.renderMap(space, squares, virtualizedMap);
=======
                controller.setProgress(lblAction);
                controller.renderMap(squares);
>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandStop)) {
            //save state and upload
            try {
                controller.saveAndUpload();
<<<<<<< HEAD
                controller.renderMap(space, squares, virtualizedMap);
=======
                controller.renderMap(squares);
>>>>>>> eb8cc0becc328b416e735f50cb58c420c1c5c3f7
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