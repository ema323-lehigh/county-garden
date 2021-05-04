import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class ema323 {
    public static void main(String[] args) {
        System.out.println("--------------------------------------------------------------------------------");
        Scanner input = new Scanner(System.in);
        System.out.println("What's your Oracle username?");
        String username = input.nextLine();
        System.out.println("What's your Oracle password?");
        String password = input.nextLine();
        System.out.println("Connecting...");
        String dbdeets = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

        try (Connection c = DriverManager.getConnection(dbdeets, username, password);) {
            System.out.println("...connected.");
            System.out.println("--------------------------------------------------------------------------------");
            c.setAutoCommit(false);
            System.out.println("Welcome. Are you...which type of user in our system?");
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
            c.close();
        }
        catch (SQLException e) {
            System.out.println("Failed to connect. Please try again later.");
            if ((args.length > 0) && args[0].equals("-d")) { System.out.println(e); }
        }
        finally {
            input.close();
            System.out.println("--------------------------------------------------------------------------------");
        }
    }
}
