import java.util.ArrayList;

public class MerkleTree {
    private final int total;
    private final int max_depth;
    private ArrayList<ArrayList<String>> nodes = new ArrayList<>();

    private int current_depth = 0;
    private int current_index = 0;


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
    }

    @Override
    public String toString() {
        return "MerkleTree{" +
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

}
