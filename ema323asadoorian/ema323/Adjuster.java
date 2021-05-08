import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class Adjuster {
    public static void adjusterDriver(Connection c, Scanner input) throws SQLException {
        System.out.println("Which adjuster are you? I will provide a listing for convenience.");
        try (Statement s = c.createStatement()) {
            int adjID = selectAdjuster(c, input);
            if (adjID != -1) {
                ResultSet r = s.executeQuery("SELECT aname FROM adjuster WHERE adj_id = " + adjID);
                r.next(); // returns a boolean so we have to advance from up here
                System.out.println("Welcome, " + r.getString("aname").split(" ", 2)[0] + ".");
                boolean backout = false;
                while (true) {
                    System.out.println("What would you like to do?");
                    Utility adjUtility = new Utility();
                    int choice = adjUtility.inputRequest(new String[] {"assign claims", "manage claims", "back"}, input);
                    switch (choice) {
                        case 1:
                            assignClaims(c, input);
                            break;
                        case 2:
                            manageClaims(c, input, adjID);
                            break;
                        case 3:
                            backout = true;
                            break;
                    }
                    if (backout) { break; }
                }
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Well, looks like we don't have any adjusters after all.");
                System.out.println("We've no mechanism by which to quit, so I don't see how that could be.");
                System.out.println("Either way...scram! :)");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void assignClaims(Connection c, Scanner input) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT * FROM claim WHERE NOT EXISTS (SELECT * FROM manages WHERE manages.claim_id = claim.claim_id) AND NOT EXISTS (SELECT * FROM payment WHERE payment.claim_id = claim.claim_id) AND NOT EXISTS (SELECT * FROM polisy WHERE polisy.policy_id = claim.policy_id AND polisy.cancelled = 1)");
            String[][] claimList = new String[100][2]; int i = 0; // assuming a safe reasonable number of claims
            if (r.next()) {
                do {
                    claimList[i][0] = String.format("%06d", r.getInt("claim_id"));
                    claimList[i][1] = r.getString("claim_title") + " - " + r.getString("event_desc");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                Utility adjUtility = new Utility();
                int claimID = adjUtility.inputRequestByID(claimList, input);
                addAdjuster(c, input, claimID);
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Looks like we don't have any claims. A customer will have to make one first.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void manageClaims(Connection c, Scanner input, int adjID) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO services VALUES (?, ?, ?)");
            Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT * FROM claim NATURAL JOIN manages NATURAL JOIN polisy WHERE polisy.cancelled = 0 AND manages.adj_id = " + adjID + " ORDER BY occurred_date");
            String[][] claimList = new String[100][2]; int i = 0; // assuming a safe reasonable number of claims
            if (r.next()) {
                do {
                    claimList[i][0] = String.format("%06d", r.getInt("claim_id"));
                    claimList[i][1] = r.getString("claim_title") + " - " + r.getString("event_desc");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                Utility adjUtility = new Utility();
                int claimID = adjUtility.inputRequestByID(claimList, input);
                claimInfo(c, claimID);
                boolean backout = false;
                while (true) {
                    System.out.println("What would you like to do?");
                    int choice = adjUtility.inputRequest(new String[] {"display information", "add an adjuster", "add a contractor", "make a payment", "back"}, input);
                    switch (choice) {
                        case 1:
                            claimInfo(c, claimID);
                            break;
                        case 2:
                            addAdjuster(c, input, claimID);
                            break;
                        case 3:
                            addContractor(c, input, claimID);
                            break;
                        case 4:
                            makePayment(c, input, claimID);
                            break;
                        case 5:
                            backout = true;
                            break;
                    }
                    if (backout) { break; }
                }
                System.out.println("--------------------------------------------------------------------------------");
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Looks like you don't manage any claims. Better get on that.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void claimInfo(Connection c, int claimID) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r; // for symmetry/parallelism
            r = s.executeQuery("SELECT * FROM claim NATURAL JOIN polisy NATURAL JOIN customer WHERE claim_id = " + claimID);
            r.next(); // we already have claimID
            String claimTitle = r.getString("claim_title");
            String claimLoc = r.getString("event_loc");
            String claimDesc = r.getString("event_desc");
            String occurredDate = String.valueOf(r.getDate("occurred_date"));
            String submittedDate = String.valueOf(r.getDate("submitted_date"));
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("(%06d) %s | %s\n", claimID, claimTitle, claimLoc);
            System.out.printf("occurred on %s | submitted on %s\n", occurredDate, submittedDate);
            System.out.println("The description says: " + claimDesc);
            System.out.printf("Filed by %s %s, customer #%06d w/ policy #%06d.\n",
            r.getString("fname"), r.getString("lname"), r.getInt("cust_id"), r.getInt("policy_id"));
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void addAdjuster(Connection c, Scanner input, int claimID) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO manages VALUES (?, ?)");
            Statement s = c.createStatement();) {
            System.out.println("--------------------------------------------------------------------------------");
            ResultSet r = s.executeQuery("SELECT * FROM payment WHERE claim_id = " + claimID);
            if (r.next()) {
                System.out.println("Looks like this claim's already been paid by us. Nothing else to do here.");
                System.out.println("--------------------------------------------------------------------------------");
                return;
            }
            int adjID = 0; // have to declare this before the loop header
            while (true) {
                System.out.println("To which adjuster would you like to assign this claim? Specialties are noted.");
                adjID = selectAdjuster(c, input);
                p.setInt(1, claimID); p.setInt(2, adjID);
                if (s.executeQuery("SELECT * FROM manages WHERE claim_id = " + claimID + " AND adj_id = " + adjID).next()) {
                    System.out.println("That adjuster already manages this claim.");
                    continue;
                }
                break;
            }
            try {
                p.executeQuery();
                c.commit();
                System.out.printf("Assignment successful; adjuster #%06d now manages claim #%06d.\n", adjID, claimID);
            }
            catch (SQLException e) {
                System.out.println("Looks like something went wrong. Please try again later.");
            }
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void addContractor(Connection c, Scanner input, int claimID) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO services VALUES (?, ?, ?)");
            Statement s = c.createStatement();) {
            System.out.println("--------------------------------------------------------------------------------");
            ResultSet r = s.executeQuery("SELECT * FROM payment WHERE claim_id = " + claimID);
            if (r.next()) {
                System.out.println("Looks like this claim's already been paid by us. Nothing else to do here.");
                System.out.println("--------------------------------------------------------------------------------");
                return;
            }
            int firmID = 0; // have to declare this before the loop header
            while (true) {
                System.out.println("To which contractor would you like to assign this claim? Industries are noted.");
                firmID = selectContractor(c, input);
                p.setInt(2, firmID); p.setInt(3, claimID);
                p.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
                if (s.executeQuery("SELECT * FROM services WHERE claim_id = " + claimID + " AND firm_id = " + firmID).next()) {
                    System.out.println("That firm already services this claim.");
                    continue;
                }
                break;
            }
            try {
                p.executeQuery();
                c.commit();
                System.out.printf("Assignment successful; contractor #%06d now services claim #%06d.\n", firmID, claimID);
            }
            catch (SQLException e) {
                System.out.println("Looks like something went wrong. Please try again later.");
            }
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void makePayment(Connection c, Scanner input, int claimID) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO payment VALUES (?, ?, ?, 0, ?)");
            Statement s = c.createStatement();) {
            Utility adjUtility = new Utility();
            System.out.println("--------------------------------------------------------------------------------");
            ResultSet r = s.executeQuery("SELECT * FROM payment WHERE claim_id = " + claimID);
            if (r.next()) {
                System.out.println("Looks like this claim's already been paid by us. Nothing else to do here.");
                System.out.println("--------------------------------------------------------------------------------");
                return;
            }
            r = s.executeQuery("SELECT * FROM item NATURAL JOIN polisy NATURAL JOIN claim WHERE claim_id = " + claimID);
            double totalInsured = 0.0;
            while (r.next()) {
                totalInsured += r.getDouble("approx_value");
            }
            System.out.printf("The total value of the items insured by this claim's underlying policy is $%.2f.\n", totalInsured);
            double amountAmt = 0.0; // have to declare this before the loop header
            while (true) {
                System.out.println("Enter the amount we will be compensating on this claim (dollars & cents, up to 18 digits of dollars):");
                String amountStr = adjUtility.inputRequestString(input, "^(\\d{1,18}\\.\\d{2})$");
                if (amountStr.equals("__BACK__")) { return; }
                amountAmt = Double.parseDouble(amountStr);
                if (amountAmt > totalInsured) {
                    System.out.println("Please enter a value no greater than the total amount insured.");
                    continue;
                }
                break;
            }
            p.setInt(1, new Random().nextInt(1000000)); p.setDouble(2, amountAmt);
            p.setDate(3, new java.sql.Date(new java.util.Date().getTime())); p.setInt(4, claimID);

            try { // ID may fail? but probably not
                p.executeQuery();
                c.commit();
                System.out.println("The payment update has been saved.");
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

    private static int selectAdjuster(Connection c, Scanner input) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT adj_id, aname, specialty FROM adjuster ORDER BY aname");
            String[][] adjList = new String[20][3]; int i = 0; // assuming a safe reasonable number of adjusters
            if (r.next()) {
                do {
                    adjList[i][0] = String.format("%06d", r.getInt("adj_id"));
                    adjList[i][1] = r.getString("aname");
                    adjList[i][2] = r.getString("specialty");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                Utility adjUtility = new Utility();
                int adjID = adjUtility.inputRequestByIDAttribute(adjList, input);
                System.out.println("--------------------------------------------------------------------------------");
                return adjID;
            }
            else {
                System.out.println("Uh...anybody home? I guess there are no adjusters around anyway.");
                return -1;
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
    private static int selectContractor(Connection c, Scanner input) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT firm_id, cname, industry FROM contractor ORDER BY cname");
            String[][] firmList = new String[20][3]; int i = 0; // assuming a safe reasonable number of contractors
            if (r.next()) {
                do {
                    firmList[i][0] = String.format("%06d", r.getInt("firm_id"));
                    firmList[i][1] = r.getString("cname");
                    firmList[i][2] = r.getString("industry");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                Utility firmUtility = new Utility();
                int firmID = firmUtility.inputRequestByIDAttribute(firmList, input);
                System.out.println("--------------------------------------------------------------------------------");
                return firmID;
            }
            else {
                System.out.println("Wow, looks like we don't have any contractors in here. Better add some.");
                return -1;
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
