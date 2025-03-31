package middleware.protos;

import middleware.rudp.Servicer;


public class FacilityBookingClientServicer implements Servicer {
    public static final int BUFFER_SIZE = 1024;
    private final FacilityBookingClient service;

    public FacilityBookingClientServicer(FacilityBookingClient service) {
        this.service = service;
    }

    public int _dispatch(byte[] message, byte[] response) throws Exception {
        int[] i = new int[]{0};
        int method_id = Unmarshaller.unmarshall_int(message, i);

        switch (method_id) {
			case 1:
				Response terminate__result = service.terminate();
				Marshaller.marshall_Response(response, i, terminate__result);
				return i[0];
			case 2:
				int availability__2__arg__len = Unmarshaller.unmarshall_int(message, i);
				Interval[] availability__2__arg = new Interval[availability__2__arg__len];
				for (int j=0; j<availability__2__arg__len; j++)
					availability__2__arg[j] = Unmarshaller.unmarshall_Interval(message, i);
				Response publish__result = service.publish(availability__2__arg);
				Marshaller.marshall_Response(response, i, publish__result);
				return i[0];
			default:
				throw new Exception("Unexpected method ID: " + method_id);
        }
    }
    
    @Override
    public byte[] callback(byte[] data) throws Exception {
        byte[] buffer = new byte[BUFFER_SIZE];
        int response_len = _dispatch(data, buffer);
        byte[] response = new byte[response_len];
        System.arraycopy(buffer, response_len, response, 0, response_len);
        return response;
    }
}
