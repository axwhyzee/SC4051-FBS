package middleware\protos;

public class Marshaller {

    public static void marshall_len_header(byte[] message, int[] i, int len) {
        marshall_int(message, i, 4);
    }

    public static void marshall_int(byte[] message, int[] i, int val) {
        for (int j=0; j<4; j++)
            message[i[0]+++j] = (byte) (val >> ((4-1-j)*8));
    }

    public static void marshall_string(byte[] message, int[] i, String val) {
        int n = val.length();
        marshall_len_header(message, i, n);
        for (int j=0; j<n; j++)
            message[i[0]+++j] = (byte) val.charAt(j);
    }

	public static void marshall_Day(byte[] message, int[] i, Day val) {
		marshall_int(message, i, val.ordinal() + 1);
	}

	public static void marshall_DayTime(byte[] message, int[] i, DayTime val) {
		marshall_Day(message, i, val.day());
		marshall_int(message, i, val.hour());
		marshall_int(message, i, val.minute());
	}

	public static void marshall_Interval(byte[] message, int[] i, Interval val) {
		marshall_DayTime(message, i, val.start());
		marshall_DayTime(message, i, val.end());
	}

	public static void marshall_Booking(byte[] message, int[] i, Booking val) {
		marshall_string(message, i, val.user());
		marshall_DayTime(message, i, val.start());
		marshall_DayTime(message, i, val.end());
	}

	public static void marshall_Facility(byte[] message, int[] i, Facility val) {
		marshall_string(message, i, val.name());
		marshall_string(message, i, val.type());
		marshall_len_header(message, i, val.bookings().length);
		for (Booking bookings__item : val.bookings())
			marshall_Booking(message, i, bookings__item);
	}

	public static void marshall_Response(byte[] message, int[] i, Response val) {
		marshall_string(message, i, val.error());
	}

	public static void marshall_AvailabilityResponse(byte[] message, int[] i, AvailabilityResponse val) {
		marshall_string(message, i, val.error());
		marshall_len_header(message, i, val.availability().length);
		for (Interval availability__item : val.availability())
			marshall_Interval(message, i, availability__item);
	}

	public static void marshall_BookResponse(byte[] message, int[] i, BookResponse val) {
		marshall_string(message, i, val.error());
		marshall_int(message, i, val.bookingId());
	}

	public static void marshall_FacilitiesResponse(byte[] message, int[] i, FacilitiesResponse val) {
		marshall_string(message, i, val.error());
		marshall_len_header(message, i, val.facilities().length);
		for (Facility facilities__item : val.facilities())
			marshall_Facility(message, i, facilities__item);
	}

}