import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class Agent {
    public static void agentDriver(Connection c, Scanner input) throws SQLException {
        System.out.println("Which agent are you? I will provide a listing for convenience.");
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT TO_CHAR(agent_id, '000009') AS agent_id, aname FROM agent ORDER BY aname");
            String[][] agentList = new String[20][2]; int i = 0; // assuming a safe reasonable number of agents
            while (r.next()) {
                agentList[i][0] = String.valueOf(r.getInt("agent_id"));
                agentList[i][1] = r.getString("aname");
                i++;
            }
            System.out.println("--------------------------------------------------------------------------------");
            Utility agentUtility = new Utility();
            int agentID = agentUtility.inputRequestByID(agentList, input);
            System.out.println("--------------------------------------------------------------------------------");
            r = s.executeQuery("SELECT aname FROM agent WHERE agent_id = " + agentID);
            r.next(); // returns a boolean so we have to advance from up here
            System.out.println("Welcome, " + r.getString("aname").split(" ", 2)[0] + ".");
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
