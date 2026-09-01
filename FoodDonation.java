public class FoodDonation implements Comparable<FoodDonation> {
    public String donationId;
    public String donorName;
    public String foodItem;
    public int quantityKg;
    public int expiryHours; // Priority metric

    public FoodDonation(String donationId, String donorName, String foodItem, int quantityKg, int expiryHours) {
        this.donationId = donationId;
        this.donorName = donorName;
        this.foodItem = foodItem;
        this.quantityKg = quantityKg;
        this.expiryHours = expiryHours;
    }

    // Min-Heap sorting: lowest expiry hours has highest priority
    @Override
    public int compareTo(FoodDonation other) {
        return Integer.compare(this.expiryHours, other.expiryHours);
    }
}