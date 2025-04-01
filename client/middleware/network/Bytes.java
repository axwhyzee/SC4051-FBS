package middleware.network;

/**
 * Simple dataclass to store byte array and length in a single object
 */
public record Bytes (
    byte[] bytes,
    int length
) {};
