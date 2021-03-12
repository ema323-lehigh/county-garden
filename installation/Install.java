import java.sql.*;
import java.util.Scanner;

public class Install {
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
            System.out.println("Bye.");
            s.close();
            c.close();
        }
        catch (SQLException e) {
            System.out.println("Failed to connect. Please try again later.");
        }
        finally {
            input.close();
            System.out.println("--------------------------------------------------------------------------------");
        }
    }
}
