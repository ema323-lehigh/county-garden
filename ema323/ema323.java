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
            int choice = inputRequest(new String[] {"agent", "adjuster", "customer"}, input);
            switch (choice) {
                case 1:
                    System.out.println("Wonderful to have you doing business for us.");
                    agent agentt = new agent(); agentt.agentDriver(c, input);
                    break;
                case 2:
                    System.out.println("Wonderful to have you doing business for us.");
                    adjuster adjusterr = new adjuster(); adjusterr.adjusterDriver(c, input);
                    break;
                case 3:
                    System.out.println("Wonderful to have you doing business with us.");
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

    public static int inputRequest(String[] choices, Scanner input) {
        int choice;
        for (int i = 0; i < choices.length; i++) {
            System.out.println((i + 1) + ") " + choices[i]);
        }
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if ((choice > 0) && (choice <= choices.length)) {
                    return choice;
                }
                else {
                    System.out.println("Please enter an integer corresponding to one of the choices above.");
                }
            }
            else {
                System.out.println("Please enter an integer corresponding to one of the choices above.");
                input.nextLine();
            }
        }
    }
}
