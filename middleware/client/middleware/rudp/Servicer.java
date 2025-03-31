package middleware.rudp;


public interface Servicer {

    /**
     * Provide a callback to serve RPCs. Byte array is returned 
     * as response so the caller can send it back to the client
     * as response if needed.
     * 
     * @param data Received message as Bytes
     * @return Response as Bytes
     * @throws Exception
     */
    public Bytes callback(Bytes data) throws Exception;
}
