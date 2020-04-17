package view;

import javax.swing.*;
import java.awt.*;

public class MessageUtil {
    private JLabel lblDisplayMessage;

    public MessageUtil() {
        lblDisplayMessage = new JLabel("");
        lblDisplayMessage.setForeground(Color.magenta);
        Font newLabelFont=new Font(lblDisplayMessage.getFont().getName(),Font.ITALIC,lblDisplayMessage.getFont().getSize());

        //Set JLabel font using new created font
        lblDisplayMessage.setFont(newLabelFont);
    }

    // Getter
    public JLabel getMessage() {
        return this.lblDisplayMessage;
    }
    public void setMessage(String message) {
        lblDisplayMessage.setText(message);
    }
}
