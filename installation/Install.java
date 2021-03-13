import java.sql.*;
import java.util.Scanner;
import java.io.*;

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
            try {
                BufferedReader initSQLReader = new BufferedReader(new FileReader(new File("init.sql")));
                String currentQ = "";
                String currLine = "";
                while ((currLine = initSQLReader.readLine()) != null) {
                    if (currLine.isEmpty()) {
                        System.out.println(currentQ);
                        //s.executeUpdate(currentQ);
                        currentQ = "";
                    }
                    else {
                        currentQ += currLine;
                    }
                }
                initSQLReader.close();
            }
            catch (IOException e) {
                System.out.println("An error occurred while attempting to read in the install script.");
            }
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
