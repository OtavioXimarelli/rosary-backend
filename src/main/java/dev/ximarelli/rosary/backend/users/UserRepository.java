package dev.ximarelli.rosary.backend.users;

import dev.ximarelli.rosary.backend.users.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    User save(User user);
}
