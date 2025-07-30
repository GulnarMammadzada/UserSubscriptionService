package com.example.usersubscriptionservice.repository;


import com.example.usersubscriptionservice.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    List<UserSubscription> findByUsernameAndIsActive(String username, Boolean isActive);

    List<UserSubscription> findByUsername(String username);

    Optional<UserSubscription> findByIdAndUsername(Long id, String username);

    boolean existsByUsernameAndSubscriptionIdAndIsActive(String username, Long subscriptionId, Boolean isActive);

    @Query("SELECT SUM(us.monthlyPrice) FROM UserSubscription us WHERE us.username = :username AND us.isActive = true")
    BigDecimal calculateTotalMonthlyCostByUsername(@Param("username") String username);

    @Query("SELECT COUNT(us) FROM UserSubscription us WHERE us.username = :username AND us.isActive = true")
    int countActiveSubscriptionsByUsername(@Param("username") String username);

    @Query("SELECT us FROM UserSubscription us WHERE us.nextBillingDate = :date AND us.isActive = true")
    List<UserSubscription> findByNextBillingDateAndIsActive(@Param("date") LocalDate date);

    @Query("SELECT us FROM UserSubscription us WHERE us.nextBillingDate BETWEEN :startDate AND :endDate AND us.isActive = true")
    List<UserSubscription> findByNextBillingDateBetweenAndIsActive(@Param("startDate") LocalDate startDate,
                                                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT us FROM UserSubscription us WHERE us.username = :username AND us.subscriptionId = :subscriptionId AND us.isActive = true")
    Optional<UserSubscription> findActiveByUsernameAndSubscriptionId(@Param("username") String username,
                                                                     @Param("subscriptionId") Long subscriptionId);
}
