package bitcoffee;

import java.util.ArrayList;

@SuppressWarnings("UnusedAssignment")
public class MerkleTree {
    private final int total;
    private final int max_depth;
    private final ArrayList<ArrayList<String>> nodes = new ArrayList<>();

    private int current_depth = 0;
    private int current_index = 0;

    /***************************************************************************/
    public static byte[] merkleParent(byte[] hash1, byte[] hash2) {
        if (hash1.length !=32 || hash2.length!=32) {
            try {
                throw new Exception("Wrong hashes sizes");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        var merged = Kit.bytesToHexString(hash1)+Kit.bytesToHexString(hash2);
        return Kit.hash256(Kit.hexStringToByteArray(merged));
    }

    /***************************************************************************/
    public static String merkleParent(String hash1, String hash2) {
        return Kit.bytesToHexString(Kit.hash256(Kit.hexStringToByteArray(hash1+hash2)));
    }

    /***************************************************************************/
    public static ArrayList<String> merkleParentLevel(ArrayList<String> hashes) {

        if (hashes.size()==1)
            throw new RuntimeException("Cannot derive parent level, size 1");

        if (hashes.size()%2==1)
            hashes.add(hashes.get(hashes.size()-1));

        var parent_level = new ArrayList<String>();

        for (int i=0;i<hashes.size();i+=2) {

            var parent = merkleParent(hashes.get(i),hashes.get(i+1));
            parent_level.add(parent);
        }

        return parent_level;
    }

    /***************************************************************************/
    public static String merkleRoot(ArrayList<String> hashes) {

        var current_level = hashes;

        while (current_level.size()>1) {
            current_level = merkleParentLevel(current_level);
        }

        return current_level.get(0);
    }


    public String getRoot() {
        return nodes.get(0).get(0);
    }
    public ArrayList<String> getNodesLevel(int level) {
        return nodes.get(level);
    }

    public void setNodesLevel(int level, ArrayList<String> hash_list) {
        nodes.set(level,hash_list);
    }

    public MerkleTree(int total) {
        this.total = total;
        this.max_depth = (int)Math.ceil(Kit.log2(total));

        for (int depth=0; depth<max_depth+1;depth++) {
            var num_items = (int)Math.ceil((double)this.total/Math.pow(2,max_depth-depth) );
            var level_hashes = new ArrayList<String>(num_items);

            for (int i=0;i<num_items;i++) level_hashes.add("-");

            this.nodes.add(level_hashes);
        }

        current_index = 0;
        current_depth = 0;
    }

    @Override
    public String toString() {
        return "bitcoffee.MerkleTree{" +
                "total=" + total +
                ", max_depth=" + max_depth +
                ", root=" + nodes.get(0) +
                ", current_depth=" + current_depth +
                ", current_index=" + current_index +
                '}';
    }

    public void print() {
        System.out.println(this);
        System.out.println("\n--------BEGIN TREE ------------------------------------------");
        for (int d=0;d<max_depth+1;d++) {
            System.out.println("Level "+d+":");
            for (int l=0;l<this.nodes.get(d).size();l++) {
                System.out.println(this.nodes.get(d).get(l));
            }
        }
        System.out.println("\n--------END TREE ------------------------------------------");
    }

    public void goUp() {
        this.current_depth-=1;
        this.current_index/=2;
    }

    public void goLeft() {
        this.current_depth +=1;
        this.current_index *=2;
    }

    public void goRight() {
        this.current_depth +=1;
        this.current_index = current_index*2+1;
    }

    public void setCurrentNode(String value) {
        var current_level = this.nodes.get(current_depth);
        current_level.set(current_index,value);
        this.nodes.set(current_depth,current_level);
    }

    public String getCurrentNode() {
        return nodes.get(current_depth).get(current_index);
    }

    public String getLeftNode() {
        return nodes.get(current_depth+1).get(current_index*2);
    }

    public String getRightNode() {
        return nodes.get(current_depth+1).get(current_index*2+1);
    }

    public boolean isLeaf() {
        return current_depth == max_depth;
    }

    public boolean rightExists()  {
        return nodes.get(current_depth+1).size() > current_index*2+1;
    }

    public void populateTree(ArrayList<String> hashes) {

        this.setNodesLevel(max_depth, hashes);
        while (this.getRoot().equals("-")) {

            if (this.isLeaf()) goUp();
            else {
                var left_hash = getLeftNode();

                if (left_hash.equals("-")) goLeft();
                else if (rightExists()) {
                    var right_hash = getRightNode();
                    if (right_hash.equals("-"))
                        goRight();
                    else {
                        setCurrentNode(merkleParent(left_hash, right_hash));
                        goUp();
                    }
                } else {
                    setCurrentNode(merkleParent(left_hash, left_hash));
                    goUp();
                }
            }
        }
        current_depth = 0;
        current_index = 0;
    }

    public void populateTree(ArrayList<Boolean> flag_bits, ArrayList<String> hashes) {

        while (getRoot().equals("-")) {
            if (isLeaf()) {
                flag_bits.remove(0);
                this.setCurrentNode(hashes.remove(0));
                this.goUp();
            }
            else {
                var left_hash = this.getLeftNode();

                if (left_hash.equals("-")) {
                    if (!flag_bits.remove(0)) {
                        setCurrentNode(hashes.remove(0));
                        this.goUp();
                    } else
                        this.goLeft();
                }
                else if (this.rightExists()) {
                    var right_hash = this.getRightNode();

                    if (right_hash.equals("-")) {
                        this.goRight();
                    } else {
                        setCurrentNode(merkleParent(left_hash, right_hash));
                        this.goUp();
                    }
                }
                else
                {
                    setCurrentNode(merkleParent(left_hash,left_hash));
                    goUp();
                }
            }
        } // while

        if (hashes.size()!=0) {
            throw new RuntimeException("Not all hashes consumed");
        }

        for (boolean f:flag_bits)  {
            if (f)
                throw new RuntimeException("Not all flag bits consumed");
        }
    }

}
