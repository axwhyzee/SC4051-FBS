package middleware.protos;

import middleware.network.Servicer;
import middleware.network.Bytes;


public class FacilityBookingServiceServicer implements Servicer {
    private final FacilityBookingService service;

    public FacilityBookingServiceServicer(FacilityBookingService service) {
        this.service = service;
    }

    public int _dispatch(byte[] message, byte[] response) throws Exception {
        int[] i = new int[]{0};
        int method_id = Unmarshaller.unmarshall_int(message, i);

        switch (method_id) {
			case 1:
				String facilityName__1__arg = Unmarshaller.unmarshall_string(message, i);
				int days__1__arg__len = Unmarshaller.unmarshall_int(message, i);
				Day[] days__1__arg = new Day[days__1__arg__len];
				for (int j=0; j<days__1__arg__len; j++)
					days__1__arg[j] = Unmarshaller.unmarshall_Day(message, i);
				AvailabilityResponse queryFacility__result = service.queryFacility(facilityName__1__arg, days__1__arg);
				Marshaller.marshall_AvailabilityResponse(response, i, queryFacility__result);
				return i[0];
			case 2:
				String user__2__arg = Unmarshaller.unmarshall_string(message, i);
				DayTime start__2__arg = Unmarshaller.unmarshall_DayTime(message, i);
				DayTime end__2__arg = Unmarshaller.unmarshall_DayTime(message, i);
				BookResponse bookFacility__result = service.bookFacility(user__2__arg, start__2__arg, end__2__arg);
				Marshaller.marshall_BookResponse(response, i, bookFacility__result);
				return i[0];
			case 3:
				int bookingId__3__arg = Unmarshaller.unmarshall_int(message, i);
				int offset__3__arg = Unmarshaller.unmarshall_int(message, i);
				Response changeBooking__result = service.changeBooking(bookingId__3__arg, offset__3__arg);
				Marshaller.marshall_Response(response, i, changeBooking__result);
				return i[0];
			case 4:
				String facilityName__4__arg = Unmarshaller.unmarshall_string(message, i);
				int minutes__4__arg = Unmarshaller.unmarshall_int(message, i);
				Response subscribe__result = service.subscribe(facilityName__4__arg, minutes__4__arg);
				Marshaller.marshall_Response(response, i, subscribe__result);
				return i[0];
			case 5:
				int bookingId__5__arg = Unmarshaller.unmarshall_int(message, i);
				int minutes__5__arg = Unmarshaller.unmarshall_int(message, i);
				Response extendBooking__result = service.extendBooking(bookingId__5__arg, minutes__5__arg);
				Marshaller.marshall_Response(response, i, extendBooking__result);
				return i[0];
			case 6:
				FacilitiesResponse viewFacilities__result = service.viewFacilities();
				Marshaller.marshall_FacilitiesResponse(response, i, viewFacilities__result);
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
