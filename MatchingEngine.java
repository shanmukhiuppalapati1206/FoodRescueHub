import java.util.PriorityQueue;

public class MatchingEngine {
    private PriorityQueue<FoodDonation> donationHeap = new PriorityQueue<>();

    public void addDonation(FoodDonation donation) {
        donationHeap.add(donation);
    }

    public FoodDonation getUrgentDonation() {
        return donationHeap.poll();
    }

    public boolean hasDonations() {
        return !donationHeap.isEmpty();
    }

    public static void main(String[] args) {
        MatchingEngine engine = new MatchingEngine();

        // Sample test cases: 8 hrs vs 2 hrs expiry
        engine.addDonation(new FoodDonation("D1", "City Hotel", "Rice", 30, 8));
        engine.addDonation(new FoodDonation("D2", "Bake House", "Bread", 15, 2));

        FoodDonation urgent = engine.getUrgentDonation();
        System.out.println("Urgent item dispatched: " + urgent.foodItem + " (Expires in " + urgent.expiryHours + " hrs)");

        if ("Bread".equals(urgent.foodItem)) {
            System.out.println(">> TEST PASSED: Min-Heap working correctly.");
        }
    }
}