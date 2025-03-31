package middleware\network;

/**
 * Servicer defines a callback interface for RPC services.
 * 
 * Servicer objects are to be passed to Protocols to
 * handle raw bytestreams via callback mechanism. The 
 * bytestreams are then unmarshalled and dispatched to the
 * corresponding methods of the concrete Servicer.
 * 
 * Byte responses are returned by the Servicer callback,
 * which will be handled by client stubs that make the RPCs.
 */
public interface Servicer {

    /**
     * Provide a callback to serve RPCs. Byte array is returned 
     * as response so the caller can send it back to the client
     * as response if needed.
     * 
     * @param data Received message as Bytes
     * @param buffer_size Max buffer size in bytes for containing 
     *    the response. This value is given by the
     *    underlying protocol.
     * @return Response as Bytes
     * @throws Exception
     */
    Bytes callback(Bytes data, int buffer_size) throws Exception;
}
