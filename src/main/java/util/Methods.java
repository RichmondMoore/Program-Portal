package main.java.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Methods {

    // Returns string that contains current time, used primarily for debugging
    public static String currentTime() {
        int hourInt = LocalDateTime.now().getHour();
        int minuteInt = LocalDateTime.now().getMinute();
        int secondInt = LocalDateTime.now().getSecond();

        String hour, minute, second;

        if (hourInt < 0) {
            hour = "0" + hourInt;
        } else {
            hour = Integer.toString(hourInt);
        }

        if (minuteInt < 0) {
            minute = "0" + minuteInt;
        } else {
            minute = Integer.toString(minuteInt);
        }

        if (secondInt < 0) {
            second = "0" + secondInt;
        } else {
            second = Integer.toString(secondInt);
        }

        return "[" + hour + ":" + minute + ":" + second + "]";
    }

    // Converts password into into SHA256, returns the new hashed spassword
    public static String hashPassword(String password) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] digest;

        // Hashes password for security
        md.update(password.getBytes(StandardCharsets.UTF_8));
        digest = md.digest();

        return String.format("%064x", new BigInteger(1, digest));
    }

    // Takes username and queries to see if it exists in DB
    public static boolean doesUserExist(String username, Connection c) throws SQLException {
        username = username.toLowerCase();

        String query = "SELECT * FROM accounts WHERE username = '"+ username + "'";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = c.prepareStatement(query);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert rs != null;
        boolean exists = rs.next();

        pst.close();
        rs.close();

        return exists;
    }

}
