package middleware.protos;

import java.net.InetAddress;
import middleware.rudp.Protocol;
import middleware.rudp.Bytes;


public class FacilityBookingServiceStub {
	
	private final Protocol proto;
	private final int server_port;
	private final InetAddress server_addr;
	

	public FacilityBookingServiceStub(InetAddress server_addr, int server_port, Protocol proto) {
		this.proto = proto;
		this.server_addr = server_addr;
		this.server_port = server_port;
	}

	AvailabilityResponse queryFacility(String facilityName, Day[] days) {
		byte[] data = new byte[proto.get_buffer_size()];
		int i[] = {0};
		
		// marshall RPC args
		Marshaller.marshall_int(data, i, 1);
		Marshaller.marshall_string(data, i, facilityName);
		for (Day day : days)
			Marshaller.marshall_Day(data, i, day);
		
		// make RPC and receive response
		Bytes response = proto.send(server_addr, server_port, new Bytes(data, i[0]));
		i[0] = 0;
		return Unmarshaller.unmarshall_AvailabilityResponse(response.bytes(), i);
	};
	BookResponse bookFacility(String user, DayTime start, DayTime end) {/* TODO: marshall and send to server via UDP */};
	Response changeBooking(int bookingId, int offset) {/* TODO: marshall and send to server via UDP */};
	Response subscribe(String facilityName, int minutes) {/* TODO: marshall and send to server via UDP */};
	Response extendBooking(int bookingId, int minutes) {/* TODO: marshall and send to server via UDP */};
	FacilitiesResponse viewFacilities() {/* TODO: marshall and send to server via UDP */};
}