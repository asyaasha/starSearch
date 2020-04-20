package view;

import controller.MainMenuController;
import model.Simulation;

import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;

public class MainMenuView extends JFrame implements ActionListener {
    public final String DRONE_IMAGE_PATH = String.join(File.separator, "resources", "images", "drone.png");
    final private String strFile = "File";
    final private String strBrowse = "Browse...";
    final private String strResume = "Resume Simulation";
    final private String strStart = "Start New Simulation";
    final private String strInstructions = "To start you can upload the csv scenario for a new simulation or resume a previous simulation";

    private MessageUtil msgUtil;
    private JLabel lblDisplayMessage;
    private String username;
    private MainMenuController controller;
    private JFilePicker filePicker;
    private JButton btnResume;
    private JButton btnStart;
    private Simulation prevSim;

    public MainMenuView(String user) {
        super("Start Search");
        this.username = user;

        // message helper
        msgUtil = new MessageUtil();
        lblDisplayMessage = msgUtil.getMessage();
        lblDisplayMessage.setBackground(Color.magenta);

        // File picker
        filePicker = new JFilePicker(strFile, strBrowse);
        filePicker.setBackground(new Color(
                133, 185, 230
        ));
        filePicker.setMode(JFilePicker.MODE_OPEN);
        filePicker.addFileTypeFilter(".csv", "CSV File");

        setLayout(new BorderLayout());
        Border emptyBorder = BorderFactory.createEmptyBorder(20, 90, 0 , 80);
        Border emptyBorderBottom = BorderFactory.createEmptyBorder(20, 70, 30, 70);

        // GAME OPTIONS PANEL
        JPanel options = new JPanel();
        options.setBorder(emptyBorder);
        options.setLayout(new GridLayout(7, 1));
        JLabel lbWelcome = new JLabel("WELCOME " + username + "!");
        JLabel lbInstructions = new JLabel(strInstructions);
        lbInstructions.setForeground(new Color(105,105,105));

        btnStart = new JButton(strStart);
        btnStart.addActionListener((ActionListener) this);
        btnStart.setPreferredSize(new Dimension(60, 50));
        btnStart.setBackground(new Color(158, 178, 178));

        // Icon
        ImageIcon image = new ImageIcon(DRONE_IMAGE_PATH);
        JLabel labelTitle = new JLabel("STAR SEARCH", JLabel.CENTER);
        labelTitle.setForeground(new Color(255,215,0));
        labelTitle.setFont(new Font("monospaced", Font.PLAIN, 90));
        JLabel label = new JLabel("", image, JLabel.CENTER);
        label.setBorder(emptyBorderBottom);

        btnResume = new JButton(strResume);
        btnResume.setEnabled(false);
        btnResume.addActionListener((ActionListener) this);
        btnResume.setPreferredSize(new Dimension(60, 50));
        btnResume.setBackground(new Color(158, 178, 178));

        // Controller
        controller = new MainMenuController(this);
        try {
            controller.getUserSnapshotList(username);
            btnResume.setEnabled(true);
        } catch (IllegalStateException ex) {
            System.out.println("Failed to load state from user information");
        }

        // Add widgets to layout
        options.add(lbWelcome);
        options.add(lbInstructions);
        options.add(filePicker);
        options.add(btnStart);
        options.add(btnResume);
        options.add(lblDisplayMessage);
        options.setBackground(new Color(
                133, 185, 230
        ));

        // model.Main layout
        add(options, BorderLayout.NORTH);
        add(labelTitle, BorderLayout.CENTER);
        add(label, BorderLayout.SOUTH);

        revalidate();

        // Set Frame configs
        setSize(1150, 750);
        getContentPane().setBackground(new Color(133, 185, 230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        // DEBUG
        System.out.println(command);

        if (command.equals(strResume)) {
            // resume previous sim
            try {
                controller.getStoredSim(username);
                dispose();
                new GamePlayView(prevSim, controller.getDb(), username, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (command.equals(strStart)) {
            // check that file has correct input
            String filePath = filePicker.getSelectedFilePath();
            controller.checkFileInput(filePath);

            if (lblDisplayMessage.getText().equals("Success!")) {
                dispose();
                // Play menu view 3
                new GamePlayView(controller.getNewSimulation(filePath, username), controller.getDb(), username, true);
            }
        }

        revalidate();
    }

    // Setters
    public void setMessage(String message){
        msgUtil.setMessage(message);
    }

    public void setPrevSim(Simulation sim){
        this.prevSim = sim;
    }
}

