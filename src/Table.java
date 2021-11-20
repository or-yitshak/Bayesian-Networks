import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    String[] nodes_in;
    ArrayList<String[]> table; //row as the length of the double list and cols as the number of nodes_in
    double[] probs;

    public Table(String[] nums ,MyNode nd){
        nodes_in = new String[nd.parents.size()+1];
        for (int i = 0; i <nd.parents.size(); i++) {
            nodes_in[i] = nd.parents.get(i).name;
        }
        nodes_in[nodes_in.length-1] = nd.name;

        table = new ArrayList<>();
        ArrayList<MyNode> nd_list = new ArrayList<>(nd.parents);
        nd_list.add(nd);
        Combinations(nd_list,table);

        probs = new double[nums.length];
        for (int i = 0; i < nums.length; i ++) {
            probs[i] = Double.parseDouble(nums[i]);
        }

    }

    public static void Combinations(ArrayList<MyNode> nodes, ArrayList<String[]> t){
        String[] curr = new String[nodes.size()];
        recAddAllCombinations(nodes, curr , 0, t);
    }

    public static void recAddAllCombinations(ArrayList<MyNode> nodes, String[] curr , int i ,ArrayList<String[]> t) {
        if(i==nodes.size()) {
            System.out.println(Arrays.toString(curr));
            t.add(curr.clone());
        }
        else{
            MyNode curr_node= nodes.get(i);
            int outs = curr_node.outcomes.size();
            for (int j = 0; j < outs; j++) {
                curr[i] = curr_node.outcomes.get(j);
                recAddAllCombinations(nodes, curr, i+1, t);
            }
        }
    }


    @Override
    public String toString() {
        String str = "[";
        for (int i = 0; i < table.size()-1; i++) {
            str += Arrays.toString(table.get(i))+", ";
        }
        str += Arrays.toString(table.get(table.size()-1))+"]";
        return "Table{" +"\n"+
                "nodes_in=" + Arrays.toString(nodes_in) +"\n"+
                ", table=" + str +"\n"+
                ", probs=" + Arrays.toString(probs) +"\n"+
                '}';
    }
}
