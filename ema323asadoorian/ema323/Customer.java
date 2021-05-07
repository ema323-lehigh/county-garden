import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;

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
                customerInfo(c, input, custID);
                boolean backout = false;
                while (true) {
                    System.out.println("What would you like to do?");
                    int choice = custUtility.inputRequest(new String[] {"display information", "make a claim", "view claims", "view policies", "pay premiums", "insure items", "remove items", "cancel policies", "update address", "add phone", "remove phone", "back"}, input);
                    switch (choice) {
                        case 1:
                            customerInfo(c, input, custID);
                            break;
                        case 2:
                            makeClaim(c, input, custID);
                            break;
                        case 3:
                            viewClaims(c, input, custID);
                            break;
                        case 4:
                            viewPolicies(c, input, custID);
                            break;
                        case 5:
                            makePayment(c, input, custID);
                            break;
                        case 6:
                            addItem(c, input, custID);
                            break;
                        case 7:
                            removeItem(c, input, custID);
                            break;
                        case 8:
                            cancelPolicy(c, input, custID);
                            break;
                        case 9:
                            addCustAddress(c, input, custID);
                            break;
                        case 10:
                            addCustPhone(c, input, custID);
                            break;
                        case 11:
                            removeCustPhone(c, input, custID);
                            break;
                        case 12:
                            backout = true;
                            break;
                    }
                    if (backout) { break; }
                }
                System.out.println("--------------------------------------------------------------------------------");
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
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void addCustAddress(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement(); Statement t = c.createStatement();
            PreparedStatement p = c.prepareStatement("INSERT INTO cust_add VALUES (?, ?, ?, ?, ?)");) {
            Utility custUtility = new Utility();
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Enter your street address (ex. 123 Sesame Place):");
            System.out.println("You can also optionally add a suite number (ex. Unit 4).");
            System.out.println("(Separate the two portions of the field with a comma.)");
            String street = custUtility.inputRequestString(input, "^(\\d{1,4} \\D+ \\D+(, \\D+ \\d{1,4})?)$");
            if (street.equals("__BACK__")) { return; }
            System.out.println("Enter your city/town (must be a word composed of letters only):");
            String city = custUtility.inputRequestString(input, "^(\\D+)$");
            if (city.equals("__BACK__")) { return; }
            System.out.println("Enter your state (ex. PA):");
            String state = custUtility.inputRequestString(input, "^([A-Z]{2})$");
            if (state.equals("__BACK__")) { return; }
            System.out.println("Enter your zipcode (five digits only):");
            String zipcode = custUtility.inputRequestString(input, "^(\\d{5})$");
            if (zipcode.equals("__BACK__")) { return; }
            int zipcod = Integer.parseInt(zipcode);
            p.setString(1, street); p.setString(2, city); p.setString(3, state); p.setInt(4, zipcod);

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
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }
    private static void addCustPhone(Connection c, Scanner input, int custID) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO phone_num VALUES (?, ?, ?)");) {
            Utility custUtility = new Utility();
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Enter the type of line (ex. home, work, cell):");
            String kind = custUtility.inputRequestString(input, "^(\\D+)$");
            if (kind.equals("__BACK__")) { return; }
            System.out.println("Enter the 10-digit number (no formatting):");
            String number = custUtility.inputRequestString(input, "^(\\d{10})$");
            if (number.equals("__BACK__")) { return; }
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
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }
    private static void removeCustPhone(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("DELETE FROM phone_num WHERE numb = ?");) {
            Utility custUtility = new Utility();
            ResultSet r = s.executeQuery("SELECT * FROM phone_num WHERE cust_id = " + custID);
            String[][] phoneList = new String[20][2]; int i = 0; // assuming a safe reasonable number of numbers
            if (r.next()) {
                do {
                    phoneList[i][0] = r.getString("numb");
                    phoneList[i][1] = r.getString("kind") + " phone: (" + r.getString("numb").substring(0, 3) +
                    ")-" +r.getString("numb").substring(3, 6) + "-" + r.getString("numb").substring(6);
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                String phoneNumb = custUtility.inputRequestByMutedIDString(phoneList, input);
                try {
                    p.setString(1, phoneNumb);
                    p.executeQuery();
                    c.commit();
                    System.out.println("Success! Your old contact information has been removed.");
                }
                catch (SQLException e) {
                    c.rollback();
                    System.out.println("Something seems to have gone wrong. Please try again soon.");
                }
                System.out.println("--------------------------------------------------------------------------------");
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("You don't have any phone numbers! Is this a joke to you?");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void makeClaim(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("INSERT INTO claim VALUES (?, ?, ?, ?, ?, ?, ?)");) {
            Utility custUtility = new Utility();
            ResultSet r = s.executeQuery("SELECT * FROM polisy WHERE cancelled = 0 AND cust_id = " + custID);
            String[][] policyList = new String[20][2]; int i = 0; // assuming a safe reasonable number of policies
            if (r.next()) {
                do {
                    policyList[i][0] = String.format("%06d", r.getInt("policy_id"));
                    policyList[i][1] = r.getString("policy_type") + " - current premium: " +
                                        String.format("$%.2f", r.getDouble("quoted_price"));
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("On which policy would you like to make this claim?");
                int policyID = custUtility.inputRequestByID(policyList, input);
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Enter a title for the claim (50 characters max):");
                String claimTitle = custUtility.inputRequestString(input, "^(.{1,50})$");
                if (claimTitle.equals("__BACK__")) { return; }
                System.out.println("Enter the location where it occurred (100 characters max):");
                String claimLoc = custUtility.inputRequestString(input, "^(.{1,100})$");
                if (claimLoc.equals("__BACK__")) { return; }
                System.out.println("Enter a description of the event (700 characters max):");
                String claimDesc = custUtility.inputRequestString(input, "^(.{1,700})$");
                if (claimDesc.equals("__BACK__")) { return; }
                String occDate = ""; // have to declare this before the loop header
                while (true) {
                    System.out.println("Enter the date this event occurred (YYYY-MM-DD):");
                    occDate = custUtility.inputRequestString(input, "^(\\d{4}-\\d{2}-\\d{2})$");
                    if (occDate.equals("__BACK__")) { return; }
                    try {
                        java.sql.Date datum = new java.sql.Date(0).valueOf(occDate);
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println("That date is invalid.");
                        continue;
                    }
                    break;
                }
                if (occDate.equals("__BACK__")) { return; }
                p.setInt(1, new Random().nextInt(1000000)); p.setString(2, claimTitle); p.setString(3, claimLoc);
                p.setString(4, claimDesc);  p.setDate(5, new java.sql.Date(0).valueOf(occDate));
                p.setDate(6, new java.sql.Date(new java.util.Date().getTime())); p.setInt(7, policyID);

                try {
                    p.executeQuery();
                    c.commit();
                    System.out.println("Success! Your new claim has been made. An adjuster will take care of it soon.");
                }
                catch (SQLException e) {
                    c.rollback();
                    System.out.println("Something seems to have gone wrong. Please try again soon.");
                }
                System.out.println("--------------------------------------------------------------------------------");
            }
            else {
                System.out.println("You don't have any policies! How can you make a claim? An agent will need to help you with that.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void makePayment(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("UPDATE invoice SET payment_type = ? WHERE trans_id = ?");) {
            Utility custUtility = new Utility();
            ResultSet r = s.executeQuery("SELECT * FROM invoice NATURAL JOIN polisy WHERE cust_id = " + custID + " AND cancelled = 0 AND payment_type IS NULL");
            String[][] invoiceList = new String[20][2]; int i = 0; // assuming a safe reasonable number of invoices
            if (r.next()) {
                do {
                    invoiceList[i][0] = String.format("%06d", r.getInt("trans_id"));
                    invoiceList[i][1] = r.getString("policy_type") + " - current premium: " +
                                        String.format("$%.2f", r.getDouble("quoted_price")) +
                                        " | due on " + r.getDate("due_date");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Which invoice would you like to pay down?");
                int transID = custUtility.inputRequestByID(invoiceList, input);
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Enter your payment type:");
                String paymentType = custUtility.inputRequestString(input, "^(\\D{1,20})$");
                if (paymentType.equals("__BACK__")) { return; }
                p.setString(1, paymentType); p.setInt(2, transID);

                try {
                    p.executeQuery();
                    c.commit();
                    System.out.println("Success! Your premium for this month has been paid.");
                }
                catch (SQLException e) {
                    c.rollback();
                    System.out.println("Something seems to have gone wrong. Please try again soon.");
                }
                System.out.println("--------------------------------------------------------------------------------");
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("You don't have any bills to pay! How nice.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void cancelPolicy(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("UPDATE polisy SET cancelled = 1 WHERE policy_id = ?");) {
            Utility custUtility = new Utility();
            ResultSet r = s.executeQuery("SELECT * FROM polisy WHERE cancelled = 0 AND cust_id = " + custID);
            String[][] policyList = new String[20][2]; int i = 0; // assuming a safe reasonable number of policies
            if (r.next()) {
                do {
                    policyList[i][0] = String.format("%06d", r.getInt("policy_id"));
                    policyList[i][1] = r.getString("policy_type") + " - current premium: " +
                                        String.format("$%.2f", r.getDouble("quoted_price"));
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Which policy would you like to cancel?");
                int policyID = custUtility.inputRequestByID(policyList, input);
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Are you sure? (Y/N):");
                String cancelChoice = custUtility.inputRequestString(input, "^(Y|N)$");
                if (cancelChoice.equals("__BACK__")) { return; }
                if (cancelChoice.equals("N")) {
                    System.out.println("Okay, no worries!");
                    System.out.println("--------------------------------------------------------------------------------");
                    return;
                }
                p.setInt(1, policyID);

                try {
                    p.executeQuery();
                    c.commit();
                    System.out.println("Success! Your policy has been cancelled.");
                }
                catch (SQLException e) {
                    c.rollback();
                    System.out.println("Something seems to have gone wrong. Please try again soon.");
                }
                System.out.println("--------------------------------------------------------------------------------");
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("You don't have any policies to cancel...nice try, but you won't fool us that fast.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
    
    private static void viewClaims(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT * FROM claim NATURAL JOIN polisy WHERE cust_id = " + custID);
            String[][] claimList = new String[20][2]; int i = 0; // assuming a safe reasonable number of claims
            if (r.next()) {
                do {
                    claimList[i][0] = String.format("%06d", r.getInt("claim_id"));
                    claimList[i][1] = r.getString("claim_title") + " - " + r.getString("event_desc");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                Utility custUtility = new Utility();
                int claimID = custUtility.inputRequestByID(claimList, input);
                claimInfo(c, input, claimID);
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Looks like you don't have any claims. Feel free to file one!");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
    private static void claimInfo(Connection c, Scanner input, int claimID) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r; // for symmetry/parallelism
            r = s.executeQuery("SELECT * FROM claim WHERE claim_id = " + claimID);
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
            r = s.executeQuery("SELECT * FROM payment WHERE claim_id = " + claimID);
            if (r.next()) {
                double amountPaid = r.getDouble("amount");
                boolean custFinished = (r.getInt("cust_fin") == 1) ? true : false;
                r = s.executeQuery("SELECT * FROM item NATURAL JOIN polisy NATURAL JOIN claim WHERE claim_id = " + claimID);
                double totalInsured = 0.0;
                while (r.next()) {
                    totalInsured += r.getDouble("approx_value");
                }
                double balance = totalInsured - amountPaid;
                if (!custFinished && (balance != 0)) {
                    System.out.printf("The insurance company has paid $%.2f on this claim, leaving you with $%.2f to cover.\n", amountPaid, balance);
                    Utility custUtility = new Utility();
                    System.out.println("Do you want to pay off the deductible? (Y/N):");
                    String payChoice = custUtility.inputRequestString(input, "^(Y|N)$");
                    if (payChoice.equals("__BACK__")) { return; }
                    if (payChoice.equals("N")) {
                        System.out.println("Okay, no worries! It'll still be here later...");
                        System.out.println("--------------------------------------------------------------------------------");
                        return;
                    }
                    System.out.println("Enter your payment type:"); // phantom input
                    String paymentType = custUtility.inputRequestString(input, "^(\\D{1,20})$");
                    if (paymentType.equals("__BACK__")) { return; }
                    try {
                        s.executeQuery("UPDATE payment SET cust_fin = 1 WHERE claim_id = " + claimID);
                        c.commit();
                        System.out.println("Success! Your claim is now fully paid off.");
                    }
                    catch (SQLException e) {
                        c.rollback();
                        System.out.println("Something seems to have gone wrong. Please try again soon.");
                    }
                }
                else {
                    System.out.println("This claim's all paid up.");
                }
            }
            else {
                System.out.println("Looks like this one's still under remediation.");
            }
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void viewPolicies(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r = s.executeQuery("SELECT * FROM polisy WHERE cust_id = " + custID);
            String[][] policyList = new String[20][2]; int i = 0; // assuming a safe reasonable number of policies
            if (r.next()) {
                do {
                    policyList[i][0] = String.format("%06d", r.getInt("policy_id"));
                    policyList[i][1] = r.getString("policy_type");
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                Utility custUtility = new Utility();
                int policyID = custUtility.inputRequestByID(policyList, input);
                policyInfo(c, input, policyID);
            }
            else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Looks like you don't have any policies. An agent will need to sign you up.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
    private static void policyInfo(Connection c, Scanner input, int policyID) throws SQLException {
        try (Statement s = c.createStatement();) {
            ResultSet r; // for symmetry/parallelism
            r = s.executeQuery("SELECT * FROM polisy WHERE policy_id = " + policyID);
            r.next(); // we already have policyID
            String policyType = r.getString("policy_type");
            double quotedPrice = r.getDouble("quoted_price");
            String policyStatus = (r.getInt("cancelled") == 1) ? "cancelled" : "active";
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("(%06d) %s\n", policyID, policyType);
            System.out.printf("%s | $%.2f/month\n", policyStatus, quotedPrice);
            r = s.executeQuery("SELECT * FROM item WHERE policy_id = " + policyID);
            double totalInsured = 0.0;
            while (r.next()) {
                totalInsured += r.getDouble("approx_value");
            }
            System.out.printf("total value of items insured: $%.2f\n", totalInsured);
            System.out.println("--------------------------------------------------------------------------------");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    private static void addItem(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("INSERT INTO item VALUES (?, ?, ?, ?)");) {
            Utility custUtility = new Utility();
            ResultSet r = s.executeQuery("SELECT * FROM polisy WHERE cancelled = 0 AND cust_id = " + custID);
            String[][] policyList = new String[20][2]; int i = 0; // assuming a safe reasonable number of policies
            if (r.next()) {
                do {
                    policyList[i][0] = String.format("%06d", r.getInt("policy_id"));
                    policyList[i][1] = r.getString("policy_type") + " - current premium: " +
                                        String.format("$%.2f", r.getDouble("quoted_price"));
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("On which policy would you like to add an item?");
                int policyID = custUtility.inputRequestByID(policyList, input);
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Enter a categorical description for the item (50 characters max):");
                String itemDesc = custUtility.inputRequestString(input, "^(.{1,50})$");
                if (itemDesc.equals("__BACK__")) { return; }
                System.out.println("Enter an approximate value for the item (dollars & cents, up to 6 digits of dollars):");
                String approxValStr = custUtility.inputRequestString(input, "^(\\d{1,6}\\.\\d{2})$");
                if (approxValStr.equals("__BACK__")) { return; }
                double approxValue = Double.parseDouble(approxValStr);
                p.setInt(1, new Random().nextInt(1000000)); p.setString(2, itemDesc);
                p.setDouble(3, approxValue); p.setInt(4, policyID);

                try {
                    p.executeQuery();
                    c.commit();
                    System.out.println("Success! Your new item has been added to your policy.");
                }
                catch (SQLException e) {
                    c.rollback();
                    System.out.println("Something seems to have gone wrong. Please try again soon.");
                }
                System.out.println("--------------------------------------------------------------------------------");
            }
            else {
                System.out.println("You don't have any policies! How can you insure any items? An agent will need to help you with that.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
    private static void removeItem(Connection c, Scanner input, int custID) throws SQLException {
        try (Statement s = c.createStatement();
            PreparedStatement p = c.prepareStatement("DELETE FROM item WHERE item_id = ?");) {
            Utility custUtility = new Utility();
            ResultSet r; // for symmetry/parallelism
            r = s.executeQuery("SELECT * FROM polisy WHERE cancelled = 0 AND cust_id = " + custID);
            String[][] policyList = new String[20][2]; int i = 0; // assuming a safe reasonable number of policies
            if (r.next()) {
                do {
                    policyList[i][0] = String.format("%06d", r.getInt("policy_id"));
                    policyList[i][1] = r.getString("policy_type") + " - current premium: " +
                                        String.format("$%.2f", r.getDouble("quoted_price"));
                    i++;
                } while (r.next());
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("From which policy would you like to remove an item?");
                int policyID = custUtility.inputRequestByID(policyList, input);
                System.out.println("--------------------------------------------------------------------------------");
                r = s.executeQuery("SELECT * FROM item WHERE policy_id = " + policyID);
                String[][] itemList = new String[100][2]; int j = 0; // assuming a safe reasonable number of items
                if (r.next()) {
                    do {
                        itemList[j][0] = String.format("%06d", r.getInt("item_id"));
                        itemList[j][1] = r.getString("item_desc") + " - approximate value: " +
                                            String.format("$%.2f", r.getDouble("approx_value"));
                        j++;
                    } while (r.next());
                    System.out.println("Which item would you like to remove?");
                    int itemID = custUtility.inputRequestByID(itemList, input);
                    System.out.println("--------------------------------------------------------------------------------");

                    p.setInt(1, itemID);

                    try {
                        p.executeQuery();
                        c.commit();
                        System.out.println("Success! That item has been removed from your policy.");
                    }
                    catch (SQLException e) {
                        c.rollback();
                        System.out.println("Something seems to have gone wrong. Please try again soon.");
                    }
                    System.out.println("--------------------------------------------------------------------------------");
                }
                else {
                    System.out.println("There are no items covered by this policy.");
                    System.out.println("--------------------------------------------------------------------------------");
                }
            }
            else {
                System.out.println("You don't have any policies! How can you insure any items? An agent will need to help you with that.");
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }
}
