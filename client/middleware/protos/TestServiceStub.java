package middleware\protos;

import java.net.InetAddress;
import middleware.network.Protocol;
import middleware.network.Bytes;


public class TestServiceStub {
	private final InetAddress server_addr;
	private final int server_port;
	private final Protocol proto;
	
	public TestServiceStub(InetAddress server_addr, int server_port, Protocol proto) {
		this.server_addr = server_addr;
		this.server_port = server_port;
		this.proto = proto;
	}

	public Bytes _send(Bytes data) {
		return proto.send(server_addr, server_port, data);
	}

	public DayTime[] generate_noon_daytimes(Day[] days) {
		int[] i = {0};
		byte[] request_data = new byte[proto.get_buffer_size()];
		Marshaller.marshall_int(request_data, i, 9);
		Marshaller.marshall_len_header(request_data, i, days.length);
		for (Day days__arg : days)
			Marshaller.marshall_Day(request_data, i, days__arg);
		byte[] response_data = _send(new Bytes(request_data, i[0])).bytes();
		i[0] = 0;
		Unmarshaller.unmarshall_int(response_data, i);  // strip method_id
		int response__seq__len = Unmarshaller.unmarshall_int(response_data, i);
		DayTime[] response__seq = new DayTime[response__seq__len];
		for (int j=0; j<response__seq__len; j++)
			response__seq[j] = Unmarshaller.unmarshall_DayTime(response_data, i);
		return response__seq;
	}

}
