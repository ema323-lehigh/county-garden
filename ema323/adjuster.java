import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class Adjuster {
    public static void adjusterDriver(Connection c, Scanner input) throws SQLException {
        System.out.println("Which adjuster are you? I will provide a listing for convenience.");
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT adj_id, aname, specialty FROM adjuster ORDER BY aname");
            String[][] adjList = new String[20][3]; int i = 0; // assuming a safe reasonable number of adjusters
            while (r.next()) {
                adjList[i][0] = String.format("%06d", r.getInt("adj_id"));
                adjList[i][1] = r.getString("aname");
                adjList[i][2] = r.getString("specialty");
                i++;
            }
            System.out.println("--------------------------------------------------------------------------------");
            Utility adjUtility = new Utility();
            int adjID = adjUtility.inputRequestByID(adjList, input);
            System.out.println("--------------------------------------------------------------------------------");
            r = s.executeQuery("SELECT aname FROM adjuster WHERE adj_id = " + adjID);
            r.next(); // returns a boolean so we have to advance from up here
            System.out.println("Welcome, " + r.getString("aname").split(" ", 2)[0] + ".");
            while (true) {
                System.out.println("What would you like to do?");
                int choice = adjUtility.inputRequest(new String[] {"assign claims", "manage claims", "quit"}, input);
                switch (choice) {
                    case 1:
                        assignClaims(c, input, adjList);
                        break;
                    case 2:
                        manageClaims(c, input, adjID);
                        break;
                    case 3:
                        System.out.println("Bye now!");
                        System.exit(0);
                        break;
                }
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void assignClaims(Connection c, Scanner input, String[][] adjList) throws SQLException {
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("INSERT INTO manages VALUES (?, ?)");) {
            ResultSet r = s.executeQuery("SELECT * FROM claim WHERE NOT EXISTS (SELECT * FROM manages WHERE manages.claim_id = claim.claim_id)");
            String[][] claimList = new String[100][2]; int i = 0; // assuming a safe reasonable number of claims
            while (r.next()) {
                claimList[i][0] = String.format("%06d", r.getInt("claim_id"));
                claimList[i][1] = r.getString("claim_title") + " - " + r.getString("event_desc");
                i++;
            }
            System.out.println("--------------------------------------------------------------------------------");
            Utility adjUtility = new Utility();
            int claimID = adjUtility.inputRequestByID(claimList, input);
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("To which adjuster would you like to assign this claim? Specialties are noted.");
            int adjID = adjUtility.inputRequestByIDAttribute(adjList, input);
            p.setInt(1, claimID); p.setInt(2, adjID); p.executeQuery(); c.commit();
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Assignment successful; adjuster #" + adjID + " now manages claim #" + claimID + ".");
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
            while (r.next()) {
                claimList[i][0] = String.format("%06d", r.getInt("claim_id"));
                claimList[i][1] = r.getString("claim_title") + " - " + r.getString("event_desc");
                i++;
            }
            System.out.println("--------------------------------------------------------------------------------");
            Utility adjUtility = new Utility();
            int claimID = adjUtility.inputRequestByID(claimList, input);
            System.out.println("--------------------------------------------------------------------------------");
            claimInfo(c, claimID);
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
            System.out.printf("(%06d) %s | %s on %s | submitted on %s\n", claimID, claimTitle, claimLoc, occurredDate, submittedDate);
            System.out.println("The description says: " + claimDesc);
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
