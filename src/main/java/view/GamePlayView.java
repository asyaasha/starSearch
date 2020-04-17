package view;
import model.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GamePlayView extends JFrame implements ActionListener  {

    private JButton[][] squares;
    private JButton btnBack;
    private JButton btnStop;
    private JButton btnForward;
    private JPanel pnlProgress;
    private JPanel pnlGameControlls;
    private String filePath;
    private Scenario scenario;

    // Currently passing file path and scenario from the previous screen, might need to be updated
    public GamePlayView(String filePath, Scenario scenario){
        super("Start Search");

        this.filePath = filePath;
        // check the scenario class it has the configurations from the file input scenario stored
        this.scenario = scenario;

        // Setting the main layout type
        setLayout(new BorderLayout());

        // SPACE REGION PANEL  --> check the logic from simulation visualize function
        JPanel space = new JPanel();
        space.setLayout(new GridLayout(scenario.getHeight(), scenario.getWidth()));
        squares = new JButton[scenario.getWidth()][scenario.getHeight()];
        for(int y = 0; y < squares.length; y++) {
            for(int x = 0; x < squares[y].length; x++) {
                squares[y][x] = new JButton("o");
                space.add(squares[y][x]);
            }
        }

        add(space, BorderLayout.EAST);


        // TABLE PANEL TO DISPLAY PROGRESS
        pnlProgress = new JPanel();
        pnlProgress.setLayout(new GridLayout(10, 2));
        JLabel lblTable = new JLabel("Current progress..");
        pnlProgress.add(lblTable);
        add(pnlProgress, BorderLayout.WEST);

        // GAME PLAY CONTROL PANEL
        pnlGameControlls = new JPanel();
        pnlGameControlls.setLayout(new GridLayout(1, 3));

        btnBack = new JButton("STOP");
        btnBack.addActionListener((ActionListener) this);
        pnlGameControlls.add(btnBack);

        btnBack = new JButton("BACK");
        btnBack.addActionListener((ActionListener) this);
        pnlGameControlls.add(btnBack);

        btnStop = new JButton("NEXT");
        btnStop.addActionListener((ActionListener) this);
        pnlGameControlls.add(btnStop);

        btnForward = new JButton("FORWARD");
        btnForward.addActionListener((ActionListener) this);
        pnlGameControlls.add(btnForward);

        add(pnlGameControlls, BorderLayout.SOUTH);

        revalidate();

        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        System.out.println(command);

        // Just for testing assigned running main to the NEXT button feel free to move it anywhere needed
        if (command.equals("NEXT")) {
            Main test = new Main();
            String[] testPath = {filePath};
            try {
                System.out.println("testPath");
                System.out.println(testPath);
                test.main(testPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        revalidate();
    }
}