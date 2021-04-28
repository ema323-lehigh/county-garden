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
            try (CallableStatement s = c.prepareCall("{CALL custdata}");) {
                s.execute();
                System.out.println(s);
                s.close();
            }
            catch (SQLException e) {
                System.out.println("Failed to execute. Please try again later.");
                if ((args.length > 0) && args[0].equals("-d")) { System.out.println(e); } 
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
