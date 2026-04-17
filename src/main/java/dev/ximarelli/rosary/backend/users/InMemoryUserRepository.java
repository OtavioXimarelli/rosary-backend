package dev.ximarelli.rosary.backend.users;

import dev.ximarelli.rosary.backend.users.User;
import dev.ximarelli.rosary.backend.users.UserRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, String> userIdByEmail = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        User seed = new User(
                "demo-user",
                "Demo User",
                "demo@rosary.app",
                "noop:demo123",
                null,
                "Usuário de demonstração",
                3,
                11,
                27,
                Instant.now(),
                Instant.now());
        save(seed);
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String userId = userIdByEmail.get(email.toLowerCase());
        if (userId == null) {
            return Optional.empty();
        }
        return findById(userId);
    }

    @Override
    public User save(User user) {
        String userId = user.id() != null ? user.id() : UUID.randomUUID().toString();
        User persisted = new User(
                userId,
                user.name(),
                user.email(),
                user.passwordHash(),
                user.avatarUrl(),
                user.bio(),
                user.currentStreak(),
                user.longestStreak(),
                user.totalCheckIns(),
                user.lastCheckIn(),
                user.createdAt());
        usersById.put(userId, persisted);
        userIdByEmail.put(persisted.email().toLowerCase(), userId);
        return persisted;
    }
}
