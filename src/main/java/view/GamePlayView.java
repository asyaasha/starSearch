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
import java.util.HashMap;

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
    private SpaceRegion virtualizedMap;
    private JButton[][] squares;

    private JLabel lblAction;
    private JLabel lblDetail;
    private JLabel lblDrone;
    private JLabel lbStatus;

    public ImageIcon imgSun = new ImageIcon("bh.png");
    public ImageIcon imgDroneN = new ImageIcon("drone_N.png");
    public ImageIcon imgDroneE = new ImageIcon("drone_E.png");
    public ImageIcon imgDroneW = new ImageIcon("drone_W.png");
    public ImageIcon imgDroneNE = new ImageIcon("drone_NE.png");
    public ImageIcon imgDroneNW = new ImageIcon("drone_NW.png");
    public ImageIcon imgDroneSE = new ImageIcon("drone_SE.png");
    public ImageIcon imgDroneSW = new ImageIcon("drone_SW.png");
    public ImageIcon imgDroneS = new ImageIcon("drone_S.png");

    public ImageIcon imgStar = new ImageIcon("star.png");
    public HashMap<String,ImageIcon> droneIconsMap = new HashMap<String,ImageIcon>();

    public GamePlayView(Simulation sim, Database db, String user) throws IOException {
        super("Start Search");
        controller = new GamePlayController(this, db, user, sim);
        this.sim = sim;
        this.virtualizedMap = sim.getVirtualizedMap();

        resizeIcons();
        droneIconsMap.put("NORTH", imgDroneN);
        droneIconsMap.put("NORTHEAST", imgDroneNE);
        droneIconsMap.put("NORTHWEST", imgDroneNW);
        droneIconsMap.put("SOUTH", imgDroneS);
        droneIconsMap.put("SOUTHEAST", imgDroneSE);
        droneIconsMap.put("SOUTHWEST", imgDroneSW);
        droneIconsMap.put("WEST", imgDroneW);
        droneIconsMap.put("EAST", imgDroneE);

        getContentPane().setVisible(false);
        getContentPane().setVisible(true);
        Border emptyBorder = BorderFactory.createEmptyBorder(40, 70, 20 , 70);

        // Setting the main layout type
        setLayout(new BorderLayout());

        ImageIcon imgDrone = new ImageIcon("drone.png");
        Image newImgD = imgDrone.getImage().getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        imgDrone = new ImageIcon(newImgD);

        space = new JPanel();
        space.setLayout(new GridLayout(virtualizedMap.getLength(), virtualizedMap.getWidth()));
        space.setBackground(new Color(133, 185, 230));
        space.setBorder(emptyBorder);
        space.setLayout(new GridLayout(virtualizedMap.getLength(), virtualizedMap.getWidth()));
        squares = new JButton[virtualizedMap.getLength() + 1][virtualizedMap.getWidth() + 1];

        for(int y = 1; y < squares.length; y++) {
            for(int x = 1; x < squares[1].length; x++) {
                if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.DRONE) {
                    JButton button = new JButton();
                    button.setIcon(imgDrone);
                    squares[y][x] = button;
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

    public void resizeIcons() {
        imgSun = new ImageIcon(imgProcesser(imgSun));
        imgDroneN = new ImageIcon(imgProcesser(imgDroneN));
        imgDroneE  = new ImageIcon(imgProcesser(imgDroneE));
        imgDroneW   = new ImageIcon(imgProcesser(imgDroneW));
        imgDroneS   = new ImageIcon(imgProcesser(imgDroneS));
        imgDroneSE  = new ImageIcon( imgProcesser(imgDroneSE));
        imgDroneSW   = new ImageIcon(imgProcesser(imgDroneSW));
        imgDroneNE   = new ImageIcon(imgProcesser(imgDroneNE));
        imgDroneNW   = new ImageIcon(imgProcesser(imgDroneNW));
        imgStar   = new ImageIcon( imgProcesser(imgStar));
    }

    public Image imgProcesser(ImageIcon currImage){
        return currImage.getImage().getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH);
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
                controller.renderMap(squares);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandForward)) {
            try {
                controller.stepForward();
                controller.renderMap(squares);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandBack)) {
            try {
                controller.previousStep();
                controller.setProgress(lblAction);
                controller.renderMap(squares);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandStop)) {
            //save state and upload
            try {
                controller.saveAndUpload();
                controller.renderMap(squares);
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