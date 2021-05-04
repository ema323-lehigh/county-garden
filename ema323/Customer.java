import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class Customer {
    public static void customerDriver(Connection c, Scanner input) throws SQLException {
        System.out.println("Which customer are you? I will provide a listing for convenience,");
        System.out.println("though it somewhat diminishes the disciplinary integrity of our system.");
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT TO_CHAR(cust_id, '000009') AS cust_id, fname, lname FROM customer ORDER BY lname");
            String[][] custList = new String[100][2]; int i = 0; // assuming a safe reasonable number of customers
            while (r.next()) {
                custList[i][0] = String.valueOf(r.getInt("cust_id"));
                custList[i][1] = r.getString("fname") + " " + r.getString("lname");
                i++;
            }
            System.out.println("--------------------------------------------------------------------------------");
            Utility custUtility = new Utility();
            int custID = custUtility.inputRequestByMutedID(custList, input);
            System.out.println("--------------------------------------------------------------------------------");
            r = s.executeQuery("SELECT fname FROM customer WHERE cust_id = " + custID);
            r.next(); // returns a boolean so we have to advance from up here
            System.out.println("Welcome, " + r.getString("fname") + ". You are a distinguished member of our little");
            System.out.println("insurance family, and we're glad to have you working with us today.");
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
