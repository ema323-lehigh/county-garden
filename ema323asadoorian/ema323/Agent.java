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
                    int choice = agentUtility.inputRequest(new String[] {"list my customers", "add a customer", "create a policy", "generate invoices", "check for deliquency", "back"}, input);
                    switch (choice) {
                        case 1:
                            listCustomers(c, agentID);
                            break;
                        case 2:
                            addNewCustomer(c, input, agentID);
                            break;
                        case 3:
                            addCustomerPolicy(c, input, agentID);
                            break;
                        case 4:
                            generateInvoices(c, input, agentID);
                            break;
                        case 5:
                            reapDeliquents(c, input, agentID);
                            break;
                        case 6:
                            backout = true;
                            break;
                    }
                    if (backout) { break; }
                }
                System.out.println("--------------------------------------------------------------------------------");
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
            String fname = agentUtility.inputRequestString(input, "^(\\D{1,50})$");
            if (fname.equals("__BACK__")) { return; }
            System.out.println("Enter the customer's middle initial (a single '-' for none):");
            String minitial = agentUtility.inputRequestString(input, "^(\\D|-)$");
            if (minitial.equals("__BACK__")) { return; }
            if (minitial.equals("-")) { minitial = ""; }
            System.out.println("Enter the customer's last name:");
            String lname = agentUtility.inputRequestString(input, "^(\\D{1,50})$");
            if (lname.equals("__BACK__")) { return; }
            System.out.println("Enter the customer's suffix/title (a single '-' for none):");
            String suffix = agentUtility.inputRequestString(input, "^(\\D{1,10})$");
            if (suffix.equals("__BACK__")) { return; }
            if (suffix.equals("-")) { minitial = ""; }
            String birthDate = ""; // have to declare this before the loop header
            while (true) {
                System.out.println("Enter the customer's date of birth (YYYY-MM-DD):");
                birthDate = agentUtility.inputRequestString(input, "^(\\d{4}-\\d{2}-\\d{2})$");
                if (birthDate.equals("__BACK__")) { return; }
                try {
                    java.sql.Date datum = new java.sql.Date(0).valueOf(birthDate);
                }
                catch (IllegalArgumentException e) {
                    System.out.println("That date is invalid.");
                    continue;
                }
                break;
            }
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

    private static void listCustomers(Connection c, int agentID) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT * FROM customer WHERE agent_id = " + agentID);
            if (r.next()) {
                do {
                    customerInfo(c, r.getInt("cust_id"));
                } while (r.next());
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("What a schmuck! You don't have any customers!");
                System.out.println("--------------------------------------------------------------------------------");
            }
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
            String birthDate = String.valueOf(r.getDate("birth_date"));
            r = s.executeQuery("SELECT agent_id, aname FROM agent WHERE agent.agent_id = " + r.getInt("agent_id"));
            r.next(); String agentID = r.getString("agent_id"); String agentName = r.getString("aname");
            r = s.executeQuery("SELECT COUNT(*) FROM polisy WHERE cancelled = 0 AND cust_id = " + custID);
            int numPolicies = 0; if (r.next()) { numPolicies = r.getInt(1); }
            r = s.executeQuery("SELECT COUNT(*) FROM dependentt WHERE cust_id = " + custID);
            int numDependents = 0; if (r.next()) { numDependents = r.getInt(1); }

            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("(%06d) %s | %d policies | %d dependents | DOB: %s\n", custID, custName, numPolicies, numDependents, birthDate);
            r = s.executeQuery("SELECT * FROM cust_add WHERE cust_id = " + custID);
            if (r.next()) {
                System.out.println(r.getString("street") + ", " + r.getString("city") + ", " + r.getString("astate") + ", " + String.format("%05d", r.getInt("zipcode")));
            }
            else {
                System.out.println("Huh, they don't have an address. They should fix that.");
            }
            r = s.executeQuery("SELECT * FROM phone_num WHERE cust_id = " + custID);
            if (r.next()) {
                do {
                    System.out.println(r.getString("kind") + " phone: (" + r.getString("numb").substring(0, 3) +
                    ")-" +r.getString("numb").substring(3, 6) + "-" + r.getString("numb").substring(6));
                } while (r.next());
            }
            else {
                System.out.println("Huh, they don't have any phone numbers. They should fix that.");
            }
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void addCustomerPolicy(Connection c, Scanner input, int agentID) throws SQLException {
       try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("INSERT INTO polisy VALUES (?, ?, ?, 0, ?)");) {
            Utility agentUtility = new Utility();
            System.out.println("Which customer is requesting a new policy?");
            int custID = selectCustomer(c, input, agentID);
            if (custID != -1) {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Enter the type of policy:");
                String policyType = agentUtility.inputRequestString(input, "^(\\D{1,50})$");
                if (policyType.equals("__BACK__")) { return; }
                System.out.println("Enter the monthly premium price you're quoting (dollars & cents, up to 7 digits of dollars):");
                String premiumStr = agentUtility.inputRequestString(input, "^(\\d{1,7}\\.\\d{2})$");
                if (premiumStr.equals("__BACK__")) { return; }
                double quotedPrice = Double.parseDouble(premiumStr);
                p.setInt(1, new Random().nextInt(1000000)); p.setInt(4, custID);
                p.setString(2, policyType); p.setDouble(3, quotedPrice);

                try {
                    p.executeQuery();
                    c.commit();
                    System.out.println("Success! Your customer now has a new policy.");
                }
                catch (SQLException e) {
                    c.rollback();
                    System.out.println("Something seems to have gone wrong. Please try again soon.");
                }
                System.out.println("--------------------------------------------------------------------------------");
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                return;
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static int selectCustomer(Connection c, Scanner input, int agentID) throws SQLException {
        try (Statement s = c.createStatement()) {
            ResultSet r = s.executeQuery("SELECT cust_id, fname, lname FROM customer WHERE agent_id = " + agentID + " ORDER BY lname");
            String[][] custList = new String[100][2]; int i = 0; // assuming a safe reasonable number of customers
            if (r.next()) {
                do {
                    custList[i][0] = String.format("%06d", r.getInt("cust_id"));
                    custList[i][1] = r.getString("fname") + " " + r.getString("lname");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                Utility agentUtility = new Utility();
                int custID = agentUtility.inputRequestByMutedID(custList, input);
                System.out.println("--------------------------------------------------------------------------------");
                return custID;
            }
            else {
                System.out.println("Whoops, guess you don't have any. Sorry!");
                return -1;
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void generateInvoices(Connection c, Scanner input, int agentID) throws SQLException {
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("INSERT INTO invoice VALUES (?, ?, NULL, ?)");) {
            ResultSet r = s.executeQuery("SELECT * FROM polisy NATURAL JOIN customer WHERE agent_id = " + agentID + " AND cancelled = 0 AND NOT EXISTS (SELECT * FROM invoice WHERE invoice.policy_id = polisy.policy_id AND invoice.payment_type IS NULL AND invoice.due_date > CURRENT_DATE)");
            if (r.next()) {
                Integer[] policyIDArray = new Integer[200]; int i = 0;
                // assuming a safe reasonable number of policies
                // and using an Integer array to check for nulls
                System.out.println("--------------------------------------------------------------------------------");
                do {
                    System.out.printf("Policy #%06d - customer will owe $%.2f\n", r.getInt("policy_id"), r.getDouble("quoted_price"));
                    policyIDArray[i] = r.getInt("policy_id");
                    i++;
                } while (r.next());
                Utility agentUtility = new Utility();
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("How will you enter the due dates (enter 'x days out' or 'specific date')?");
                String outDate = "";
                switch (agentUtility.inputRequestString(input, "^((x days out)|(specific date))$")) {
                    case "x days out":
                        System.out.println("Enter a number of days (ex. 30):");
                        outDate = agentUtility.inputRequestString(input, "^(\\d{1,3})$");
                        // convert today to our desired format
                        String today = new SimpleDateFormat("YYYY-MM-dd").format(new java.util.Date());
                        // make a Gregorian hocus-pocus
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(outDate));
                        java.util.Date outDays = cal.getTime();
                        // put it back into our desired format
                        outDate = new SimpleDateFormat("YYYY-MM-dd").format(outDays);
                        break;
                    case "specific date":
                        while (true) {
                            System.out.println("Enter a date in YYYY-MM-DD format:");
                            outDate = agentUtility.inputRequestString(input, "^(\\d{4}-\\d{2}-\\d{2})$");
                            if (outDate.equals("__BACK__")) { return; }
                            try {
                                java.sql.Date datum = new java.sql.Date(0).valueOf(outDate);
                            }
                            catch (IllegalArgumentException e) {
                                System.out.println("That date is invalid.");
                                continue;
                            }
                            break;
                        }
                        break;
                    case "__BACK__":
                        return;
                }
                for (int j = 0; j < policyIDArray.length; j++) {
                    if (policyIDArray[j] != null) {
                        p.setInt(1, new Random().nextInt(1000000));
                        p.setInt(3, policyIDArray[j]);
                        p.setDate(2, new java.sql.Date(0).valueOf(outDate));

                        try {
                            p.executeQuery();
                            // don't commit here because we didn't finish generating yet
                        }
                        catch (SQLException e) {
                            c.rollback();
                            System.out.println("Something seems to have gone wrong. Please try again soon.");
                            return;
                        }
                    }
                }
                try {
                    c.commit(); // now we're free to commit
                }
                catch (SQLException e) {
                    c.rollback();
                    System.out.println("Something seems to have gone wrong. Please try again soon.");
                    return;
                }
                System.out.println("Success! Your customers' invoices have been generated.");
                System.out.println("--------------------------------------------------------------------------------");
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("There are no active policies without a current standing invoice.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void reapDeliquents(Connection c, Scanner input, int agentID) throws SQLException {
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("DELETE FROM customer WHERE cust_id = ?");) {
            ResultSet r = s.executeQuery("SELECT * FROM polisy NATURAL JOIN customer NATURAL JOIN invoice WHERE agent_id = " + agentID + " AND cancelled = 0 AND invoice.due_date <= CURRENT_DATE AND invoice.payment_type IS NULL");
            if (r.next()) {
                Integer[] custIDArray = new Integer[200]; int i = 0;
                // assuming a safe reasonable number of customers
                // and using an Integer array to check for nulls
                System.out.println("--------------------------------------------------------------------------------");
                do {
                    System.out.printf("Customer #%06d - unpaid invoice dated %s\n", r.getInt("cust_id"), String.valueOf(r.getDate("due_date")));
                    custIDArray[i] = r.getInt("cust_id");
                    i++;
                } while (r.next());
                Utility agentUtility = new Utility();
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Do you want to boot them from the system? (Y/N):");
                String bootChoice = agentUtility.inputRequestString(input, "^(Y|N)$");
                if (bootChoice.equals("__BACK__")) { return; }
                if (bootChoice.equals("N")) {
                    System.out.println("Okay, no worries! They get to live another day...");
                    System.out.println("--------------------------------------------------------------------------------");
                    return;
                }
                for (int j = 0; j < custIDArray.length; j++) {
                    if (custIDArray[j] != null) {
                        p.setInt(1, custIDArray[j]);

                        try {
                            p.executeQuery();
                            // don't commit here because we didn't finish generating yet
                        }
                        catch (SQLException e) {
                            c.rollback();
                            System.out.println("Something seems to have gone wrong. Please try again soon.");
                            return;
                        }
                    }
                }
                try {
                    c.commit(); // now we're free to commit
                }
                catch (SQLException e) {
                    c.rollback();
                    System.out.println("Something seems to have gone wrong. Please try again soon.");
                    return;
                }
                System.out.println("Success! Your delinquent customers have been vanquished from our fair halls.");
                System.out.println("--------------------------------------------------------------------------------");
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("There are no customers with unpaid past-due invoices. ':)");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
