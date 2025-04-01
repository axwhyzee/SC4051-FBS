package middleware.protos;

import java.net.InetAddress;
import middleware.network.Protocol;
import middleware.network.Bytes;


public class FacilityBookingClientStub {
	private final InetAddress server_addr;
	private final int server_port;
	private final Protocol proto;
	
	public FacilityBookingClientStub(InetAddress server_addr, int server_port, Protocol proto) {
		this.server_addr = server_addr;
		this.server_port = server_port;
		this.proto = proto;
	}

	public Bytes _send(Bytes data) {
		return proto.send(server_addr, server_port, data);
	}

	public Response terminate() {
		int[] i = {0};
		byte[] request_data = new byte[proto.get_buffer_size()];
		Marshaller.marshall_int(request_data, i, 7);
		byte[] response_data = _send(new Bytes(request_data, i[0])).bytes();
		i[0] = 0;
		Unmarshaller.unmarshall_int(response_data, i);  // strip method_id
		return Unmarshaller.unmarshall_Response(response_data, i);
	}

	public Response publish(Interval[] availability) {
		int[] i = {0};
		byte[] request_data = new byte[proto.get_buffer_size()];
		Marshaller.marshall_int(request_data, i, 8);
		Marshaller.marshall_len_header(request_data, i, availability.length);
		for (Interval availability__arg : availability)
			Marshaller.marshall_Interval(request_data, i, availability__arg);
		byte[] response_data = _send(new Bytes(request_data, i[0])).bytes();
		i[0] = 0;
		Unmarshaller.unmarshall_int(response_data, i);  // strip method_id
		return Unmarshaller.unmarshall_Response(response_data, i);
	}

}
