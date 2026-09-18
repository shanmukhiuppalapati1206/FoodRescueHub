import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Instances of your team's core data structure modules
        UserDirectory userDirectory = new UserDirectory();
        DonationQueue donationQueue = new DonationQueue();
        MatchingEngine matchingEngine = new MatchingEngine();

        boolean running = true;

        while (running) {
            System.out.println("\n===== FOOD RESCUE HUB =====");
            System.out.println("1. Register User/NGO");
            System.out.println("2. View Registered Users");
            System.out.println("3. Request Food (NGO Queue)");
            System.out.println("4. Add Food Donation (Donor)");
            System.out.println("5. Match & Dispatch Urgent Food");
            System.out.println("6. Exit");
            System.out.print("Enter your choice (1-6): ");

            if (!scanner.hasNextInt()) {
                System.out.println("[!] Invalid input. Please enter a valid number.");
                scanner.nextLine(); 
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            if (choice == 1) {
                // Uses Bindhu's addUser method fields (ID, Name, Phone, Location)
                System.out.print("Enter User ID (e.g. U101): ");
                String id = scanner.nextLine();
                System.out.print("Enter Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter Phone: ");
                String phone = scanner.nextLine();
                System.out.print("Enter Location: ");
                String location = scanner.nextLine();
                
                userDirectory.addUser(id, name, phone, location);

            } else if (choice == 2) {
                // Uses Bindhu's display method
                userDirectory.displayUsers();

            } else if (choice == 3) {
                System.out.print("Enter NGO ID: ");
                String ngoId = scanner.nextLine();
                
                // Uses Bindhu's findUser method
                Object user = userDirectory.findUser(ngoId);
                
                if (user == null) {
                    System.out.println("[!] User ID not found. Register first.");
                } else {
                    System.out.print("Enter required food details (e.g. Rice 20kg): ");
                    String need = scanner.nextLine();
                    
                    // Uses Hansika's restructured addRequest method
                    donationQueue.addRequest("User ID " + ngoId + " needs " + need);
                    System.out.println("✓ Request added to FIFO Queue.");
                }

            } else if (choice == 4) {
                // Uses your Min-Heap MatchingEngine module
                System.out.print("Enter Donation ID (e.g. D101): ");
                String dId = scanner.nextLine();
                System.out.print("Enter Donor Name: ");
                String donor = scanner.nextLine();
                System.out.print("Enter Food Item: ");
                String item = scanner.nextLine();
                System.out.print("Enter Quantity (kg): ");
                int qty = scanner.nextInt();
                System.out.print("Enter Expiry in Hours (Min-Heap Priority): ");
                int expiry = scanner.nextInt();
                scanner.nextLine();

                matchingEngine.addDonation(new FoodDonation(dId, donor, item, qty, expiry));
                System.out.println("✓ Donation added to PriorityQueue.");

            } else if (choice == 5) {
                // Integration: Matches your MatchingEngine with Hansika's Queue
                if (!matchingEngine.hasDonations()) {
                    System.out.println("[!] No donations currently available.");
                } else if (!donationQueue.hasRequests()) {
                    System.out.println("[!] No pending requests in the NGO queue.");
                } else {
                    FoodDonation urgentFood = matchingEngine.getUrgentDonation();
                    String request = donationQueue.processNextRequest();
                    
                    System.out.println("\n>>> SUCCESSFUL RESCUE MATCH <<<");
                    System.out.println("Dispatched Food : " + urgentFood.foodItem + " (" + urgentFood.quantityKg + "kg)");
                    System.out.println("Expiry Time     : In " + urgentFood.expiryHours + " hours (URGENT)");
                    System.out.println("Allocated To    : " + request);
                }

            } else if (choice == 6) {
                System.out.println("Exiting program... Goodbye!");
                running = false;

            } else {
                System.out.println("[!] Invalid choice. Please enter a number between 1 and 6.");
            }
        }
        scanner.close();
    }
}