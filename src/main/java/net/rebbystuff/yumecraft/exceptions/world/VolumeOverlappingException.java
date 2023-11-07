package net.rebbystuff.yumecraft.exceptions.world;

public class VolumeOverlappingException extends RuntimeException {
    public VolumeOverlappingException(String message) {
        super(message);
    }

    public VolumeOverlappingException() {
        this("Two volumes cannot overlap in this instance");
    }
}
