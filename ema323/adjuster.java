import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class Adjuster {
    public static void adjusterDriver(Connection c, Scanner input) throws SQLException {
        System.out.println("Which adjuster are you? I will provide a listing for convenience.");
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT adj_id, aname FROM adjuster ORDER BY aname");
            String[][] adjList = new String[20][2]; int i = 0; // assuming a safe reasonable number of adjusters
            while (r.next()) {
                adjList[i][0] = String.format("%06d", r.getInt("adj_id"));
                adjList[i][1] = r.getString("aname");
                i++;
            }
            System.out.println("--------------------------------------------------------------------------------");
            Utility adjUtility = new Utility();
            int adjID = adjUtility.inputRequestByID(adjList, input);
            System.out.println("--------------------------------------------------------------------------------");
            r = s.executeQuery("SELECT aname FROM adjuster WHERE adj_id = " + adjID);
            r.next(); // returns a boolean so we have to advance from up here
            System.out.println("Welcome, " + r.getString("aname").split(" ", 2)[0] + ".");
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
