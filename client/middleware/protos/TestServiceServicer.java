package middleware.protos;

import middleware.network.Servicer;
import middleware.network.Bytes;


public class TestServiceServicer implements Servicer {
    private final TestService service;

    public TestServiceServicer(TestService service) {
        this.service = service;
    }

    public int _dispatch(byte[] message, byte[] response) throws Exception {
        int[] i = new int[]{0};
        int method_id = Unmarshaller.unmarshall_int(message, i);

        switch (method_id) {
			case 9:
				int days__9__arg__len = Unmarshaller.unmarshall_int(message, i);
				Day[] days__9__arg = new Day[days__9__arg__len];
				for (int j=0; j<days__9__arg__len; j++)
					days__9__arg[j] = Unmarshaller.unmarshall_Day(message, i);
				DayTime[] generate_noon_daytimes__result = service.generate_noon_daytimes(days__9__arg);
				i[0] = 0;
				Marshaller.marshall_int(response, i, 9);
				Marshaller.marshall_len_header(response, i, generate_noon_daytimes__result.length);
				for (DayTime generate_noon_daytimes__result__item : generate_noon_daytimes__result)
					Marshaller.marshall_DayTime(response, i, generate_noon_daytimes__result__item);
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
