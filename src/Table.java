import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class Table {
    ArrayList<String> nodes_order;
    Hashtable<String,Double> table;
//    ArrayList<String> values_table; //row as the length of the double list and cols as the number of nodes_in
    public Table(){
        table = new Hashtable<>();
        nodes_order = new ArrayList<>();
    }


    public Table(String[] nums ,MyNode nd){
        nodes_order = new ArrayList<>();
        for (int i = 0; i <nd.parents.size(); i++) {
            nodes_order.add(nd.parents.get(i).name) ;
        }
        nodes_order.add(nd.name);

        ArrayList<String> values_table = new ArrayList<>();
        ArrayList<MyNode> nd_list = new ArrayList<>(nd.parents);
        nd_list.add(nd);
        Combinations(nd_list, values_table);

        double[] probs = new double[nums.length];
        for (int i = 0; i < nums.length; i ++) {
            probs[i] = Double.parseDouble(nums[i]);
        }

        table = new Hashtable<>();
        for (int i = 0; i < probs.length; i++) {
            String curr_str = values_table.get(i);
            double curr_prob = probs[i];
            table.put(curr_str,curr_prob);
        }
    }

    public static void Combinations(ArrayList<MyNode> nodes, ArrayList<String> t){
        String curr="";// = new String[nodes.size()];
        recAddAllCombinations(nodes, curr , 0, t);
    }

    public static void recAddAllCombinations(ArrayList<MyNode> nodes, String curr , int i ,ArrayList<String> t) {
        if(i==nodes.size()) {
            System.out.println(curr);
            t.add(curr);
        }
        else{
            MyNode curr_node= nodes.get(i);
            int outs = curr_node.outcomes.size();
            for (int j = 0; j < outs; j++) {
                curr += curr_node.outcomes.get(j);
                recAddAllCombinations(nodes, curr, i+1, t);
                curr = curr.substring(0,curr.length()-curr_node.outcomes.get(j).length());
            }
        }
    }

    @Override
    public String toString() {
        return "Table{" +
                "nodes_in=" + nodes_order +
                ", table=" + table +
                '}';
    }
//    @Override
//    public String toString() {
////        String str = "[";
////        for (int i = 0; i < table.size()-1; i++) {
////            str += Arrays.toString(table.get(i))+", ";
////        }
////        str += Arrays.toString(table.get(table.size()-1))+"]";
//        return "Table{" +"\n"+
//                "nodes_in=" + Arrays.toString(nodes_in) +"\n"+
//                ", table=" + values_table +"\n"+
//                ", probs=" + Arrays.toString(probs) +"\n"+
//                '}';
//    }
}
