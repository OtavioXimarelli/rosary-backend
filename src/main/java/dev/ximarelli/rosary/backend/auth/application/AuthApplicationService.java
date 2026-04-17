package dev.ximarelli.rosary.backend.auth.application;

import dev.ximarelli.rosary.backend.auth.domain.TokenIssuer;
import dev.ximarelli.rosary.backend.users.application.UserApplicationService;
import dev.ximarelli.rosary.backend.users.domain.User;
import dev.ximarelli.rosary.backend.users.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final UserApplicationService userService;
    private final TokenIssuer tokenIssuer;

    public AuthApplicationService(UserRepository userRepository, UserApplicationService userService, TokenIssuer tokenIssuer) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.tokenIssuer = tokenIssuer;
    }

    public AuthResult register(String name, String email, String password) {
        userRepository.findByEmail(email).ifPresent(existing -> {
            throw new IllegalArgumentException("Email already in use");
        });

        User user = userRepository.save(new User(
                null,
                name,
                email,
                encode(password),
                null,
                null,
                0,
                0,
                0,
                null,
                Instant.now()));

        return new AuthResult(tokenIssuer.issue(user.id()), userService.toSummary(user));
    }

    public AuthResult login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!user.passwordHash().equals(encode(password))) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return new AuthResult(tokenIssuer.issue(user.id()), userService.toSummary(user));
    }

    private String encode(String password) {
        return "noop:" + password;
    }
}
