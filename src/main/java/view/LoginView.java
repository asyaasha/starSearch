package view;

import controller.App;
import controller.LoginController;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginView extends JFrame implements ActionListener {

    private JLabel lbEnter;
    private JTextArea txtUsername;
    private JButton btnSubmit;
    private MessageUtil msgUtil;
    private JLabel lblDisplayMessage;
    private LoginController controller;
    private String submitCommand = "SUBMIT";
    private String successMessage = "SUCCESS!";

    public LoginView() {
        super("Start Search");

        controller = new LoginController(this);
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
        btnSubmit = new JButton(submitCommand);
        btnSubmit.setPreferredSize(new Dimension(60, 50));
        btnSubmit.addActionListener((ActionListener) this);

        login.add(lbEnter);
        login.add(txtUsername);
        login.add(btnSubmit);


        add(login);
        add(lblDisplayMessage);

        //revalidate();

        // Set the frame configs
        setSize(1150, 750);
        getContentPane().setBackground(new Color(133, 185, 230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        String username = txtUsername.getText();

        if(command.equals(submitCommand))
        {
            controller.checkUsername(username);

            if (lblDisplayMessage.getText().equals(successMessage)) {
                dispose();
                new MainMenuView(username);
            }
        }
    }

    public void setMessage(String message){
        msgUtil.setMessage(message);
    }
}
