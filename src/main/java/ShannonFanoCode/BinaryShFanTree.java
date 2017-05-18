package ShannonFanoCode;

import java.util.HashMap;

/**
 * Created by Installed on 18.05.2017.
 */
public class BinaryShFanTree {

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
    public TreeNode curPos;

    public BinaryShFanTree() {
        root = new TreeNode();
        curPos = root;
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


    public class StringReadPrefixFromTreeResult {
        String s; //string
        Integer endPos; //prefix end
        TreeNode endNode; //which node the search stopped
    }

    //reads prefix
    public StringReadPrefixFromTreeResult readPathWithBitString(String s) {
        StringReadPrefixFromTreeResult result = new StringReadPrefixFromTreeResult();
        result.s = s;
        result.endPos = 0;
        result.endNode = root;
        int len = s.length();
        if(len <= 0)
            return null;

        //move current node via tree until we go to the leaf
        for(int i = 0; i < len; ++i) {
            //if get 0 - try to move left if possible, else return the result
            if(s.charAt(i) == '0') {
                if(result.endNode.leftChild != null) { //if we can go left - let's do it
                    result.endNode = result.endNode.leftChild;
                    result.endPos++;
                    continue;
                }
                else { //if we can't go left - check if we are in a leaf, if not - there's an error into input string, if yes - we read new symbol
                    if(result.endNode.isLeaf) {
                        return result;
                    }
                    else {
                        return null;
                    }
                }//if-else left child
            }
            else {
                if(result.endNode.rightChild != null) { //if we can go right - let's do it
                    result.endNode = result.endNode.rightChild;
                    result.endPos++;
                    continue;
                }
                else { //if we can't go right - check if we are in a leaf, if not - there's an error into input string, if yes - we read new symbol
                    if(result.endNode.isLeaf) {
                        return result;
                    }
                    else {
                        return null;
                    }
                }//if-else right child
            }//if-else char at
        }//for

        //if we iterated through the whole string and did not return from cycle -
        //that means we read last symbol fully or not, if not - its error of input data, if yes - ok
        if(result.endNode.isLeaf) {
            return result;
        }
        else {
            return null;
        }
    }

    public void buildBinaryShFanTreeFromCodeTable(HashMap<Character, String> codeMap) {
        root = new TreeNode();
        for (Character c:
                codeMap.keySet()) {
            insertPathWithBitString(codeMap.get(c), root, c);
        }
    }

}//tree class
