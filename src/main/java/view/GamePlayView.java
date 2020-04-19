package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
    private final String progressTitle = "--- CURRENT PROGRESS --- ";
    private final JButton btnNext;
    private final JButton btnBack;
    private final JButton btnStop;
    private final JButton btnForward;
    private final JPanel pnlProgress;
    private final JPanel pnlGameControls;
    private final JPanel space;
    private final JButton[][] squares;

    private final GamePlayController controller;
    private final SpaceRegion baseMap;
    private Simulation sim;

    private final JLabel lblProgressState;

    public ImageIcon imgSun = new ImageIcon(getImageFilePath("sun.png"));
    public ImageIcon imgDroneN = new ImageIcon(getImageFilePath("drone_N.png"));
    public ImageIcon imgDroneE = new ImageIcon(getImageFilePath("drone_E.png"));
    public ImageIcon imgDroneW = new ImageIcon(getImageFilePath("drone_W.png"));
    public ImageIcon imgDroneNE = new ImageIcon(getImageFilePath("drone_NE.png"));
    public ImageIcon imgDroneNW = new ImageIcon(getImageFilePath("drone_NW.png"));
    public ImageIcon imgDroneSE = new ImageIcon(getImageFilePath("drone_SE.png"));
    public ImageIcon imgDroneSW = new ImageIcon(getImageFilePath("drone_SW.png"));
    public ImageIcon imgDroneS = new ImageIcon(getImageFilePath("drone_S.png"));
    public ImageIcon imgStar = new ImageIcon(getImageFilePath("star.png"));

    public HashMap<String,ImageIcon> droneIconsMap = new HashMap<>();

    private int count;

    public GamePlayView(Simulation sim, Database db, String user) {
        super("Start Search");
        count = 0;
        controller = new GamePlayController(this, db, user, sim);
        this.baseMap = sim.getBaseMap();

        this.sim = sim;

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
        Border emptyBorder = BorderFactory.createEmptyBorder(40, 20, 20 , 20);

        // Setting the main layout type
        setLayout(new BorderLayout());

        space = new JPanel();
        space.setLayout(new GridLayout(baseMap.getLength(), baseMap.getWidth()));
        space.setBackground(new Color(133, 185, 230));
        space.setBorder(emptyBorder);
        space.setLayout(new GridLayout(baseMap.getLength(), baseMap.getWidth()));
        squares = new JButton[baseMap.getLength() + 1][baseMap.getWidth() + 1];

        for(int y = 1; y < squares.length; y++) {
            for(int x = 1; x < squares[1].length; x++) {
                if (baseMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.DRONE) {
                    String orientation = String.valueOf(baseMap.getSpaceLayout()[y][x].getOccupantDrone().getDroneOrientation());
                    JButton button = new JButton();
                    button.setIcon(droneIconsMap.get(orientation));
                    squares[y][x] = button;
                    squares[y][x].setText(String.valueOf(baseMap.getSpaceLayout()[y][x].getOccupantDrone().getDroneID()));
                } else if (baseMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.EMPTY) {
                    if (sim.getVirtualizedMap().getSpaceLayout()[y][x].getExplorationStatus() == true) {
                        squares[y][x] = new JButton("");
                    } else {
                        JButton button = new JButton();
                        button.setIcon(imgStar);
                        squares[y][x] = button;
                    }
                } else if (sim.getVirtualizedMap().getSpaceLayout()[y][x].getStarFieldContents() == Content.STARS) {
                    JButton button = new JButton();
                    button.setIcon(imgStar);
                    squares[y][x] = button;
                } else if (baseMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.SUN) {
                    JButton button = new JButton();
                    button.setIcon(imgSun);
                    squares[y][x] = button;
                } else if (baseMap.getSpaceLayout()[y][x].getStarFieldContents() == Content.UNKNOWN) {
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

        JLabel lblTitle = new JLabel(progressTitle);

        lblProgressState = new JLabel("");
        pnlProgress.add(lblTitle);
        pnlProgress.add(lblProgressState);
        pnlProgress.add(simulationStatusLabel);
        controller.setProgress(lblProgressState);

        add(pnlProgress, BorderLayout.WEST);

        // GAME PLAY CONTROL PANEL
        pnlGameControls = new JPanel();
        pnlGameControls.setBackground(new Color(158, 178, 178));
        pnlGameControls.setBorder(emptyBorder);
        pnlGameControls.setLayout(new GridLayout(1, 3));

        btnStop = new JButton(commandStop);
        btnStop.setPreferredSize(new Dimension(60, 50));
        btnStop.addActionListener(this);
        pnlGameControls.add(btnStop);

        btnBack = new JButton(commandBack);
        btnBack.setEnabled(false);
        btnBack.setPreferredSize(new Dimension(60, 50));
        btnBack.addActionListener(this);
        pnlGameControls.add(btnBack);
        btnNext = new JButton(commandNext);
        btnNext.setPreferredSize(new Dimension(60, 50));
        btnNext.addActionListener(this);
        pnlGameControls.add(btnNext);

        btnForward = new JButton(commandForward);
        btnForward.setPreferredSize(new Dimension(60, 50));
        btnForward.addActionListener(this);
        pnlGameControls.add(btnForward);

        add(pnlGameControls, BorderLayout.SOUTH);

        revalidate();

        setSize(1150, 750);
        getContentPane().setBackground(new Color(133, 185, 230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void statsDisplay(String summaryReport){
        JOptionPane.showMessageDialog(null, summaryReport, "Summary Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public void setSimulation(Simulation sim){
        this.sim = sim;
    }

    public void resizeIcons() {
        imgSun = new ImageIcon(imgProcesser(imgSun));
        imgDroneN = new ImageIcon(imgProcesser(imgDroneN));
        imgDroneE = new ImageIcon(imgProcesser(imgDroneE));
        imgDroneW = new ImageIcon(imgProcesser(imgDroneW));
        imgDroneS = new ImageIcon(imgProcesser(imgDroneS));
        imgDroneSE = new ImageIcon( imgProcesser(imgDroneSE));
        imgDroneSW = new ImageIcon(imgProcesser(imgDroneSW));
        imgDroneNE = new ImageIcon(imgProcesser(imgDroneNE));
        imgDroneNW = new ImageIcon(imgProcesser(imgDroneNW));
        imgStar = new ImageIcon( imgProcesser(imgStar));
    }

    public Image imgProcesser(ImageIcon currImage){
        return currImage.getImage().getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        // Perform next step
        if (command.equals(commandNext)) {
            btnBack.setEnabled(true);

            try {
                count ++;
                controller.nextStep();
                controller.setProgress(lblProgressState);
                controller.renderMap(squares);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command.equals(commandForward)) {
            btnBack.setEnabled(false);
            btnNext.setEnabled(false);

            if (!btnBack.isEnabled() && !btnNext.isEnabled()) {
                try {
                    controller.stepForward(squares, btnForward);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            btnBack.setEnabled(true);
        }
        if (command.equals(commandBack)) {
            try {
                count --;
                if (count == 0) {
                    btnBack.setEnabled(false);
                } else {
                    controller.previousStep();
                    controller.setProgress(lblProgressState);
                    controller.renderMap(squares);
                }
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

    public String getImageFilePath(String imageName) {
        return String.join(File.separator, "resources", "images", imageName);
    }
}