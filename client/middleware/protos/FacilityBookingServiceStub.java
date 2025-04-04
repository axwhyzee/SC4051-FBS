package middleware.protos;

import java.net.InetAddress;
import middleware.network.RUDP;
import middleware.network.Bytes;


public class FacilityBookingServiceStub {
	private final InetAddress server_addr;
	private final int server_port;
	private final RUDP proto;
	
	public FacilityBookingServiceStub(InetAddress server_addr, int server_port, RUDP proto) {
		this.server_addr = server_addr;
		this.server_port = server_port;
		this.proto = proto;
	}

	public Bytes _send(Bytes data) throws Exception {
		return proto.send(server_addr, server_port, data);
	}

	public AvailabilityResponse queryFacility(String facilityName, Day[] days) throws Exception {
		int[] i = {0};
		byte[] request_data = new byte[proto.get_buffer_size()];
		Marshaller.marshall_int(request_data, i, 1);
		Marshaller.marshall_string(request_data, i, facilityName);
		Marshaller.marshall_len_header(request_data, i, days.length);
		for (Day days__arg : days)
			Marshaller.marshall_Day(request_data, i, days__arg);
		byte[] response_data = _send(new Bytes(request_data, i[0])).bytes();
		i[0] = 0;
		Unmarshaller.unmarshall_int(response_data, i);  // strip method_id
		return Unmarshaller.unmarshall_AvailabilityResponse(response_data, i);
	}

	public BookResponse bookFacility(String facilityName, String user, DayTime start, DayTime end) throws Exception {
		int[] i = {0};
		byte[] request_data = new byte[proto.get_buffer_size()];
		Marshaller.marshall_int(request_data, i, 2);
		Marshaller.marshall_string(request_data, i, facilityName);
		Marshaller.marshall_string(request_data, i, user);
		Marshaller.marshall_DayTime(request_data, i, start);
		Marshaller.marshall_DayTime(request_data, i, end);
		byte[] response_data = _send(new Bytes(request_data, i[0])).bytes();
		i[0] = 0;
		Unmarshaller.unmarshall_int(response_data, i);  // strip method_id
		return Unmarshaller.unmarshall_BookResponse(response_data, i);
	}

	public Response changeBooking(int bookingId, int offset) throws Exception {
		int[] i = {0};
		byte[] request_data = new byte[proto.get_buffer_size()];
		Marshaller.marshall_int(request_data, i, 3);
		Marshaller.marshall_int(request_data, i, bookingId);
		Marshaller.marshall_int(request_data, i, offset);
		byte[] response_data = _send(new Bytes(request_data, i[0])).bytes();
		i[0] = 0;
		Unmarshaller.unmarshall_int(response_data, i);  // strip method_id
		return Unmarshaller.unmarshall_Response(response_data, i);
	}

	public Response subscribe(String facilityName, int minutes) throws Exception {
		int[] i = {0};
		byte[] request_data = new byte[proto.get_buffer_size()];
		Marshaller.marshall_int(request_data, i, 4);
		Marshaller.marshall_string(request_data, i, facilityName);
		Marshaller.marshall_int(request_data, i, minutes);
		byte[] response_data = _send(new Bytes(request_data, i[0])).bytes();
		i[0] = 0;
		Unmarshaller.unmarshall_int(response_data, i);  // strip method_id
		return Unmarshaller.unmarshall_Response(response_data, i);
	}

	public Response extendBooking(int bookingId, int minutes) throws Exception {
		int[] i = {0};
		byte[] request_data = new byte[proto.get_buffer_size()];
		Marshaller.marshall_int(request_data, i, 5);
		Marshaller.marshall_int(request_data, i, bookingId);
		Marshaller.marshall_int(request_data, i, minutes);
		byte[] response_data = _send(new Bytes(request_data, i[0])).bytes();
		i[0] = 0;
		Unmarshaller.unmarshall_int(response_data, i);  // strip method_id
		return Unmarshaller.unmarshall_Response(response_data, i);
	}

	public FacilitiesResponse viewFacilities() throws Exception {
		int[] i = {0};
		byte[] request_data = new byte[proto.get_buffer_size()];
		Marshaller.marshall_int(request_data, i, 6);
		byte[] response_data = _send(new Bytes(request_data, i[0])).bytes();
		i[0] = 0;
		Unmarshaller.unmarshall_int(response_data, i);  // strip method_id
		return Unmarshaller.unmarshall_FacilitiesResponse(response_data, i);
	}

}
