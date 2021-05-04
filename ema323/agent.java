import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class Agent {
    public static void agentDriver(Connection c, Scanner input) throws SQLException {
        System.out.println("Which agent are you? I will provide a listing for convenience.");
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT TO_CHAR(agent_id, '000009') AS agent_id, aname FROM agent ORDER BY aname");
            String[][] agentList = new String[20][2]; int i = 0;
            while (r.next()) {
                agentList[i][0] = String.valueOf(r.getInt("agent_id"));
                agentList[i][1] = r.getString("aname");
                i++;
            }
            int agentID = inputRequestByID(agentList, input);
            r = s.executeQuery("SELECT aname FROM agent WHERE agent_id = " + agentID);
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
