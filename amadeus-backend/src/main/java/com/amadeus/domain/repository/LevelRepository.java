package com.amadeus.domain.repository;

import com.amadeus.domain.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<Level, String> {

    Optional<Level> findByOrderNumber(Integer orderNumber);

    List<Level> findAllByOrderByOrderNumberAsc();

    boolean existsByOrderNumber(Integer orderNumber);
}
