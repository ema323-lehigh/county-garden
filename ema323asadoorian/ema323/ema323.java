import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class ema323 {
    public static void main(String[] args) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Before we make any introductions, we need your login details !");
        Scanner input = new Scanner(System.in);
        System.out.println("What's your Oracle username?");
        String username = input.nextLine();
        System.out.println("What's your Oracle password?");
        String password = input.nextLine();
        System.out.println("Connecting...");
        String dbdeets = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

        try (Connection c = DriverManager.getConnection(dbdeets, username, password);) {
            System.out.println("...connected.");
            System.out.println("(Please expand the width & height of your terminal window as much as possible!)");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Welcome to County Garden Insurance! We're thrilled to have you.");
            System.out.println("Please find the navigation options as follows:");
            System.out.println("- When presented with a menu with numbered bullet items, enter a numbered choice as such.");
            System.out.println("- When presented with a freeform input, follow the indicated formatting (some more specific than others).");
            System.out.println("- Most action menus will have a 'back' option to take you up to the last action menu seen.");
            System.out.println("- Numbered menus presenting a data choice will not have this backout option.");
            System.out.println("- When entering freeform input, the special code '__BACK__' will allow you to exit the entry action.");
            System.out.println("- There are some numbered data menus that represent the go-ahead on a data update.");
            System.out.println("- These will not have a back option of any kind, but don't sweat it! We can always re-up the database :).");
            System.out.println("So, with all that said...!");
            System.out.println("--------------------------------------------------------------------------------");
            c.setAutoCommit(false);
            while (true) {
                System.out.println("Are you...which type of user in our system?");
                Utility mainUtility = new Utility(); // pull in object from shared functions class
                int choice = mainUtility.inputRequest(new String[] {"agent", "adjuster", "customer"}, input);
                switch (choice) {
                    case 1:
                        System.out.println("Wonderful to have you doing business for us.");
                        Agent agent = new Agent(); agent.agentDriver(c, input);
                        break;
                    case 2:
                        System.out.println("Wonderful to have you doing business for us.");
                        Adjuster adjuster = new Adjuster(); adjuster.adjusterDriver(c, input);
                        break;
                    case 3:
                        System.out.println("Wonderful to have you doing business with us.");
                        Customer customer = new Customer(); customer.customerDriver(c, input);
                        break;
                }
            }
            //c.close();
        }
        catch (SQLRecoverableException e) {
            System.out.println("Looks like your connection timed out. Please try again later!");
        }
        catch (SQLException e) {
            System.out.println("Failed to connect or execute. Please try again later.");
            //if ((args.length > 0) && args[0].equals("-d")) { System.out.println(e); }
        }
        finally {
            input.close();
            System.out.println("--------------------------------------------------------------------------------");
        }
    }
}
