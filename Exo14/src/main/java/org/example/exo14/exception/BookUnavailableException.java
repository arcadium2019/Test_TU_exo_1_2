package org.example.exo14.exception;

public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String message) { super(message); }
}
