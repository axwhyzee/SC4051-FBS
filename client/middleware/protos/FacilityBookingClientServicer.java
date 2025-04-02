package middleware\protos;

import middleware.network.Servicer;
import middleware.network.Bytes;


public class FacilityBookingClientServicer implements Servicer {
    private final FacilityBookingClient service;

    public FacilityBookingClientServicer(FacilityBookingClient service) {
        this.service = service;
    }

    public int _dispatch(byte[] message, byte[] response) throws Exception {
        int[] i = new int[]{0};
        int method_id = Unmarshaller.unmarshall_int(message, i);

        switch (method_id) {
			case 7:
				Response terminate__result = service.terminate();
				i[0] = 0;
				Marshaller.marshall_int(response, i, 7);
				Marshaller.marshall_Response(response, i, terminate__result);
				return i[0];
			case 8:
				int availability__8__arg__len = Unmarshaller.unmarshall_int(message, i);
				Interval[] availability__8__arg = new Interval[availability__8__arg__len];
				for (int j=0; j<availability__8__arg__len; j++)
					availability__8__arg[j] = Unmarshaller.unmarshall_Interval(message, i);
				Response publish__result = service.publish(availability__8__arg);
				i[0] = 0;
				Marshaller.marshall_int(response, i, 8);
				Marshaller.marshall_Response(response, i, publish__result);
				return i[0];
			default:
				throw new Exception("Unexpected method ID: " + method_id);
        }
    }
    
    @Override
    public Bytes callback(Bytes data, int buffer_size) throws Exception {
        byte[] response = new byte[buffer_size];
        int response_len = _dispatch(data.bytes(), response);
        return new Bytes(response, response_len);
    }
}
