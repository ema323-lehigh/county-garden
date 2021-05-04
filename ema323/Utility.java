import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class Utility {
    public static int inputRequest(String[] choices, Scanner input) {
        int choice;
        for (int i = 0; i < choices.length; i++) {
            System.out.println((i + 1) + ") " + choices[i]);
        }
        System.out.println((choices.length + 1) + ") quit");
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if (choice == (choices.length + 1)) {
                    System.out.println("Bye now!");
                    input.close();
                    System.exit(0);
                }
                else if ((choice > 0) && (choice <= choices.length)) {
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
        int choice;
        for (int i = 0; i < choices.length; i++) {
            if (choices[i][0] != null) { System.out.println((i + 1) + ") " + choices[i][1] + " (" + choices[i][0] + ")"); }
        }
        System.out.println((choices.length + 1) + ") quit");
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if (choice == choices.length + 1) {
                    System.out.println("Bye now!");
                    input.close();
                    System.exit(0);
                }
                else if ((choice > 0) && (choice <= choices.length)) {
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
        int choice;
        for (int i = 0; i < choices.length; i++) {
            if (choices[i][0] != null) { System.out.println((i + 1) + ") " + choices[i][1]); }
        }
        System.out.println((choices.length + 1) + ") quit");
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if (choice == choices.length + 1) {
                    System.out.println("Bye now!");
                    input.close();
                    System.exit(0);
                }
                else if ((choice > 0) && (choice <= choices.length)) {
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
        int choice;
        for (int i = 0; i < choices.length; i++) {
            if (choices[i][0] != null) { System.out.println((i + 1) + ") " + choices[i][1] + " (" + choices[i][0] + ") - " + choices[i][2]); }
        }
        System.out.println((choices.length + 1) + ") quit");
        while (true) {
            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
                if (choice == choices.length + 1) {
                    System.out.println("Bye now!");
                    input.close();
                    System.exit(0);
                }
                else if ((choice > 0) && (choice <= choices.length)) {
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
}
