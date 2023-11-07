package net.rebbystuff.yumecraft.exceptions.world;

public class UnloadedChunkException extends RuntimeException{

    public UnloadedChunkException(String message) {
        super(message);
    }

    public UnloadedChunkException() {
        this("Chunk not loaded");
    }
}
