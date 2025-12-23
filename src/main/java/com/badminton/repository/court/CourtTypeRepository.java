package com.badminton.repository.court;

import com.badminton.entity.court.CourtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourtTypeRepository extends JpaRepository<CourtType, Long> {

    Optional<CourtType> findByName(String name);

    List<CourtType> findByIsActiveTrue();

    boolean existsByName(String name);
}
