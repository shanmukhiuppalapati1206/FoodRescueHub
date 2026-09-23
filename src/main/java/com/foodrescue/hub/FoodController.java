package com.foodrescue.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class FoodController {

    @Autowired
    private FoodDonationRepository donationRepository;

    @Autowired
    private UserDirectory userDirectory;

    @PostMapping("/register")
    public String registerUser(@RequestParam String id, @RequestParam String name, @RequestParam String phone, @RequestParam String location, @RequestParam String password) {
        double[] coords = getCoordinatesFromNominatim(location);
        userDirectory.addUser(id, name, phone, location, coords[0], coords[1], password);
        return "Success: Account " + name + " registered successfully!";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String id, @RequestParam String password) {
        UserDirectory.UserDetails user = userDirectory.findUser(id);
        if (user == null) return "ERROR: User ID not found.";
        
        String hashedInputPassword = userDirectory.hashPassword(password);
        if (user.checkPassword(hashedInputPassword)) {
            return "SUCCESS";
        }
        return "ERROR: Incorrect password.";
    }

    @GetMapping("/user-profile")
    public UserDirectory.UserDetails getUserProfile(@RequestParam String id) {
        return userDirectory.findUser(id);
    }

    // New endpoint to inspect NGO and calculate distance from a specific donation/donor
    @GetMapping("/inspect-ngo")
    public NgoInspectDTO inspectNgo(@RequestParam String ngoId, @RequestParam String donorId) {
        UserDirectory.UserDetails ngo = userDirectory.findUser(ngoId);
        UserDirectory.UserDetails donor = userDirectory.findUser(donorId);

        if (ngo == null) return null;

        double distance = 0.0;
        if (donor != null) {
            distance = calculateDistance(donor.getLatitude(), donor.getLongitude(), ngo.getLatitude(), ngo.getLongitude());
        }

        return new NgoInspectDTO(ngo.getId(), ngo.getName(), ngo.getPhone(), ngo.getLocation(), Math.round(distance * 10.0) / 10.0);
    }

    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam String id, @RequestParam String name, @RequestParam String phone, @RequestParam String location) {
        UserDirectory.UserDetails user = userDirectory.findUser(id);
        if (user != null) {
            double[] coords = getCoordinatesFromNominatim(location);
            userDirectory.addUser(id, name, phone, location, coords[0], coords[1], "password123"); 
            return "SUCCESS: Profile updated successfully!";
        }
        return "ERROR: User not found.";
    }

    @PostMapping("/add-donation")
    public String addDonation(@RequestParam String donorId, @RequestParam String foodItem, @RequestParam int quantity, @RequestParam int expiryValue, @RequestParam String expiryUnit, @RequestParam String dietaryType) {
        UserDirectory.UserDetails donor = userDirectory.findUser(donorId);
        String donorName = (donor != null) ? donor.getName() : donorId;
        String donorLocation = (donor != null) ? donor.getLocation() : "Bhimavaram";
        double lat = (donor != null) ? donor.getLatitude() : 16.5448;
        double lon = (donor != null) ? donor.getLongitude() : 81.5212;

        String donationId = "FR" + (1000 + (int)(Math.random() * 9000));
        FoodDonation donation = new FoodDonation(donationId, donorId, donorName, foodItem, quantity, expiryValue, expiryUnit, dietaryType, lat, lon, donorLocation);
        donation.setTrackingStage("AVAILABLE");
        donationRepository.save(donation);

        return "Success: Surplus donation listed successfully in the marketplace!";
    }

    @GetMapping("/available-donations")
    public List<DonationWithDistanceDTO> getAvailableDonations(@RequestParam String ngoId) {
        UserDirectory.UserDetails ngo = userDirectory.findUser(ngoId);
        double ngoLat = (ngo != null) ? ngo.getLatitude() : 16.5062;
        double ngoLon = (ngo != null) ? ngo.getLongitude() : 80.6480;

        List<FoodDonation> list = donationRepository.findAll().stream()
                .filter(d -> "AVAILABLE".equals(d.getStatus()) || "PENDING".equals(d.getStatus()))
                .filter(d -> !d.getRequestedByNgoIds().contains(ngoId) && !d.getRejectedNgoIds().contains(ngoId))
                .toList();

        List<FoodDonation> mutableList = new ArrayList<>(list);
        mutableList.sort(null);

        return mutableList.stream().map(d -> {
            double distance = calculateDistance(ngoLat, ngoLon, d.getDonorLatitude(), d.getDonorLongitude());
            return new DonationWithDistanceDTO(d, Math.round(distance * 10.0) / 10.0);
        }).toList();
    }

    @PostMapping("/request-donation")
    public String requestDonation(@RequestParam String donationId, @RequestParam String ngoId) {
        Optional<FoodDonation> opt = donationRepository.findById(donationId);
        if (opt.isPresent()) {
            FoodDonation donation = opt.get();
            if (!donation.getRequestedByNgoIds().contains(ngoId)) {
                donation.getRequestedByNgoIds().add(ngoId);
                donation.getRejectedNgoIds().remove(ngoId);
                donation.setStatus("PENDING");
                donation.setTrackingStage("REQUESTED");
                donationRepository.save(donation);
                return "SUCCESS: Request sent to donor!";
            }
            return "ERROR: You already requested this item.";
        }
        return "ERROR: Item not found.";
    }

    @GetMapping("/donor-requests")
    public List<FoodDonation> getDonorRequests(@RequestParam String donorId) {
        return donationRepository.findByDonorId(donorId);
    }

    @GetMapping("/donor-impact")
    public DonorImpactDTO getDonorImpact(@RequestParam String donorId) {
        List<FoodDonation> donations = donationRepository.findByDonorId(donorId);
        int totalKg = donations.stream().mapToInt(FoodDonation::getQuantity).sum();
        int totalDeliveredKg = donations.stream().filter(d -> "DELIVERED".equals(d.getStatus())).mapToInt(FoodDonation::getQuantity).sum();
        int totalCount = donations.size();
        int completedCount = (int) donations.stream().filter(d -> "DELIVERED".equals(d.getStatus())).count();
        int estimatedMeals = (int) Math.round(totalKg * 2.5);
        double co2PreventedKg = Math.round(totalKg * 2.2 * 10.0) / 10.0;

        String tierBadge = "Bronze Rescuer";
        if (totalKg >= 100) tierBadge = "Platinum Hero 🏆";
        else if (totalKg >= 50) tierBadge = "Gold Champion 🥇";
        else if (totalKg >= 25) tierBadge = "Silver Supporter 🥈";

        return new DonorImpactDTO(totalKg, totalDeliveredKg, estimatedMeals, co2PreventedKg, totalCount, completedCount, tierBadge);
    }

    @PostMapping("/respond-request")
    public String respondRequest(@RequestParam String donationId, @RequestParam String ngoId, @RequestParam boolean accept) {
        Optional<FoodDonation> opt = donationRepository.findById(donationId);
        if (opt.isPresent()) {
            FoodDonation donation = opt.get();
            if (accept) {
                for (String reqNgo : donation.getRequestedByNgoIds()) {
                    if (!reqNgo.equals(ngoId) && !donation.getRejectedNgoIds().contains(reqNgo)) {
                        donation.getRejectedNgoIds().add(reqNgo);
                    }
                }
                donation.setStatus("ACCEPTED");
                donation.setAcceptedNgoId(ngoId);
                donation.setTrackingStage("ACCEPTED");
                donation.getRequestedByNgoIds().clear();
            } else {
                donation.getRequestedByNgoIds().remove(ngoId);
                if (!donation.getRejectedNgoIds().contains(ngoId)) {
                    donation.getRejectedNgoIds().add(ngoId);
                }
                if (donation.getRequestedByNgoIds().isEmpty()) {
                    donation.setStatus("AVAILABLE");
                    donation.setTrackingStage("AVAILABLE");
                }
            }
            donationRepository.save(donation);
            return "SUCCESS: Response recorded!";
        }
        return "ERROR: Item not found.";
    }

    @PostMapping("/advance-tracking")
    public String advanceTracking(@RequestParam String donationId, @RequestParam String nextStage) {
        Optional<FoodDonation> opt = donationRepository.findById(donationId);
        if (opt.isPresent()) {
            FoodDonation donation = opt.get();
            donation.setTrackingStage(nextStage);
            if (nextStage.equals("DELIVERED")) {
                donation.setStatus("DELIVERED");
            }
            donationRepository.save(donation);
            return "SUCCESS: Tracking stage updated to " + nextStage;
        }
        return "ERROR: Donation not found.";
    }

    @GetMapping("/ngo-status")
    public List<FoodDonation> getNgoStatus(@RequestParam String ngoId) {
        return donationRepository.findAll().stream()
                .filter(d -> d.getRequestedByNgoIds().contains(ngoId) || 
                             ngoId.equals(d.getAcceptedNgoId()) || 
                             d.getRejectedNgoIds().contains(ngoId))
                .toList();
    }

    @GetMapping("/admin-stats")
    public AdminStatsDTO getAdminStats() {
        List<FoodDonation> all = donationRepository.findAll();
        
        int totalDonations = all.size();
        int totalKgRescued = all.stream().filter(d -> "DELIVERED".equals(d.getStatus())).mapToInt(FoodDonation::getQuantity).sum();
        int availableCount = (int) all.stream().filter(d -> "AVAILABLE".equals(d.getStatus())).count();
        int deliveredCount = (int) all.stream().filter(d -> "DELIVERED".equals(d.getStatus())).count();
        int pendingRequests = (int) all.stream().filter(d -> "PENDING".equals(d.getStatus())).count();

        return new AdminStatsDTO(totalDonations, totalKgRescued, 32, pendingRequests, deliveredCount, 8, availableCount, 0, 0, deliveredCount);
    }

    private double[] getCoordinatesFromNominatim(String location) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?q=" + location.replace(" ", "+") + "&format=json&limit=1";
            RestTemplate restTemplate = new RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "FoodRescueHubApp/1.0");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<List> response = restTemplate.exchange(
                url, org.springframework.http.HttpMethod.GET, entity, List.class
            );

            List<Map<String, Object>> results = response.getBody();
            if (results != null && !results.isEmpty()) {
                Map<String, Object> firstResult = results.get(0);
                double lat = Double.parseDouble(firstResult.get("lat").toString());
                double lon = Double.parseDouble(firstResult.get("lon").toString());
                return new double[]{lat, lon};
            }
        } catch (Exception ignored) {}
        return new double[]{16.5448, 81.5212};
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; 
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; 
    }

    public static class NgoInspectDTO {
        public String id;
        public String name;
        public String phone;
        public String location;
        public double distanceKm;

        public NgoInspectDTO(String id, String name, String phone, String location, double distanceKm) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.location = location;
            this.distanceKm = distanceKm;
        }
    }

    public static class DonationWithDistanceDTO {
        public FoodDonation donation;
        public double distanceKm;

        public DonationWithDistanceDTO(FoodDonation donation, double distanceKm) {
            this.donation = donation;
            this.distanceKm = distanceKm;
        }
    }

    public static class DonorImpactDTO {
        public int totalKg;
        public int deliveredKg;
        public int estimatedMeals;
        public double co2PreventedKg;
        public int totalDonations;
        public int completedDonations;
        public String tierBadge;

        public DonorImpactDTO(int totalKg, int deliveredKg, int estimatedMeals, double co2PreventedKg, int totalDonations, int completedDonations, String tierBadge) {
            this.totalKg = totalKg;
            this.deliveredKg = deliveredKg;
            this.estimatedMeals = estimatedMeals;
            this.co2PreventedKg = co2PreventedKg;
            this.totalDonations = totalDonations;
            this.completedDonations = completedDonations;
            this.tierBadge = tierBadge;
        }
    }

    public static class AdminStatsDTO {
        public int totalDonations;
        public int foodRescuedKg;
        public int activeNgos;
        public int pendingRequests;
        public int successfulDeliveries;
        public int expiredDonations;
        public int statusAvailable;
        public int statusMatched;
        public int statusPickedUp;
        public int statusDelivered;

        public AdminStatsDTO(int totalDonations, int foodRescuedKg, int activeNgos, int pendingRequests, int successfulDeliveries, int expiredDonations, int statusAvailable, int statusMatched, int statusPickedUp, int statusDelivered) {
            this.totalDonations = totalDonations;
            this.foodRescuedKg = foodRescuedKg;
            this.activeNgos = activeNgos;
            this.pendingRequests = pendingRequests;
            this.successfulDeliveries = successfulDeliveries;
            this.expiredDonations = expiredDonations;
            this.statusAvailable = statusAvailable;
            this.statusMatched = statusMatched;
            this.statusPickedUp = statusPickedUp;
            this.statusDelivered = statusDelivered;
        }
    }
}