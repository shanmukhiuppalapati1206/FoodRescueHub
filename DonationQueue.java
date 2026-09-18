import java.util.LinkedList;
import java.util.Queue;

public class DonationQueue {
    
    // Moved to class level so other methods can use it
    private Queue<String> foodRequests = new LinkedList<>();

    public void addRequest(String request) {
        foodRequests.add(request); // Based on Case 1 logic
    }

    public boolean hasRequests() {
        return !foodRequests.isEmpty(); // Based on Case 2/3 logic
    }

    public String processNextRequest() {
        return foodRequests.poll(); // Based on Case 2 logic
    }
}