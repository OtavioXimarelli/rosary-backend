package dev.ximarelli.rosary.backend.checkins;

import dev.ximarelli.rosary.backend.checkins.CheckIn;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckInRepository {
    CheckIn save(CheckIn checkIn);
    Optional<CheckIn> findById(String id);
    Optional<CheckIn> findByUserAndDay(String userId, LocalDate day);
    List<CheckIn> findByUser(String userId);
    List<CheckIn> findPublic();
    void deleteById(String id);
}
