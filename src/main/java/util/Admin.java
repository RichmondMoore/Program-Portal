package main.java.util;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;


public class Admin {

    public static void generateUsers(int num, Connection c) throws SQLException {
        Statement statement = c.createStatement();
        for(int i = 0; i < num; i++) {
            long timeInt = (int) (new Date().getTime()/1000);

            String username = "user_" + Long.toString(timeInt + i);
            String password = "pass_" + Long.toString(timeInt + i);

            String insert = "INSERT INTO Accounts VALUES ('" + username + "','" + password +
                    "','" + username + "','bot')";
            statement.executeUpdate(insert);
        }
    }

    public static void removeGeneratedUsers(Connection c) throws SQLException {
        String query = "DELETE FROM Accounts WHERE userType = 'bot'";

        try {
            PreparedStatement pst = c.prepareStatement(query);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Bots deleted successfully\n");
    }
}