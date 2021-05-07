import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class Agent {
    public static void agentDriver(Connection c, Scanner input) throws SQLException {
        System.out.println("Which agent are you? I will provide a listing for convenience.");
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT agent_id, aname FROM agent ORDER BY aname");
            String[][] agentList = new String[20][2]; int i = 0; // assuming a safe reasonable number of agents
            if (r.next()) {
                do {
                    agentList[i][0] = String.format("%06d", r.getInt("agent_id"));
                    agentList[i][1] = r.getString("aname");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                Utility agentUtility = new Utility();
                int agentID = agentUtility.inputRequestByID(agentList, input);
                System.out.println("--------------------------------------------------------------------------------");
                r = s.executeQuery("SELECT aname FROM agent WHERE agent_id = " + agentID);
                r.next(); // returns a boolean so we have to advance from up here
                System.out.println("Welcome, " + r.getString("aname").split(" ", 2)[0] + ".");
                boolean backout = false;
                while (true) {
                    System.out.println("What would you like to do?");
                    int choice = agentUtility.inputRequest(new String[] {"tell me a joke", "back"}, input);
                    switch (choice) {
                        case 1:
                            System.out.println("A joke? You're a joke, trying to get into our systems.");
                            break;
                        case 2:
                            backout = true;
                            break;
                    }
                    if (backout) { break; }
                }
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Well, looks like we don't have any agents after all.");
                System.out.println("We've no mechanism by which to quit, so I don't see how that could be.");
                System.out.println("Either way...scram! :)");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
