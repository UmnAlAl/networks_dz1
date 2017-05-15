/**
 * Created by Installed on 15.05.2017.
 */
public class Utils {

    public static String LongToHexString(Long x) {
        return Long.toHexString(x);
    }

    public static Long LongFromHexString(String s) {
        return Long.parseLong(s, 16);
    }

    public static boolean isPowerOfTwo(int x) {
        return ((x & (x - 1)) == 0);
    }

    public static int getBit(int n, int k) {
        return (n >> (k - 1)) & 1;
    }

    public static int setBit(int n, int k) {
        return n | (1 << (k - 1));
    }

}
