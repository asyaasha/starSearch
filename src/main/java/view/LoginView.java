package view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame implements ActionListener {

    private JLabel lbEnter;
    private JTextArea txtUsername;
    private JButton btnSubmit;
    private MessageUtil msgUtil;
    private JLabel lblDisplayMessage;

    public LoginView() {
        super("Start Search");

        setLayout(new FlowLayout());

        // message helper
        msgUtil = new MessageUtil();
        lblDisplayMessage = msgUtil.getMessage();
        //lblDisplayMessage.setForeground(Color.magenta);

        // LOGIN PANEL
        JPanel login = new JPanel();
        Border emptyBorder = BorderFactory.createEmptyBorder(120, 90, 0 , 80);
        login.setBorder(emptyBorder);
        login.setBackground(new Color(133, 185, 230));
        login.setLayout(new GridLayout(1, 3));
        lbEnter = new JLabel("ENTER YOUR NAME, PLEASE    ");
        lbEnter.setBackground(new Color(133, 185, 230));
        lbEnter.setOpaque(true);
        txtUsername = new JTextArea();
        btnSubmit = new JButton("SUBMIT");
        btnSubmit.setPreferredSize(new Dimension(60, 50));
        btnSubmit.addActionListener((ActionListener) this);

        login.add(lbEnter);
        login.add(txtUsername);
        login.add(btnSubmit);


        add(login);
        add(lblDisplayMessage);

        //revalidate();

        setSize(1150, 750);
        getContentPane().setBackground(new Color(133, 185, 230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        System.out.println(command);
        String username = txtUsername.getText();
        System.out.println(username);
        // Check username against the database?.. pass it to the menu
        if(command.equals("SUBMIT"))
        {
            if (username.length() <1) {
                msgUtil.setMessage("Enter the username please");
            } else {
                dispose();
                // Play menu view 2
                new MainMenuView(username);
            }
        }
    }
}
