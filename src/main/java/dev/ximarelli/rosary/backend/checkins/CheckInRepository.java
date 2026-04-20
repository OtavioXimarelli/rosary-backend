package dev.ximarelli.rosary.backend.checkins;

import dev.ximarelli.rosary.backend.checkins.CheckIn;
import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface CheckInRepository extends MongoRepository<CheckIn, String> {
   List<CheckIn> findByUserIdAndCreatedAtBetween(String userId, Instant start, Instant end);
   List<CheckIn> findByUserIdOrderByCreatedAtDesc(String userId);
   List<CheckIn> findByIsPublicTrueOrderByCreatedAtDesc();
} 
