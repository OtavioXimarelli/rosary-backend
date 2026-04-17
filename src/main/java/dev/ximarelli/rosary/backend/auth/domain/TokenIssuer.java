package dev.ximarelli.rosary.backend.auth.domain;

public interface TokenIssuer {
    String issue(String userId);
}
