package com.foodrescue.hub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodDonationRepository extends JpaRepository<FoodDonation, String> {
    List<FoodDonation> findByStatus(String status);
    List<FoodDonation> findByDonorId(String donorId);
}