import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) {

        // Scanner lets our program read what the user types on the keyboard
        Scanner scanner = new Scanner(System.in);

        boolean running = true; // this stays true until the user chooses to Exit

        while (running) {

            // Display the menu
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Register");
            System.out.println("2. Add Food");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt(); // reads the number the user types

            if (choice == 1) {
                System.out.println("You selected: Register");
            } else if (choice == 2) {
                System.out.println("You selected: Add Food");
            } else if (choice == 3) {
                System.out.println("Exiting program... Goodbye!");
                running = false; // this stops the while loop
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }

        scanner.close(); // closes the Scanner when we're done using it
    }
}