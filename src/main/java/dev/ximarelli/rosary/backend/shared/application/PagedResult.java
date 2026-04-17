package dev.ximarelli.rosary.backend.shared.application;

import java.util.List;

public record PagedResult<T>(
        int page,
        int limit,
        long total,
        List<T> items) {
}
