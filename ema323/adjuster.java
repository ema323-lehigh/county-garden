import java.sql.*;
import java.io.*;
import java.util.Scanner;

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
            ResultSet r = s.executeQuery("SELECT * FROM claim WHERE NOT EXISTS (SELECT * FROM manages WHERE manages.claim_id = claim.claim_id)");
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
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("INSERT INTO services VALUES (?, ?, ?)");) {
            ResultSet r = s.executeQuery("SELECT * FROM claim NATURAL JOIN manages WHERE manages.adj_id = " + adjID + " ORDER BY occurred_date");
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
                    int choice = adjUtility.inputRequest(new String[] {"add an adjuster", "add a contractor", "make a payment", "back"}, input);
                    switch (choice) {
                        case 1:
                            addAdjuster(c, input, claimID);
                            break;
                        case 2:
                            addContractor(c, input, claimID);
                            break;
                        case 3:
                            makePayment(c, input, claimID);
                            break;
                        case 4:
                            backout = true;
                            break;
                    }
                    if (backout) { break; }
                }
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
            r = s.executeQuery("SELECT * FROM claim WHERE claim_id = " + claimID);
            r.next(); // we already have claimID
            String claimTitle = r.getString("claim_title");
            String claimLoc = r.getString("event_loc");
            String claimDesc = r.getString("event_desc");
            String occurredDate = String.valueOf(r.getTimestamp("occurred_date"));
            String submittedDate = String.valueOf(r.getTimestamp("submitted_date"));
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("(%06d) %s | %s on %s | submitted on %s\n", claimID, claimTitle, claimLoc, occurredDate, submittedDate);
            System.out.println("The description says: " + claimDesc);
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void addAdjuster(Connection c, Scanner input, int claimID) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO manages VALUES (?, ?)");) {
            System.out.println("To which adjuster would you like to assign this claim? Specialties are noted.");
            int adjID = selectAdjuster(c, input);
            p.setInt(1, claimID); p.setInt(2, adjID);
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
        /*try {
            System.out.println("Shell method");
        }
        catch (SQLException e) {
            throw e;
        }*/
        System.out.println("bloop");
    }

    private static void makePayment(Connection c, Scanner input, int claimID) throws SQLException {
        /*try {
            System.out.println("Shell method");
        }
        catch (SQLException e) {
            throw e;
        }*/
        System.out.println("bloop");
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
}
