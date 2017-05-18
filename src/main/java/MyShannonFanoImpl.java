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

    /*
    *
    * REDUNDANT: java hashmap is used instead of this tree
    *
    */
    private class BinaryShFanTree {

        public class TreeNode {
            public boolean isLeaf;
            public TreeNode leftChild; //to 0 edge
            public TreeNode rightChild; //to 1 edge
            public Character value;
            public  TreeNode() {
                this.isLeaf = false;
                this.leftChild = null;
                this.rightChild = null;
            }
        }

        public TreeNode root;

        public BinaryShFanTree() {
            root = new TreeNode();
        }

        //converts string like "100101010" as path at the tree with leaf value
        public void insertPathWithBitString(String s, TreeNode curNode, Character value) {
            //if came to leaf - insert val and return
            if(s.length() == 0) {
                curNode.isLeaf = true;
                curNode.value = value;
                return;
            }
            //if continue to left
            if(s.charAt(0) == '0') {
                //if no child in appropriate direction - add new node
                if(curNode.leftChild == null) {
                    TreeNode newNode = new TreeNode();
                    curNode.leftChild = newNode;
                    curNode.isLeaf = false;
                    insertPathWithBitString(s.substring(1), curNode.leftChild, value);
                }
                else { //if we have appropiate child - continue with it
                    insertPathWithBitString(s.substring(1), curNode.leftChild, value);
                }
            }//if-left-child
            else {
                //if no child in appropriate direction - add new node
                if(curNode.rightChild == null) {
                    TreeNode newNode = new TreeNode();
                    curNode.rightChild = newNode;
                    curNode.isLeaf = false;
                    insertPathWithBitString(s.substring(1), curNode.rightChild, value);
                }
                else { //if we have appropiate child - continue with it
                    insertPathWithBitString(s.substring(1), curNode.rightChild, value);
                }
            }//if-else
        }//fn

        //reads inserted
        public Character readPathWithBitString(String s, TreeNode curNode) {
            //if came to leaf - return val, else got bad string - return null
            if(s.length() == 0) {
                if(!curNode.isLeaf) {
                    return null;
                }
                return curNode.value;
            }
            //if continue to left
            if(s.charAt(0) == '0') {
                //if no child in appropriate direction - bad string
                if(curNode.leftChild == null) {
                    return null;
                }
                else { //if we have appropiate child - continue with it
                    return readPathWithBitString(s.substring(1), curNode.leftChild);
                }
            }//if-left-child
            else {
                //if no child in appropriate direction
                if(curNode.rightChild == null) {
                    return null;
                }
                else { //if we have appropiate child - continue with it
                    return readPathWithBitString(s.substring(1), curNode.rightChild);
                }
            }//if-else
        }

        public void buildBinaryShFanTreeFromCodeTable(HashMap<Character, String> codeMap) {
            root = new TreeNode();
            for (Character c:
                 codeMap.keySet()) {
                insertPathWithBitString(codeMap.get(c), root, c);
            }
        }

    }//tree class


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

    //to decode - build reverse code table and search occurency of code word at the start of cur string
    public static String decode(String data) {
        StringBuilder result = new StringBuilder();
        HashMap<String, Character> reverseCodeTable = buildReverseCodeTable(myCharacterfreqs);
        decode(result, data, reverseCodeTable);
        return result.toString();
    }

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
