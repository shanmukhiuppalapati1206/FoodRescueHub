import java.util.HashMap;
import java.util.Map;

public class UserDirectory {

    // Key = User/Donor ID, Value = UserDetails
    private final Map<String, UserDetails> users = new HashMap<>();

    // Add a new user/donor
    public void addUser(String id, String name, String phone, String location) {
        if (users.containsKey(id)) {
            System.out.println("User ID already exists: " + id);
            return;
        }

        users.put(id, new UserDetails(id, name, phone, location));
        System.out.println("User added successfully.");
    }

    // Find user/donor quickly using ID
    public UserDetails findUser(String id) {
        return users.get(id);
    }

    // Remove user/donor
    public void removeUser(String id) {
        if (users.remove(id) != null) {
            System.out.println("User removed successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    // Display all users/donors
    public void displayUsers() {
        if (users.isEmpty()) {
            System.out.println("No users available.");
            return;
        }

        for (UserDetails user : users.values()) {
            System.out.println(user);
        }
    }

    public int getUserCount() {
        return users.size();
    }

    // User/Donor details
    static class UserDetails {
        private final String id;
        private final String name;
        private final String phone;
        private final String location;

        public UserDetails(String id, String name, String phone, String location) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.location = location;
        }

        @Override
        public String toString() {
            return "ID: " + id
                    + " | Name: " + name
                    + " | Phone: " + phone
                    + " | Location: " + location;
        }
    }

    // Testing
    public static void main(String[] args) {

        UserDirectory directory = new UserDirectory();

        directory.addUser("D101", "Ravi", "9876543210", "Vijayawada");
        directory.addUser("D102", "Anitha", "9123456780", "Guntur");

        System.out.println("\nSearch by ID:");

        UserDetails user = directory.findUser("D101");

        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("User not found.");
        }

        System.out.println("\nAll Users/Donors:");
        directory.displayUsers();

        System.out.println("\nTotal Users/Donors: "
                + directory.getUserCount());
    }
}