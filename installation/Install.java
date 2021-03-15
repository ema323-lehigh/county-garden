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
                c.setAutoCommit(false);
                BufferedReader initSQLReader = new BufferedReader(new FileReader(new File("init.sql")));
                String currentQ = "";
                String currLine = "";
                String dropQ = "";
                String createQ = "";
                while ((currLine = initSQLReader.readLine()) != null) {
                    if (currLine.isEmpty()) {
                        dropQ = currentQ.split(";")[0];
                        createQ = currentQ.split(";")[1];
                        System.out.println("Dropping table " + dropQ.split(" ")[2] + "...");
                        s.executeUpdate(dropQ);
                        System.out.println("Creating table " + createQ.split(" ")[2] + "...");
                        s.executeUpdate(createQ);
                        System.out.println("Done.");
                        currentQ = "";
                    }
                    else {
                        currentQ += currLine;
                    }
                }
                initSQLReader.close();
                c.commit();
            }
            catch (IOException e) {
                System.out.println("An error occurred while attempting to read in the install script.");
                if ((args.length > 0) && args[0].equals("-d")) { System.out.print(e); }
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
