package dev.ximarelli.rosary.backend.users;

import dev.ximarelli.rosary.backend.users.UserProfile;
import dev.ximarelli.rosary.backend.users.UserStats;
import dev.ximarelli.rosary.backend.users.UserSummary;
import dev.ximarelli.rosary.backend.users.User;
import dev.ximarelli.rosary.backend.users.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService {

    private final UserRepository userRepository;

    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfile getProfile(String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return toProfile(user);
    }

    public UserProfile getById(String id) {
        User user = userRepository.findById(id).orElseThrow();
        return toProfile(user);
    }

    public UserStats getStats(String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return new UserStats(user.currentStreak(), user.longestStreak(), user.totalCheckIns());
    }

    public UserProfile updateProfile(String userId, String name, String avatarUrl, String bio) {
        User current = userRepository.findById(userId).orElseThrow();
        User updated = current.withProfile(name, avatarUrl, bio);
        return toProfile(userRepository.save(updated));
    }

    public UserSummary toSummary(User user) {
        return new UserSummary(
                user.id(),
                user.name(),
                user.email(),
                user.avatarUrl(),
                user.currentStreak(),
                user.longestStreak(),
                user.totalCheckIns());
    }

    private UserProfile toProfile(User user) {
        return new UserProfile(
                user.id(),
                user.name(),
                user.email(),
                user.avatarUrl(),
                user.bio(),
                user.currentStreak(),
                user.longestStreak(),
                user.totalCheckIns(),
                user.lastCheckIn(),
                user.createdAt());
    }
}
