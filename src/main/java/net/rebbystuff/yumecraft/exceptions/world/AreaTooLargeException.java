package net.rebbystuff.yumecraft.exceptions.world;

public class AreaTooLargeException extends RuntimeException {

    private static final String BEGINNING_SENTENCE = "Area of size ";

    private static final String ENDING_SENTENCE = " too large for ";

    public AreaTooLargeException(int actual, int expected) {
        super(BEGINNING_SENTENCE + actual + ENDING_SENTENCE + expected);
    }

    public AreaTooLargeException() {
        super("Area too large");
    }
}
