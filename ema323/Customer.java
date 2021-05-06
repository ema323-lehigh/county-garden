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
            if (r.next()) {
                do {
                    custList[i][0] = String.format("%06d", r.getInt("cust_id"));
                    custList[i][1] = r.getString("fname") + " " + r.getString("lname");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                Utility custUtility = new Utility();
                int custID = custUtility.inputRequestByMutedID(custList, input);
                System.out.println("--------------------------------------------------------------------------------");
                r = s.executeQuery("SELECT fname FROM customer WHERE cust_id = " + custID);
                r.next(); // returns a boolean so we have to advance from up here
                System.out.println("Welcome, " + r.getString("fname") + ". You are a distinguished member of our little");
                System.out.println("insurance family, and we're glad to have you working with us today.");
                System.out.println("--------------------------------------------------------------------------------");
                customerInfo(c, input, custID);
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Never mind, we don't have any customers. An agent will have to sign you up.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void customerInfo(Connection c, Scanner input, int custID) throws SQLException {
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
            r = s.executeQuery("SELECT * FROM cust_add WHERE cust_id = " + custID);
            if (r.next()) {
                System.out.println(r.getString("street") + ", " + r.getString("city") + ", " + r.getString("astate") + ", " + String.format("%05d", r.getInt("zipcode")));
            }
            else {
                System.out.println("Huh, we don't have an address for you. We should fix that.");
                addCustAddress(c, input, custID);

            }
            r = s.executeQuery("SELECT * FROM phone_num WHERE cust_id = " + custID);
            if (r.next()) {
                do {
                    System.out.println(r.getString("kind") + " phone: (" + r.getString("numb").substring(0, 3) +
                    ")-" +r.getString("numb").substring(3, 6) + "-" + r.getString("numb").substring(6));
                } while (r.next());
            }
            else {
                System.out.println("Huh, we don't have any phone numbers for you. We should fix that.");
                addCustPhone(c, input, custID);
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void addCustAddress(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement(); Statement t = c.createStatement();
            PreparedStatement p = c.prepareStatement("INSERT INTO cust_add VALUES (?, ?, ?, ?, ?)");) {

            Utility custUtility = new Utility();
            System.out.println("Enter your street address (ex. 123 Sesame Place):");
            String street = custUtility.inputRequestString(input, "^\\d{1,4} \\D+ \\D+$");
            System.out.println("Enter your city/town (must be a word composed of letters only):");
            String city = custUtility.inputRequestString(input, "^\\D+$");
            System.out.println("Enter your state (ex. AB):");
            String state = custUtility.inputRequestString(input, "^[A-Z]{2}$");
            System.out.println("Enter your zipcode (five digits only):");
            int zipcode = Integer.parseInt(custUtility.inputRequestString(input, "^\\d{5}$"));
            p.setString(1, street); p.setString(2, city); p.setString(3, state); p.setInt(4, zipcode);

            try { // instead of wrapper methods & passing things around just do it all here
                // check if an address already exists for the customer
                ResultSet r = s.executeQuery("SELECT * FROM cust_add WHERE cust_id = " + custID);
                if (r.next()) { // delete that address
                    t.executeQuery("DELETE FROM cust_add WHERE cust_id = " + custID);
                }
                p.setInt(5, custID);
                p.executeQuery(); // replace it with the new one
                c.commit();
                System.out.println("Success! Your new address information has been saved.");
            }
            catch (SQLException e) {
                c.rollback();
                System.out.println("Something seems to have gone wrong. Please try again soon.");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
    private static void addCustPhone(Connection c, Scanner input, int custID) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO phone_num VALUES (?, ?, ?)");) {

            Utility custUtility = new Utility();
            System.out.println("Enter the type of line (ex. home, work, cell):");
            String kind = custUtility.inputRequestString(input, "^\\D+$");
            System.out.println("Enter the 10-digit number (no formatting):");
            String number = custUtility.inputRequestString(input, "^\\d{10}$");
            p.setString(2, kind); p.setString(1, number); p.setInt(3, custID);

            try { // no need to check for existing numbers here
                p.executeQuery(); // let 'er rip
                c.commit();
                System.out.println("Success! Your new contact information has been saved.");
            }
            catch (SQLException e) {
                c.rollback();
                System.out.println("Something seems to have gone wrong. Please try again soon.");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
