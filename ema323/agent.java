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
                    int choice = agentUtility.inputRequest(new String[] {"add a customer", "back"}, input);
                    switch (choice) {
                        case 1:
                            addNewCustomer(c, input, agentID);
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

    private static void addNewCustomer(Connection c, Scanner input, int agentID) throws SQLException {
        try (Statement s = c.createStatement(); Statement t = c.createStatement();
            PreparedStatement p = c.prepareStatement("INSERT INTO customer VALUES (?, ?, ?, ?, ?, ?, ?)");) {
            Utility agentUtility = new Utility();
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Enter the customer's first name:");
            String fname = agentUtility.inputRequestString(input, "^\\D{1,50}$");
            if (fname.equals("__BACK__")) { return; }
            System.out.println("Enter the customer's middle initial (a single '-' for none):");
            String minitial = agentUtility.inputRequestString(input, "^\\D|-$");
            if (minitial.equals("__BACK__")) { return; }
            if (minitial.equals("-")) { minitial = ""; }
            System.out.println("Enter the customer's last name:");
            String lname = agentUtility.inputRequestString(input, "^\\D{1,50}$");
            if (lname.equals("__BACK__")) { return; }
            System.out.println("Enter the customer's suffix/title (a single '-' for none):");
            String suffix = agentUtility.inputRequestString(input, "^\\D{1,10}$");
            if (suffix.equals("__BACK__")) { return; }
            if (suffix.equals("-")) { minitial = ""; }
            System.out.println("Enter the customer's date of birth (YYYY-MM-DD):");
            String birthDate = agentUtility.inputRequestString(input, "^\\d{4}-\\d{2}-\\d{2}$");
            if (birthDate.equals("__BACK__")) { return; }
            p.setInt(1, new Random().nextInt(1000000)); p.setInt(7, agentID);
            p.setDate(6, new java.sql.Date(0).valueOf(birthDate));
            p.setString(2, fname); p.setString(3, minitial); p.setString(4, lname); p.setString(5, suffix);

            try {
                p.executeQuery();
                c.commit();
                System.out.println("Success! Your new happy customer has been signed up.");
            }
            catch (SQLException e) {
                c.rollback();
                System.out.println("Something seems to have gone wrong. Please try again soon.");
            }
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
