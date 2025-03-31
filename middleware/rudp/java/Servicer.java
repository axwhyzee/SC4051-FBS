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
     * @return Response as Bytes
     * @throws Exception
     */
    public Bytes callback(Bytes data) throws Exception;
}
