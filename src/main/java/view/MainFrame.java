package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MainFrame extends JFrame implements ActionListener  {

    private JLabel lbEnter;
    private JTextArea txtUsername;
    private JButton btnSubmit;
    private JButton[][] squares;

    public MainFrame(){
        super("Start Search");

        setLayout(new BorderLayout());

        // LOGIN PANEL
        JPanel login = new JPanel();
        login.setLayout(new GridLayout(2, 3));
        lbEnter = new JLabel("Hello! Enter your username please.");

        txtUsername = new JTextArea();
        btnSubmit = new JButton("Submit");
        login.add(lbEnter);
        login.add(txtUsername);
        login.add(btnSubmit);


        add(login, BorderLayout.NORTH);

        // SPACE PANEL
        JPanel space = new JPanel();
        space.setLayout(new GridLayout(10, 20));
        squares = new JButton[10][10];
        for(int y = 0; y < squares.length; y++) {

            for(int x = 0; x < squares[y].length; x++) {

                squares[x][y] = new JButton("o");
                space.add(squares[x][y]);
            }
        }

        add(space, BorderLayout.CENTER);

        // GAME OPTIONS PANEL
        JPanel options = new JPanel();
        options.setLayout(new GridLayout(10, 2));
        JLabel lbFile = new JLabel("Upload scenario");
        JButton btnFile = new JButton("File");
        JButton btnPause = new JButton("Pause");
        JButton btnResume = new JButton("Resume");
        options.add(lbFile);
        options.add(btnFile);
        options.add(btnPause);
        options.add(btnResume);
        add(options, BorderLayout.WEST);

        // GAME PLAY PANEL
        JPanel play = new JPanel();
        play.setLayout(new GridLayout(2, 3));


        JButton btnBack = new JButton("BACK");
        btnBack.addActionListener((ActionListener) this);
        play.add(btnBack);

        JButton btnNextAction = new JButton("NEXT ACTION");
        btnNextAction.addActionListener((ActionListener) this);
        play.add(btnNextAction);

        JButton btnForward = new JButton("FORWARD");
                btnForward.addActionListener((ActionListener) this);
        play.add(btnForward);

        add(play, BorderLayout.SOUTH);

        revalidate();

        setSize(1150, 750);
        //frame.setContentPane(new App().loginPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        System.out.println(command);

        revalidate();
    }
}
