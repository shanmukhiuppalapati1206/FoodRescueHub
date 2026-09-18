import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class DonationQueue {

    public static void main(String[] args) {

        Queue<String> foodRequests = new LinkedList<>();
        Scanner sc = new Scanner(System.in);

        int choice;

        do {
            System.out.println("\n--- Food Donation Queue ---");
            System.out.println("1. Add Food Request");
            System.out.println("2. Serve Food Request");
            System.out.println("3. Display Requests");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter food request: ");
                    String request = sc.nextLine();
                    foodRequests.add(request);
                    System.out.println("Request added successfully.");
                    break;

                case 2:
                    if (foodRequests.isEmpty()) {
                        System.out.println("No food requests in the queue.");
                    } else {
                        String served = foodRequests.poll();
                        System.out.println("Food request served: " + served);
                    }
                    break;

                case 3:
                    if (foodRequests.isEmpty()) {
                        System.out.println("Queue is empty.");
                    } else {
                        System.out.println("Pending Food Requests:");
                        for (String req : foodRequests) {
                            System.out.println(req);
                        }
                    }
                    break;

                case 4:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 4);

        sc.close();
    }
}