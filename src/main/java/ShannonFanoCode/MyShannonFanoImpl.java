package ShannonFanoCode;

import java.util.*;

/**
 * Created by Installed on 15.05.2017.
 */
public class MyShannonFanoImpl {

    public static final HashMap<Character, Double> myCharacterfreqs;
    static {
        myCharacterfreqs = new HashMap<>();
        myCharacterfreqs.put('a', 0.10);
        myCharacterfreqs.put('b', 0.10);
        myCharacterfreqs.put('c', 0.10);
        myCharacterfreqs.put('d', 0.20);
        myCharacterfreqs.put('e', 0.20);
        myCharacterfreqs.put('f', 0.30);
    }

    //create code table with entrias like <symbol, code>, code - string like "1001011"
    private static HashMap<Character, String> buildCodeTable(HashMap<Character, Double> freqs) {
        ArrayList<Map.Entry<Character, Double>> list = new ArrayList<Map.Entry<Character, Double>>(freqs.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Character, Double>>() {
            @Override
            public int compare(Map.Entry<Character, Double> o1, Map.Entry<Character, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        HashMap<Character, String> result = new HashMap<>();
        buildCodeTable(result, list, true);
        //cut off first 0 that was added on the first recursion step
        for (Map.Entry<Character, String> entry:
             result.entrySet()) {
            result.put(entry.getKey(), entry.getValue().substring(1));
        }
        return result;
    }

    private static void buildCodeTable(HashMap<Character, String> result, List<Map.Entry<Character, Double>> orderedFreqlistInterval, boolean up) {
        //which bit to concat to words into current list freq interval
        String bit = (up) ? "0" : "1";

        //update strings for symbols from current interval and count sum freq
        double sumFreq = 0;
        for (Map.Entry<Character, Double> entry : orderedFreqlistInterval) {
            Character c = entry.getKey();
            String s = (result.get(c) == null) ? "" : result.get(c);
            result.put(c, s + bit);
            sumFreq += entry.getValue();
        }

        if (orderedFreqlistInterval.size() >= 2) {

            //count separator for recursion
            int separator = 0;
            if(orderedFreqlistInterval.size() == 2) {
                separator = 1;
            }
            else {
                double curHalfSum = 0;
                while (curHalfSum < sumFreq / 2.0) {
                    curHalfSum += (orderedFreqlistInterval.get(separator)).getValue();
                    separator++;
                }
            }

            List<Map.Entry<Character, Double>> upList = orderedFreqlistInterval.subList(0, separator);
            buildCodeTable(result, upList, true);
            List<Map.Entry<Character, Double>> downList = orderedFreqlistInterval.subList(separator, orderedFreqlistInterval.size());
            buildCodeTable(result, downList, false);
        }
    }

    private static HashMap<String, Character> buildReverseCodeTable(HashMap<Character, Double> freqs) {
        HashMap<Character, String> codeTable = buildCodeTable(freqs);
        HashMap<String, Character> reverseCodeTable = new HashMap<>();
        for (Map.Entry<Character, String> entry:
             codeTable.entrySet()) {
            reverseCodeTable.put(entry.getValue(), entry.getKey());
        }
        return reverseCodeTable;
    }

    //to encode - just build code table and substitute
    public static String encode(String data) {
        StringBuilder result = new StringBuilder();
        HashMap<Character, String> codeTable = buildCodeTable(myCharacterfreqs);
        for (Character c:
             data.toCharArray()) {
            String code = codeTable.get(c);
            result.append ( (code == null) ? "" : code );
        }
        return result.toString();
    }

    //decoding with tree
    //to decode - build build tree code and use it to decoding
    public static String decode(String data) {
        StringBuilder result = new StringBuilder();
        HashMap<Character, String> codeTable = buildCodeTable(myCharacterfreqs);
        BinaryShFanTree tree = new BinaryShFanTree();
        tree.buildBinaryShFanTreeFromCodeTable(codeTable);

        while (data.length() != 0) {
            BinaryShFanTree.StringReadPrefixFromTreeResult prefix =  tree.readPathWithBitString(data);
            //if error occured - return what we could decode
            if(prefix == null)
                break;
            //read next symbol
            result.append(prefix.endNode.value);
            //cut what we decoded
            data = data.substring(prefix.endPos);
        }
        return result.toString();
    }

    //decoding with hash table
    private static void decode(StringBuilder result, String data, HashMap<String, Character> reverseCodeodeTable) {
        for (String s:
             reverseCodeodeTable.keySet()) {
            if(data.startsWith(s)) {
                result.append( reverseCodeodeTable.get(s) );
                decode(result, data.substring(s.length(), data.length()), reverseCodeodeTable);
                return;
            }//if
        }//foreach
    }//fn

    /*
    * Not used
    * */
    private static HashMap<Character, Double> countCharFrequencesIntoString(String s) {
        HashMap<Character, Double> result = new HashMap<>();
        //foreach for counting nums of symbols
        for (Character c:
             s.toCharArray()) {
            if(result.containsKey(c)) {
                result.put(c, result.get(c) + 1);
            }
            else {
                result.put(c, 1.0);
            }
        }
        //normalization to 1
        Double len = s.length() * 1.0;
        for (Character c:
                result.keySet()) {
            result.put(c, result.get(c)/len);
        }
        return result;
    }//fn

}
