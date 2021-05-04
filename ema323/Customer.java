import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class Customer {
    public static void customerDriver(Connection c, Scanner input) throws SQLException {
        System.out.println("Which customer are you? I will provide a listing for convenience,");
        System.out.println("though it somewhat diminishes the disciplinary integrity of our system.");
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT cust_id, fname, lname FROM customer ORDER BY lname");
            String[][] custList = new String[100][2]; int i = 0; // assuming a safe reasonable number of customers
            while (r.next()) {
                custList[i][0] = String.format("%06d", r.getInt("cust_id"));
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
            customerInfo(c, custID);
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void customerInfo(Connection c, int custID) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r; // for symmetry/parallelism
            r = s.executeQuery("SELECT fname, lname, minitial, suffix, birth_date, agent_id FROM customer WHERE cust_id = " + custID);
            r.next(); // we already have custID
            String custName = r.getString("lname") + ", " + r.getString("fname");
            String minitial = r.getString("minitial"); if (minitial != null) { custName += " " + minitial + "."; }
            String suffix = r.getString("suffix"); if (suffix != null) { custName += ", " + suffix; }
            //String birthDate = r.getString("birth_date");
            r = s.executeQuery("SELECT agent_id, aname FROM agent WHERE agent.agent_id = " + r.getInt("agent_id"));
            r.next(); String agentID = r.getString("agent_id"); String agentName = r.getString("aname");
            r = s.executeQuery("SELECT COUNT(*) FROM polisy WHERE cust_id = " + custID);
            int numPolicies = 0; if (r.next()) { numPolicies = r.getInt(1); }
            r = s.executeQuery("SELECT COUNT(*) FROM dependentt WHERE cust_id = " + custID);
            int numDependents = 0; if (r.next()) { numDependents = r.getInt(1); }
            System.out.printf("(%06d) %s | %d policies | %d dependents\n", custID, custName, numPolicies, numDependents);
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
