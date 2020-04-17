package controller;

import view.GamePlayView;
import view.LoginView;
import view.MainFrame;
import view.MainMenuView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// Runner
public class App {
    public static void main(String[] args) {
        try
        {
            setUIFont(new javax.swing.plaf.FontUIResource("monospaced", Font.BOLD,18));
        }
        catch(Exception e){}

        new LoginView();
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
