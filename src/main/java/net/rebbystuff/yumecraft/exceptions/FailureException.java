package net.rebbystuff.yumecraft.exceptions;

public class FailureException extends RuntimeException{

    public FailureException(String message) {
        super(message);
    }

    public FailureException() {
        this("Failure in Task");
    }
}
