import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * this class represent a CPT table. it holds in it an ArrayList that represent the order of the columns
 * of the table, and a Hashtable such that the key is a String represent a row in the table and the
 * value is the probability of this row.
 */
public class Table implements Comparable<Table> {
    ArrayList<String> nodes_order;
    //    Hashtable<String, Double> table;//Strings need to be changed to ArrayList that means that it should be Hashtable<ArrayList<String>, Double>
    Hashtable<ArrayList<String>, Double> table;

    public Table() {
        table = new Hashtable<>();
        nodes_order = new ArrayList<>();
    }

    public Table(Table t) {
        this.nodes_order = new ArrayList<>(t.nodes_order);
        this.table = new Hashtable<>();
        Set<ArrayList<String>> keys = new HashSet<>(t.table.keySet());
        for (ArrayList<String> key : keys) {
            double prob = t.table.get(key);
            ArrayList<String> new_key = new ArrayList<>(key);
            this.table.put(new_key, prob);
        }
    }


    public Table(String[] nums, MyNode nd) {
        nodes_order = new ArrayList<>();
        for (int i = 0; i < nd.parents.size(); i++) {
            nodes_order.add(nd.parents.get(i).name);
        }
        nodes_order.add(nd.name);

//        ArrayList<String> values_table = new ArrayList<>();
        ArrayList<ArrayList<String>> values_table = new ArrayList<>();
        ArrayList<MyNode> nd_list = new ArrayList<>(nd.parents);
        nd_list.add(nd);
        Combinations(nd_list, values_table);

        double[] probs = new double[nums.length];
        for (int i = 0; i < nums.length; i++) {
            probs[i] = Double.parseDouble(nums[i]);
        }

        table = new Hashtable<>();
        for (int i = 0; i < probs.length; i++) {
            ArrayList<String> curr_str = values_table.get(i);
            double curr_prob = probs[i];
            table.put(curr_str, curr_prob);
        }
    }

    /**
     * this function calls the recursive function recAddAllCombinations.
     *
     * @param nodes
     * @param t
     */
    public static void Combinations(ArrayList<MyNode> nodes, ArrayList<ArrayList<String>> t) {
//        String curr = "";
        ArrayList<String> curr = new ArrayList<>(nodes.size());
        recAddAllCombinations(nodes, curr, 0, t);
    }

    /**
     * this function create all the rows of this table. that means it will go over the columns (node_order),
     * and for each one it will go over outcomes it can get and then will add it to a string that eventually will
     * represent a row in the table.
     *
     * @param nodes - a list of node that appears in this table.
     * @param curr  - the string that will eventually represent a row in the table.
     * @param i     - counter.
     * @param t     - the list that contains all the rows.
     */
    public static void recAddAllCombinations(ArrayList<MyNode> nodes, ArrayList<String> curr, int i, ArrayList<ArrayList<String>> t) {
        if (i == nodes.size()) {
            ArrayList<String> new_row = new ArrayList<>(curr);
            t.add(new_row);
        } else {
            MyNode curr_node = nodes.get(i);
            int outs = curr_node.outcomes.size();
            for (int j = 0; j < outs; j++) {
                curr.add(curr_node.outcomes.get(j));
                recAddAllCombinations(nodes, curr, i + 1, t);
                curr.remove(i);
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

    /**
     * At first this function will compare 2 tables by their size (the size of the Hashmap),
     * if they are equal it will compare them by the ASCII value of their variables names.
     *
     * @param t
     * @return
     */
    @Override
    public int compareTo(Table t) {
        int ans = this.table.size() - t.table.size();
        if (ans == 0) {
            int sum1 = 0;
            for (int i = 0; i < this.nodes_order.size(); i++) {
                String curr_str = this.nodes_order.get(i);
                for (int j = 0; j < curr_str.length(); j++) {
                    char c = curr_str.charAt(j);
                    int value = (int) (c);
                    sum1 += value;
                }
            }
            int sum2 = 0;
            for (int i = 0; i < t.nodes_order.size(); i++) {
                String curr_str = t.nodes_order.get(i);
                for (int j = 0; j < curr_str.length(); j++) {
                    char c = curr_str.charAt(j);
                    int value = (int) (c);
                    sum2 += value;
                }
            }
            if (sum1 == sum2) {
                return 0;
            }
            if (sum1 > sum2) {
                return 1;
            } else {
                return -1;
            }
        } else if (ans > 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
