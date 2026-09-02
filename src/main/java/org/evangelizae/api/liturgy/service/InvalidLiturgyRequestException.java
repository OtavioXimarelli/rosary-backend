package org.evangelizae.api.liturgy.service;

public class InvalidLiturgyRequestException extends RuntimeException {
    public InvalidLiturgyRequestException(String message) { super(message); }
    public InvalidLiturgyRequestException(String message, Throwable cause) { super(message, cause); }
}
