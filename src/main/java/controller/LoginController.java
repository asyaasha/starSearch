package controller;

import model.Database;
import view.LoginView;

public class LoginController {

    private LoginView view;
    private Database db;

    public LoginController(LoginView view){
        this.view = view;
        this.db = new Database();
    }

    public void checkUsername(String username){
        if (username.length() > 0){
            view.setMessage("SUCCESS!");
        }
        else{
            view.setMessage("Enter the username!");
        }
    }
}