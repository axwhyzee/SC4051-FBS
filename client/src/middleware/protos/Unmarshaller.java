package middleware\protos;

public class Unmarshaller {

    public static int unmarshall_int(byte[] message, int[] i) {
        int res = 0;
        for (int j=0; j<4; j++) {
            res <<= (j*8);
            res |= message[i[0]+j];
        }
        i[0] += 4;
        return res;
    }

    public static String unmarshall_string(byte[] message, int[] i) {
        int n = unmarshall_int(message, i);
        byte[] res = new byte[n];
        System.arraycopy(message, i[0], res, 0, n);
        i[0] += n;
        return new String(res);
    }

	public static Day unmarshall_Day(byte[] message, int[] i) throws EnumConstantNotPresentException {
		int enum_id = unmarshall_int(message, i);
		switch (enum_id) {
			case 1:
				return Day.MONDAY;
			case 2:
				return Day.TUESDAY;
			case 3:
				return Day.WEDNESDAY;
			case 4:
				return Day.THURSDAY;
			case 5:
				return Day.FRIDAY;
			case 6:
				return Day.SATURDAY;
			case 7:
				return Day.SUNDAY;
			default:
				throw new EnumConstantNotPresentException(Day.class, "Invalid ordinal value: " + enum_id);		}
	}

	public static DayTime unmarshall_DayTime(byte[] message, int[] i) {
		Day day__arg = unmarshall_Day(message, i);
		int hour__arg = unmarshall_int(message, i);
		int minute__arg = unmarshall_int(message, i);
		return new DayTime(day__arg, hour__arg, minute__arg);
	}

	public static Interval unmarshall_Interval(byte[] message, int[] i) {
		DayTime start__arg = unmarshall_DayTime(message, i);
		DayTime end__arg = unmarshall_DayTime(message, i);
		return new Interval(start__arg, end__arg);
	}

	public static Booking unmarshall_Booking(byte[] message, int[] i) {
		String user__arg = unmarshall_string(message, i);
		DayTime start__arg = unmarshall_DayTime(message, i);
		DayTime end__arg = unmarshall_DayTime(message, i);
		return new Booking(user__arg, start__arg, end__arg);
	}

	public static Facility unmarshall_Facility(byte[] message, int[] i) {
		String name__arg = unmarshall_string(message, i);
		String type__arg = unmarshall_string(message, i);
		int bookings__len = unmarshall_int(message, i);
		Booking[] bookings__arg = new Booking[bookings__len];
		for (int j=0; j<bookings__len; j++)
			bookings__arg[j] = unmarshall_Booking(message, i);
		return new Facility(name__arg, type__arg, bookings__arg);
	}

	public static Response unmarshall_Response(byte[] message, int[] i) {
		String error__arg = unmarshall_string(message, i);
		return new Response(error__arg);
	}

	public static AvailabilityResponse unmarshall_AvailabilityResponse(byte[] message, int[] i) {
		String error__arg = unmarshall_string(message, i);
		int availability__len = unmarshall_int(message, i);
		Interval[] availability__arg = new Interval[availability__len];
		for (int j=0; j<availability__len; j++)
			availability__arg[j] = unmarshall_Interval(message, i);
		return new AvailabilityResponse(error__arg, availability__arg);
	}

	public static BookResponse unmarshall_BookResponse(byte[] message, int[] i) {
		String error__arg = unmarshall_string(message, i);
		int bookingId__arg = unmarshall_int(message, i);
		return new BookResponse(error__arg, bookingId__arg);
	}

	public static FacilitiesResponse unmarshall_FacilitiesResponse(byte[] message, int[] i) {
		String error__arg = unmarshall_string(message, i);
		int facilities__len = unmarshall_int(message, i);
		Facility[] facilities__arg = new Facility[facilities__len];
		for (int j=0; j<facilities__len; j++)
			facilities__arg[j] = unmarshall_Facility(message, i);
		return new FacilitiesResponse(error__arg, facilities__arg);
	}

}