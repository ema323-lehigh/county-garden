import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class adjuster {
    public static void adjusterDriver(Connection c, Scanner input) throws SQLException {
        System.out.println("Which adjuster are you? I will provide a listing for convenience.");
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT TO_CHAR(adj_id, '000009') AS adj_id, aname FROM adjuster ORDER BY aname");
            String[][] adjList = new String[20][2]; int i = 0;
            while (r.next()) {
                adjList[i][0] = String.valueOf(r.getInt("adj_id"));
                adjList[i][1] = r.getString("aname");
                i++;
            }
            int adjID = inputRequestByID(adjList, input);
            r = s.executeQuery("SELECT aname FROM adjuster WHERE adj_id = " + adjID);
            r.next(); // returns a boolean so we have to advance from up here
            System.out.println("Welcome, " + r.getString("aname").split(" ", 2)[0] + ".");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    public static int inputRequestByID(String[][] choices, Scanner input) {
        int choice;
        for (int i = 0; i < choices.length; i++) {
            if (choices[i][0] != null) { System.out.println((i + 1) + ") " + choices[i][1] + " (" + choices[i][0] + ")"); }
        }
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if ((choice > 0) && (choice <= choices.length)) {
                    return Integer.parseInt(choices[choice - 1][0]);
                }
                else {
                    System.out.println("Please enter an integer corresponding to one of the choices above.");
                }
            }
            else {
                System.out.println("Please enter an integer corresponding to one of the choices above.");
                input.nextLine();
            }
        }
    }
}
