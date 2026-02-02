package com.amadeus.domain.repository;

import com.amadeus.domain.model.Streak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreakRepository extends JpaRepository<Streak, String> {

    Optional<Streak> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
