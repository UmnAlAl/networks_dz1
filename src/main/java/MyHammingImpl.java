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

    //insert parity symbols for error correction
    private static int expandData(int x) {
        int y = 0; //val with correct patity checks
        int curXpos = 1; //cur pos in 26 input data bits from the lowest one
        int[] parityVals = new int[5]; //parity vals for power of two positions
        for (int i = 1; i <= 31; ++i) {
            if(Utils.isPowerOfTwo(i)) //if parity bit - miss
                continue;
            //if null bit - its value not influences parity bits, as data bit its null by default
            if(Utils.getBit(x, curXpos) == 0) {
                curXpos++; //we read new bit from x
                continue;
            }
            //set 1 bit into apropriate data place
            y = Utils.setBit(y, i);
            curXpos++;//we read new bit from x

            //update parity bits - i is true pos of bit, we add 1 mod 2 to parity bits, that containted by cur bit number
            for (int j = 1; j <= 5; j++) {
                if(Utils.getBit(i, j) == 1) {
                    parityVals[j - 1] ^= 1;
                }
            }

        }
        //parity bits are nulls yet - lets set them
        for(int k = 1; k <= 5; k++) {
            if(parityVals[k - 1] == 1) {
                y = Utils.setBit( y, (1 << (k - 1)) );
            }
        }
        return y;
    }

    //remove parity symbols with error correction
    private static int reduceData(int x) {
        int s = 0; //syndrome
        //multiplication on check matrix H
        for (int i = 1; i <= 31; ++i) {
            //if 1 bit - add column from H matrix
            if(Utils.getBit(x, i) == 1) {
                s ^= i;
            }
        }
        //if mistake detected
        if(s != 0) {
            x = Utils.invBit(x, s);
        }
        int y = 0; //val without correct patity checks
        //mistakes fixed - lets get data bits
        int curYpos = 1; //cur pos in 26 bits data bits from the lowest one
        for (int i = 1; i <= 31; ++i) {
            //miss parity bit
            if(Utils.isPowerOfTwo(i))
                continue;
            //if 1 bit - set it in y (0 not necessary to be set)
            if(Utils.getBit(x, i) == 1) {
                y = Utils.setBit(y, curYpos);
            }
            curYpos++; //we "wrote" to y
        }
        return y;
    }

    public static int HammingEncode(int x) {
        //only low 26 bits are used as data bits
        x = x & (0x3FFFFFF);
        return expandData(x);
    }

    public static int HammingDecode(int y) {
        return reduceData(y);
    }

}
