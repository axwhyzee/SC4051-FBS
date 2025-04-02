import middleware.network.Servicer;
import middleware.network.Bytes;


public class {__SERVICE_NAME__}Servicer implements Servicer {
    private final {__SERVICE_NAME__} service;

    public {__SERVICE_NAME__}Servicer({__SERVICE_NAME__} service) {
        this.service = service;
    }

    public int _dispatch(byte[] message, byte[] response) throws Exception {
        int[] i = new int[]{0};
        int method_id = Unmarshaller.unmarshall_int(message, i);

        switch (method_id) {{__DISPATCH_CODE__}
        }
    }
    
    @Override
    public Bytes callback(Bytes data, int buffer_size) throws Exception {
        byte[] response = new byte[buffer_size];
        int response_len = _dispatch(data.bytes(), response);
        return new Bytes(response, response_len);
    }
}
