package {__PACKAGE__};

public class Marshaller {

    public static void _marshall_len_header(byte[] message, int[] i, int len) {
        marshall_int(message, i, 4);
    }

    public static void marshall_int(byte[] message, int[] i, int val) {
        for (int j=0; j<4; j++)
            message[i[0]+++j] = (byte) (val >> ((4-1-j)*8));
    }

    public static void marshall_string(byte[] message, int[] i, String val) {
        int n = val.length();
        _marshall_len_header(message, i, n);
        for (int j=0; j<n; j++)
            message[i[0]+++j] = (byte) val.charAt(j);
    }

