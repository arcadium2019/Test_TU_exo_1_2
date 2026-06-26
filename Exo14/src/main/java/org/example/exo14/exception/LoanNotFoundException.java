package org.example.exo14.exception;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(String message) { super(message); }
}
