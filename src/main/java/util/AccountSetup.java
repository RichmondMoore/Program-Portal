package main.java.util;

import static  main.java.util.Methods.*;

import java.sql.*;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountSetup {

    // Allows for username/password input and if username available, adds a new row to the table
    public static void addUser(Connection c) throws SQLException {
        Scanner input = new Scanner(System.in);

        String username;
        String password;

        do {
            System.out.println("Choose a new username:");
            username = input.next();
        } while (doesUserExist(username, c) & !validateUsername(username));

        String usernameID = username.toLowerCase();

        do {
            System.out.println("Create a password:");
            password = input.next();
        } while (!validatePassword(password));

        Statement statement = c.createStatement();

        String passwordHash = hashPassword(password);

        /*
        String insert = "INSERT INTO Accounts VALUES ('" + username + "','" + passwordHash +
                "','" + usernameID + "','user'";

         */
        String insert = "INSERT INTO Accounts VALUES ('"+usernameID+"','"+passwordHash+"','"+username+"','user')";
        statement.executeUpdate(insert);

        System.out.println("Redirecting to login...\n");
    }

    // Takes username and removes row from the table
    public static void removeUser(String username, Connection c) {
        String query = "DELETE FROM Accounts WHERE username = ?";

        try {
            PreparedStatement pst = c.prepareStatement(query);
            pst.setString(1, username);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Account deleted successfully\n");
    }

    public static boolean validateUsername(String username) {
        boolean containsBannedChars = false;
        boolean underEightChars = false;

        String bannedChars = "!@#$%^&*()_-+=~`;:'\"<,>.?/{[}]|\\ ";

        for (int i = 0; i < username.length(); i++) {
            String currentChar = username.charAt(i) + "";
            if (bannedChars.contains(currentChar)) {
                containsBannedChars = true;
                System.out.println("\nUsername cannot contain symbols or spaces!");
                break;
            }
        }

        if (username.length() < 8) {
            underEightChars = true;
            System.out.println("\nUsername must be longer than 8 characters!");
        }

        return (!containsBannedChars & !underEightChars);
    }

    // Password must have a least one lowercase and uppercase character, digit, symbol,
    // and be between 8 and 32 characters to be valid
    public static boolean validatePassword(String password) {
        Pattern pat;
        Matcher match;

        boolean isValid;

        final String regex = "((?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[!@#$%^&*]).{8,32})";

        pat = Pattern.compile(regex);
        match = pat.matcher(password);

        isValid = match.matches();

        if (!isValid) {
            System.out.println("Password must have at least one uppercase letter, lowercase letter, number, symbol, and " +
                    "between 8-32 characters to be valid!");
        }

        return isValid;
    }

    public static String getOriginalUsername(Connection c, String username) throws SQLException {
        username = username.toLowerCase();

        String usernameID = "";

        String query = "SELECT * FROM Accounts WHERE username = '"+ username + "'";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = c.prepareStatement(query);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert rs != null;
        while(rs.next()) {
            usernameID = rs.getString("originalUsername");
        }

        pst.close();
        rs.close();

        return usernameID;
    }

    public static boolean isAdmin(String username, Connection c) throws SQLException {
        String query = "SELECT * FROM Accounts WHERE (username = '"+ username + "' AND userType='admin')";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = c.prepareStatement(query);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert rs != null;
        return rs.next();
    }
}
