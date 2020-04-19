package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JOptionPane;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

import controller.GamePlayController;
import model.Content;
import model.Database;
import model.Simulation;
import static model.Simulation.END_STATUS;
import static model.Simulation.START_STATUS;
import model.SpaceRegion;

public class GamePlayView extends JFrame implements ActionListener  {
    private final JLabel simulationStatusLabel;

    private final String commandNext = "NEXT";
    private final String commandBack = "BACK";
    private final String commandStop = "STOP";
    private final String commandForward = "FAST FORWARD";

    private JButton btnBack;
    private final JButton btnStop;
    private final JButton btnForward;
    private final JPanel pnlProgress;
    private final JPanel pnlGameControls;
    private final JPanel space;
    private final JButton[][] squares;

    private final GamePlayController controller;
    private Simulation sim;
    private final SpaceRegion virtualizedMap;

    private final JLabel lblAction;

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
    public HashMap<String,ImageIcon> droneIconsMap = new HashMap<>();

    public GamePlayView(Simulation sim, Database db, String user) {
        super("Start Search");
        controller = new GamePlayController(this, db, user, sim);
        this.sim = sim;
        this.virtualizedMap = sim.getVirtualizedMap();

        simulationStatusLabel = new JLabel("");

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
                    squares[y][x].setText(String.valueOf(virtualizedMap.getSpaceLayout()[y][x].getOccupantDrone().getDroneID()));
                } else if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.EMPTY) {
                    squares[y][x] = new JButton("");
                } else if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.STARS) {
                    squares[y][x] = new JButton("+");
                } else if (virtualizedMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.SUN) {
                    JButton button = new JButton();
                    button.setIcon(imgSun);
                    button.setText("");
                    squares[y][x] = button;
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
        pnlProgress.add(simulationStatusLabel);
        controller.setProgress(lblAction);

        add(pnlProgress, BorderLayout.WEST);

        // GAME PLAY CONTROL PANEL
        pnlGameControls = new JPanel();
        pnlGameControls.setBackground(new Color(158, 178, 178));
        pnlGameControls.setBorder(emptyBorder);
        pnlGameControls.setLayout(new GridLayout(1, 3));

        btnBack = new JButton(commandStop);
        btnBack.addActionListener(this);
        pnlGameControls.add(btnBack);

        btnBack = new JButton(commandBack);
        btnBack.addActionListener(this);
        pnlGameControls.add(btnBack);

        btnStop = new JButton(commandNext);
        btnStop.addActionListener(this);
        pnlGameControls.add(btnStop);

        btnForward = new JButton(commandForward);
        btnForward.addActionListener(this);
        pnlGameControls.add(btnForward);

        add(pnlGameControls, BorderLayout.SOUTH);

        revalidate();

        setSize(1150, 750);
        getContentPane().setBackground(new Color(133, 185, 230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void statsDisplay(Integer exploredTiles, Integer aliveDrones, Integer discoveredSuns){
        String message = "Explored Squares: " + exploredTiles.toString() + "\n" + "Final Drones: " + aliveDrones.toString() + "\n" + "Discovered Suns: " + discoveredSuns;
        JOptionPane.showMessageDialog(null, message, "Simulation Results", JOptionPane.INFORMATION_MESSAGE);
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

    public void setStatusMessage(String message){
        if (message.equals(END_STATUS)) {
            simulationStatusLabel.setText("End of Simulation");
        } else if (message.equals(START_STATUS)) {
            simulationStatusLabel.setText("Start of Simulation");
        } else {
            simulationStatusLabel.setText("");
        }
    }
}