package controller;

import view.LoginView;

import javax.swing.*;
import java.awt.*;

// Runner
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try
                {
                    setUIFont(new javax.swing.plaf.FontUIResource("monospaced", Font.BOLD,18));
                }
                catch(Exception e){}

                new LoginView();
            }
        });
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f)
    {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while(keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if(value instanceof javax.swing.plaf.FontUIResource) UIManager.put(key, f);
        }
    }
}
