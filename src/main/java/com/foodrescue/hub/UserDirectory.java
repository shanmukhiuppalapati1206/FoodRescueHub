package com.foodrescue.hub;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

interface UserRepository extends JpaRepository<UserDirectory.UserDetails, String> {}

@Service
public class UserDirectory {
    private final UserRepository userRepository;

    public UserDirectory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(String id, String name, String phone, String location, double latitude, double longitude, String password) {
        String hashedPassword = hashPassword(password);
        UserDetails newUser = new UserDetails(id, name, phone, location, latitude, longitude, hashedPassword);
        userRepository.save(newUser);
    }

    public UserDetails findUser(String id) {
        Optional<UserDetails> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    @Entity
    public static class UserDetails {
        @Id
        private String id;
        private String name;
        private String phone;
        private String location;
        private double latitude;
        private double longitude;
        private String passwordHash;

        public UserDetails() {}

        public UserDetails(String id, String name, String phone, String location, double latitude, double longitude, String passwordHash) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.location = location;
            this.latitude = latitude;
            this.longitude = longitude;
            this.passwordHash = passwordHash;
        }

        public boolean checkPassword(String inputPasswordHash) {
            return this.passwordHash.equals(inputPasswordHash);
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getLocation() { return location; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }
}