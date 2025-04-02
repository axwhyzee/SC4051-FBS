public class Marshaller {

    public static void marshall_len_header(byte[] message, int[] i, int len) {
        marshall_int(message, i, len);
    }

    public static void marshall_int(byte[] message, int[] i, int val) {
        for (int j=0; j<4; j++)
            message[i[0]++] = (byte) (val >> ((4-1-j)*8));
    }

    public static void marshall_string(byte[] message, int[] i, String val) {
        int n = val.length();
        marshall_len_header(message, i, n);
        for (int j=0; j<n; j++)
            message[i[0]++] = (byte) val.charAt(j);
    }

