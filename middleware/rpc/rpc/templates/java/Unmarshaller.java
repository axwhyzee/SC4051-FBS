package {__PACKAGE__};

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

