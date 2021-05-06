import java.sql.*;
import java.io.*;
import java.util.regex.*;
import java.util.Scanner;

public class Utility {
    public static int inputRequest(String[] choices, Scanner input) {
        int choice; int i;
        for (i = 0; i < choices.length; i++) {
            System.out.println((i + 1) + ") " + choices[i]);
        }
        System.out.println((++i) + ") quit");
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if (choice == i) {
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println("Bye now!");
                    System.out.println("--------------------------------------------------------------------------------");
                    input.close();
                    System.exit(0);
                }
                else if ((choice > 0) && (choice < i)) {
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

    public static int inputRequestByID(String[][] choices, Scanner input) {
        int choice; int i;
        for (i = 0; i < choices.length; i++) {
            if (choices[i][0] != null) {
                System.out.println((i + 1) + ") " + choices[i][1] + " (" + choices[i][0] + ")");
            }
            else {
                break;
            }
        }
        System.out.println((++i) + ") quit");
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if (choice == i) {
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println("Bye now!");
                    System.out.println("--------------------------------------------------------------------------------");
                    input.close();
                    System.exit(0);
                }
                else if ((choice > 0) && (choice < i)) {
                    return Integer.parseInt(choices[choice - 1][0]);
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

    public static int inputRequestByMutedID(String[][] choices, Scanner input) {
        int choice; int i;
        for (i = 0; i < choices.length; i++) {
            if (choices[i][0] != null) {
                System.out.println((i + 1) + ") " + choices[i][1]);
            }
            else {
                break;
            }
        }
        System.out.println((++i) + ") quit");
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if (choice == i) {
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println("Bye now!");
                    System.out.println("--------------------------------------------------------------------------------");
                    input.close();
                    System.exit(0);
                }
                else if ((choice > 0) && (choice < i)) {
                    return Integer.parseInt(choices[choice - 1][0]);
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

    public static int inputRequestByIDAttribute(String[][] choices, Scanner input) {
        int choice; int i;
        for (i = 0; i < choices.length; i++) {
            if (choices[i][0] != null) {
                System.out.println((i + 1) + ") " + choices[i][1] + " (" + choices[i][0] + ") - " + choices[i][2]);
            }
            else {
                break;
            }
        }
        System.out.println((++i) + ") quit");
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if (choice == i) {
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println("Bye now!");
                    System.out.println("--------------------------------------------------------------------------------");
                    input.close();
                    System.exit(0);
                }
                else if ((choice > 0) && (choice < i)) {
                    return Integer.parseInt(choices[choice - 1][0]);
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

    public static String inputRequestString(Scanner input, String format) {
        Pattern pattern = Pattern.compile(format);
        while (true) {
            String buffer = input.nextLine();
            if (!pattern.matcher(buffer).find()) {
                System.out.println("Please match the expected format.");
            }
            else {
                return buffer;
            }
        }
    }
}
