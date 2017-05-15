/**
 * Created by Installed on 15.05.2017.
 */
public class MyHammingImpl {

    //length of codeword - n = 31 = 2^5 - 1
    //k = 2^5 - 1 - 5 = 26
    //check bits added at the begining

    private static boolean isPowerOfTwo(int x) {
        return ((x & (x - 1)) == 0);
    }

    //insert places for parity symbols
    private static int expandData(int x) {
        int y = 0; //val with correct patity checks
        int curXpos = 1; //cur pos in 26 input data bits from the lowest one
        int[] parityVals = new int[5]; //parity vals for power of two positions
        for (int i = 1; i <= 31; ++i) {
            if(Utils.isPowerOfTwo(i)) //if parity bit - miss
                continue;
            if(Utils.getBit(x, curXpos) == 0) { //
                curXpos++;
                continue;
            }
            y = Utils.setBit(y, i);
            curXpos++;
        }
        //parity bits are nulls yet
        return y;
    }

    public static int HammingEncode(int x) {
        //only low 26 bits are used as data bits
        x = x & (0x3FFFFFF);
        //bits from 27 to 31 (5 bits) are used to parity check;

    }

}
