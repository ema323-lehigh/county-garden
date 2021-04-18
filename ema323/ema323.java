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

        try (
            Connection c = DriverManager.getConnection(dbdeets, username, password);
            Statement s = c.createStatement();
        ) {
            System.out.println("...connected.");
            System.out.println("--------------------------------------------------------------------------------");
            c.setAutoCommit(false);
            ResultSet r = s.executeQuery("SELECT * FROM customer");
            while (r.next()) {
                System.out.println(r.getString("firstname") + " " + r.getString("lastname"));
            }     
            s.close();
            c.close();
        }
        catch (SQLException e) {
            System.out.println("Failed to connect or execute. Please try again later.");
            if ((args.length > 0) && args[0].equals("-d")) { System.out.print(e); }
        }
        finally {
            input.close();
            System.out.println("--------------------------------------------------------------------------------");
        }
    }
}
