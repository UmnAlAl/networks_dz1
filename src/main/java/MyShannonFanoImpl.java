import java.util.*;

/**
 * Created by Installed on 15.05.2017.
 */
public class MyShannonFanoImpl {

    private static final int ASCII_LENGTH = 7;

    private String originalString;
    private int originalStringLength;
    private HashMap<Character, String> compressedResult;
    private HashMap<Character, Double> characterFrequency;
    private double entropy;
    private double averageLengthBefore;
    private double averageLengthAfter;
    private boolean probabilityIsGiven;

    public ShannonFano(String str) {
        super();
        originalString = str;
        originalStringLength = str.length();
        characterFrequency = new HashMap<Character, Double>();
        compressedResult = new HashMap<Character, String>();
        entropy = 0.0;
        averageLengthBefore = 0.0;
        averageLengthAfter = 0.0;
        probabilityIsGiven = false;

        this.calculateFrequency();
        this.compressString();
        this.calculateEntropy();
        this.calculateAverageLengthBeforeCompression();
        this.calculateAverageLengthAfterCompression();

    }

    public ShannonFano(String str, HashMap<Character, Double> probablity) {
        super();
        originalString = str;
        originalStringLength = str.length();

        characterFrequency = new HashMap<Character, Double>();

        double checkPoint = 0;
        for (Character c : originalString.toCharArray()) {
            checkPoint += probablity.get(c);
            characterFrequency.put(c, originalStringLength * probablity.get(c));
        }

        assert checkPoint == 1.0; // Invariant, make sure sum of probabilities
        // is 1

        compressedResult = new HashMap<Character, String>();
        entropy = 0.0;
        averageLengthBefore = 0.0;
        averageLengthAfter = 0.0;
        probabilityIsGiven = true;

        this.compressString();
        this.calculateEntropy();
        this.calculateAverageLengthBeforeCompression();
        this.calculateAverageLengthAfterCompression();

    }

    private void compressString() {
        List<Character> charList = new ArrayList<Character>();

        Iterator<Map.Entry<Character, Double>> entries = characterFrequency.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Character, Double> entry = entries.next();
            charList.add(entry.getKey());
        }

        appendBit(compressedResult, charList, true);
    }

    private void appendBit(HashMap<Character, String> result, List<Character> charList, boolean up) {
        String bit = "";
        if (!result.isEmpty()) {
            bit = (up) ? "0" : "1";
        }

        for (Character c : charList) {
            String s = (result.get(c) == null) ? "" : result.get(c);
            result.put(c, s + bit);
        }

        if (charList.size() >= 2) {
            int separator = (int) Math.floor((float) charList.size() / 2.0);

            List<Character> upList = charList.subList(0, separator);
            appendBit(result, upList, true);
            List<Character> downList = charList.subList(separator, charList.size());
            appendBit(result, downList, false);
        }
    }

    private void calculateFrequency() {
        for (Character c : originalString.toCharArray()) {
            if (characterFrequency.containsKey(c)) {
                characterFrequency.put(c, new Double(characterFrequency.get(c) + 1.0));
            } else {
                characterFrequency.put(c, 1.0);
            }
        }
    }

    private void calculateEntropy() {
        double probability = 0.0;
        for (Character c : originalString.toCharArray()) {
            probability = 1.0 * characterFrequency.get(c) / originalStringLength;
            entropy += probability * (Math.log(1.0 / probability) / Math.log(2));
        }
    }

    private void calculateAverageLengthBeforeCompression() {
        double probability = 0.0;
        for (Character c : originalString.toCharArray()) {
            probability = 1.0 * characterFrequency.get(c) / originalStringLength;
            averageLengthBefore += probability * ASCII_LENGTH;
        }
    }

    private void calculateAverageLengthAfterCompression() {
        double probability = 0.0;
        for (Character c : originalString.toCharArray()) {
            probability = 1.0 * characterFrequency.get(c) / originalStringLength;
            averageLengthAfter += probability * compressedResult.get(c).length();
        }
    }

    @SuppressWarnings("unchecked")
    public HashMap<Character, Double> getCharacterFrequency() {
        return (HashMap<Character, Double>) characterFrequency.clone();
    }

    @SuppressWarnings("unchecked")
    public HashMap<Character, String> getCompressedResult() {
        return (HashMap<Character, String>) compressedResult.clone();
    }

    @Override
    public String toString() {
        String str = "";
        str += "*** Probability is" + (probabilityIsGiven ? " " : " Not ") + "Given. "
                + (probabilityIsGiven ? "We did not calculate the probability."
                : "Probability was calculated using frequency of each character in the given String.")
                + "\n";
        str += "Original String: \"" + originalString + "\"\n";
        str += "------------------------------------------------------------------------\n";
        str += "Symbol\t\tFrequency\tProbability\tShannon-F Code\tASCII Code\n";
        str += "------------------------------------------------------------------------\n";

        for (Character c : compressedResult.keySet()) {
            str += "'" + c + "'" + "\t\t" + Math.round(characterFrequency.get(c) * 100.0) / 100.0 + "\t\t"
                    + Math.round(characterFrequency.get(c) / originalStringLength * 10000.0) / 10000.0 + "\t\t"
                    + compressedResult.get(c) + "\t\t" + Integer.toBinaryString((int) c);
            str += "\n";
        }
        str += "------------------------------------------------------------------------\n";
        str += "Efficiency before Compression: " + 100 * (Math.round((entropy / averageLengthBefore) * 100.0) / 100.0)
                + "%\n";
        str += "Efficiency after Compression: " + 100 * (Math.round((entropy / averageLengthAfter) * 100.0) / 100.0)
                + "%\n";
        str += "------------------------------------------------------------------------\n";
        return str;
    }






    public static final HashMap<Character, Double> myCharacterfreqs;
    static {
        myCharacterfreqs = new HashMap<>();
        myCharacterfreqs.put('a', 0.10);
        myCharacterfreqs.put('b', 0.20);
        myCharacterfreqs.put('c', 0.30);
        myCharacterfreqs.put('d', 0.40);
    }

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
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

    }

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
