package dev.ximarelli.rosary.backend.users;

import dev.ximarelli.rosary.backend.users.UserApplicationService;
import dev.ximarelli.rosary.backend.users.UserProfile;
import dev.ximarelli.rosary.backend.users.UserStats;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    private final UserApplicationService userService;

    public UsersController(UserApplicationService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/me")
    public UserProfile getMe(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        return userService.getProfile(resolveUser(userId));
    }

    @GetMapping("/users/me/stats")
    public UserStats getMyStats(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        return userService.getStats(resolveUser(userId));
    }

    @PutMapping("/users/me")
    public UserProfile updateMe(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateProfile(resolveUser(userId), request.name(), request.avatarUrl(), request.bio());
    }

    @GetMapping("/users/{id}")
    public UserProfile getById(@PathVariable String id) {
        return userService.getById(id);
    }

    private String resolveUser(String userId) {
        return userId == null || userId.isBlank() ? "demo-user" : userId;
    }
}
