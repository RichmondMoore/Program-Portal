package main.java.application;

import main.java.util.AccountSetup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

import static main.java.util.AccountSetup.isAdmin;
import static main.java.util.Methods.*;
import static main.java.util.Admin.generateUsers;
import static main.java.util.Admin.removeGeneratedUsers;

public class Login {

    // System that allows users to enter username/password to log in, all info stored in SQLite DB
    public static void login() throws IOException {

        Scanner input = new Scanner(System.in);

        // Establish connection to DB
        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\rjwmo\\IdeaProjects\\Account-Management-System\\src\\main\\java\\accountsdb.db");
        } catch(Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        boolean isValid = false; // Is the username/password combo is valid?
        boolean accountExists = true; // Self-explanatory

        String password;
        String username;

        // Input login info
        System.out.println("Username:");
        username = input.next();

        System.out.println("Password:");
        password = input.next();


        // Check to see if account with username exists
        try {
            accountExists = doesUserExist(username, c);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If user enters an account that doesn't exist, allows them to make one
        if (!accountExists) {
            System.out.println("No account with that username exists! Make an account?");
            String ans = input.next();

            if (ans.equalsIgnoreCase("y")) {
                try {
                    AccountSetup.addUser(c);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //Redirects user to log in after making an account
                login();
            } else {
                System.exit(0);
            }
        }

        // If the account does exist, make sure they username/password combo is correct
        if (accountExists) {
            isValid = validateCredentials(username, password, c);
        } else {
            System.out.println("Invalid username or password! Please try again.");
            login();
        }

        // Basically just to print the username the username someone submitted when creating the account
        // This is due to the way usernames are stored in the database (all lowercase)
        String originalUsername = "";
        try {
            originalUsername = AccountSetup.getOriginalUsername(c, username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Checks if username/password combo is valid, if it is, validate username/password combo
        if(isValid) {
            System.out.println("Welcome " + originalUsername + "!");
        } else if(accountExists) {
            login();
        }

        System.out.println("Would you like to remove your account?");
        String ans = input.next();

        if (ans.equalsIgnoreCase("y")) {
            AccountSetup.removeUser(username, c);
        }

        try {
            if (isAdmin(username, c)) {
                System.out.println("1. Add users\n2. Remove users");
                ans = input.next();
                
                if (ans.equals("1")) {
                    generateUsers(10000, c);
                } else if (ans.equals("2")) {
                    removeGeneratedUsers(c);   
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Takes username and password to determine if the login is valid
    // Hashes password and compares it to the stored hash in the DB
    private static boolean validateCredentials(String username, String passwordInput, Connection c) {
        username = username.toLowerCase();

        String query = "SELECT * FROM Accounts WHERE username=\'" + username + "\'";
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            pst = c.prepareStatement(query);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String password = hashPassword(passwordInput);

        String validPassword = null;
        try {
            assert rs != null;
            validPassword = rs.getString("password_hash");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assert validPassword != null;
        if(validPassword.equalsIgnoreCase(password)) {
            return true;
        } else {
            return false;
        }
    }
}