package dev.ximarelli.rosary.backend.users.api;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 2, max = 100) String name,
        @Size(max = 300) String bio,
        String avatarUrl) {
}
