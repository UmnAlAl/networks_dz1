import java.util.zip.CRC32;

/**
 * Created by Installed on 15.05.2017.
 */
public class MyCRCImpl {

    public static String countCRC32(String data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data.getBytes());
        long hash = crc32.getValue();
        return Utils.LongToHexString(hash);
    }

    public static boolean checkCRC32(String dataWithCRC) {
        int limIndex = dataWithCRC.lastIndexOf("|");
        if(limIndex <= 0) {
            return false;
        }
        if(dataWithCRC.length() < 8) {
            return false;
        }
        String gotCRC = dataWithCRC.substring(limIndex + 1, dataWithCRC.length());
        String data = dataWithCRC.substring(0, limIndex);
        String countedCRC = countCRC32(data);
        if(gotCRC.equals(countedCRC)) {
            return true;
        }
        return false;
    }

}
