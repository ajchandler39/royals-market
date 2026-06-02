package com.royalsmarket.service;

/** Thrown for user-facing business-rule violations (shown as a flash message). */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
