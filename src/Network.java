import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * this class represent a Bayesian Network. it holds an ArrayList of the names of the nodes it contains,
 * and a Hashmap such that the key is a name of a node and the value is node object answering to this name.
 */

public class Network {

    public ArrayList<String> nodes_names;//the names of the nodes.
    Hashtable<String, MyNode> hs_names_nodes = new Hashtable<>();//the key is a name of a node and the value is node object answering to this name.

    public Network() {
        nodes_names = new ArrayList<>();
        hs_names_nodes = new Hashtable<>();

    }

    /**
     * this function gets a query in the form of "A-B|E1=e1,E2=e2,...,Ek=ek" and will check if A conditional
     * independent in B if E1,E2,...,Ek are given.
     *
     * @param query - "A-B|E1=e1,E2=e2,...,Ek=ek"
     * @return - true if A conditional independent in B and false otherwise.
     */

    public boolean bayes_ball(String query) {
        String[] x = query.split("\\|");
        String[] vertices = x[0].split("-");// an array of the 2 vertices that we want to check.
        ArrayList<String> givens = new ArrayList<>();
        if (x.length == 2) {
            givens = getGivens((x[1]));// if there are given evidence we will save them in ArrayList.
        }
        MyNode nd1 = hs_names_nodes.get(vertices[0]);
        MyNode nd2 = hs_names_nodes.get(vertices[1]);
        Queue<MyNode> q = new LinkedList<>();// a queue that will hold the nodes we need to visit.
        Queue<Boolean> came_from_son_q = new LinkedList<>();// a parallel queue to the one above that hold true if we got to this node from his children and false otherwise.
        boolean[][] visited = new boolean[this.nodes_names.size()][2];// 2D array the first column says if we already got to this node from his children and the 2 column says if we already got to this node from his parent.
        /*
        we will begin to travel on the network from nd1 so at first we will add his parents and children.
         */
        for (int i = 0; i < nd1.parents.size(); i++) {
            MyNode parent = nd1.parents.get(i);
            if (parent.equals(nd2)) {
                return false;// before entering the node to the queue we will check if its nd2.
            }
            q.offer(parent);
            came_from_son_q.offer(true);
        }
        for (int i = 0; i < nd1.children.size(); i++) {
            MyNode child = nd1.children.get(i);
            if (child.equals(nd2)) {
                return false;
            }
            q.offer(child);
            came_from_son_q.offer(false);
        }
        int j = this.nodes_names.indexOf(nd1.name);
        visited[j][0] = true;
        visited[j][1] = true;
        while (!q.isEmpty()) {//traveling in the network in BFS style combined with bayes ball rules.
            MyNode curr_nd = q.poll();
            boolean came_from_son = came_from_son_q.poll();
            boolean is_given = givens.contains(curr_nd.name);
            j = this.nodes_names.indexOf(curr_nd.name);
            if (visited[j][0] && came_from_son) {//if we already came to this node from his children we can skip it to avoid repeating the same actions again.
                continue;
            }
            if (visited[j][1] && !came_from_son) {//if we already came to this node from his parents we can skip it to avoid repeating the same actions again.
                continue;
            }
            /*
            if this node is not given, and we got to him from one of his parents
            we can now go only to his children.
             */
            if (!is_given && !came_from_son) {
                for (int i = 0; i < curr_nd.children.size(); i++) {
                    MyNode child = curr_nd.children.get(i);
                    if (child.equals(nd2)) {
                        return false;
                    }
                    q.offer(child);
                    came_from_son_q.offer(false);
                }
            }
             /*
            if this node is not given, and we got to him from one of his children
            we can now go both to his parents and his children.
             */
            else if (!is_given && came_from_son) {
                for (int i = 0; i < curr_nd.parents.size(); i++) {
                    MyNode parent = curr_nd.parents.get(i);
                    if (parent.equals(nd2)) {
                        return false;
                    }
                    q.offer(parent);
                    came_from_son_q.offer(true);
                }
                for (int i = 0; i < curr_nd.children.size(); i++) {
                    MyNode child = curr_nd.children.get(i);
                    if (child.equals(nd2)) {
                        return false;
                    }
                    q.offer(child);
                    came_from_son_q.offer(false);
                }
            }
             /*
            if this node is given, and we got to him from one of his parents
            we can now go only to his parents.
             */
            else if (is_given && !came_from_son) {
                for (int i = 0; i < curr_nd.parents.size(); i++) {
                    MyNode parent = curr_nd.parents.get(i);
                    if (parent.equals(nd2)) {
                        return false;
                    }
                    q.offer(parent);
                    came_from_son_q.offer(true);
                }
            }
            /*
            if this node is given, and we got to him from one of his children
            we can't go anywhere.
             */
            else if (is_given && came_from_son) {

            }
            if (came_from_son) {
                visited[j][0] = true;
            } else {
                visited[j][1] = true;
            }
        }
        return true;
    }

    /**
     * after splitting the query string, this function will take apart the substring of the query that contains
     * the evidence and will put them in ArrayList.
     *
     * @param query
     * @return - ArrayList of the names of the evidences.
     */

    private static ArrayList<String> getGivens(String query) {
        ArrayList<String> ans = new ArrayList<>();
        String[] subs = query.split(",");
        for (int i = 0; i < subs.length; i++) {
            String[] curr_arr = subs[i].split("=");
            String curr_e = curr_arr[0];
            ans.add(curr_e);
        }
        return ans;
    }

    /**
     * this function gets a query in the form of "P(Q=q|E1=e1, E2=e2, …, Ek=ek) H1-H2-…-Hj" and
     * calculate the answer of it by using variable elimination algorithm.
     *
     * @param query
     * @return - String in the form : "probability,# adding actions,# multiplying actions"
     */

    public String variableElimination(String query) {
        String[] querys_subs = query.split(" ");
        String[] hidden = querys_subs[1].split("-");//[A, E]
        String str = querys_subs[0].substring(2, querys_subs[0].length() - 1);//B=T|J=T,M=T
        querys_subs = str.split("\\|");//[B=T, J=T,M=T] 2 strings
        Hashtable<String, String> names_values = new Hashtable<>();//{J=T, M=T, B=T}
        String q = querys_subs[0].split("=")[0];
        String q_value = querys_subs[0].split("=")[1];
        names_values.put(q, q_value);
        ArrayList<String> e = new ArrayList<>();
        if (querys_subs.length == 2) {
            getNamesAndValues(querys_subs, names_values);
            e = getGivens(querys_subs[1]);
        }
        /*
        at first, we will check if the answer to this query is already given in one of the cpt_tables.
        to check it we will go to the query node and check if the evidence given are equal to his parents.
        if all his parents are given then the answer will appear in his cpt_table.
         */
        boolean in_table = true;
        MyNode q_nd = hs_names_nodes.get(q);
        for (int i = 0; i < e.size(); i++) {
            MyNode curr_nd = hs_names_nodes.get(e.get(i));
            if (!q_nd.parents.contains(curr_nd)) {
                in_table = false;
            }
        }
        if (in_table) {
            Table t = q_nd.cpt_table;
            if (t.nodes_order.size() - 1 == e.size()) {
                ArrayList<String> s = new ArrayList<>();
                for (int j = 0; j < t.nodes_order.size(); j++) {
                    s.add(names_values.get(t.nodes_order.get(j)));
                }
                double prob = t.table.get(s);
                String ans = String.format("%.5f", prob) + ",0,0";
                return ans;
            }
        }
        /*
        next we will go over the hidden nodes and check if they are ancestors of the query or the evidences nodes. if
        they are not then they will be irrelevant, and we can get rid of them.
        then we will check if they are conditionally independent in the query node with the
        given evidence. we will do it by creating new query(in the style that given in the input.txt) and use
        bayes_ball function on it. if the answer will be true we will omit the curr variable because he is
        not relevant to our calculation.
         */
        ArrayList<MyNode> q_e_nodes = new ArrayList<>();
        q_e_nodes.add(q_nd);
        for (int i = 0; i < e.size(); i++) {
            String e_str = e.get(i);
            MyNode e_node = hs_names_nodes.get(e_str);
            q_e_nodes.add(e_node);
        }
        ArrayList<String> relevant_hidden = new ArrayList<>();
        ArrayList<String> irrelevant_hidden = new ArrayList<>();
        for (int i = 0; i < hidden.length; i++) {
            String new_query = q + "-" + hidden[i] + "|" + querys_subs[1];
            if (isAncestor(q_e_nodes, hidden[i]) && !this.bayes_ball(new_query)) {
                relevant_hidden.add(hidden[i]);
            } else {
                irrelevant_hidden.add(hidden[i]);
            }
        }
        ArrayList<String> relevant = new ArrayList<>(relevant_hidden);
        for (int i = 0; i < e.size(); i++) {
            relevant.add(e.get(i));
        }
        /*
        now we will go over the nodes and update their tables if some evidence appears in it.
        therefore, if some cpt_table contains evidence e we can remove all the irrelevant outcomes of e from the table.
         */
        ArrayList<Table> factors = new ArrayList<>();
        for (int i = 0; i < relevant.size(); i++) {
            String curr_name = relevant.get(i);
            MyNode curr_nd = hs_names_nodes.get(curr_name);
            Table new_f = new Table(curr_nd.cpt_table);
            for (int j = 0; j < e.size(); j++) {
                String curr_e = e.get(j);
                if (curr_nd.cpt_table.nodes_order.contains(curr_e)) {
                    evidenceReduce(new_f, curr_e, names_values.get(curr_e));
                }
            }
            if (new_f.table.size() == 1) {
                continue;
            }
            factors.add(new_f);
        }
        /*
        now we will do the same reduce for the query node.
         */
        Table red_q_f = new Table(q_nd.cpt_table);
        for (int j = 0; j < e.size(); j++) {
            String curr_e = e.get(j);
            if (red_q_f.nodes_order.contains(curr_e)) {
                evidenceReduce(red_q_f, curr_e, names_values.get(curr_e));
            }
        }
        /*
        the next step will be to go over the relevant factors and preform join and elimination on them in the given order.
         */
        factors.add(red_q_f);
        factors.sort(Table::compareTo);
        AtomicInteger mul_counter = new AtomicInteger();
        AtomicInteger add_counter = new AtomicInteger();
        for (int i = 0; i < relevant_hidden.size(); i++) {
            String curr_name = relevant_hidden.get(i);
            while (true) {
                Table f1 = null;
                Table f2 = null;
                for (int j = 0; j < factors.size(); j++) {
                    Table curr_factor = factors.get(j);
                    if (curr_factor.nodes_order.contains(curr_name)) {
                        if (f1 == null) {
                            f1 = curr_factor;
                        } else {
                            f2 = curr_factor;
                            break;
                        }
                    }
                }
                if (f1 != null && f2 != null) {
                    factors.remove(f1);
                    factors.remove(f2);
                    Table new_factor = join(f1, f2, hs_names_nodes, mul_counter);
                    factors.add(new_factor);
                    factors.sort(Table::compareTo);
                } else {
                    factors.remove(f1);
                    Table new_factor = elimination(f1, curr_name, add_counter);
                    factors.add(new_factor);
                    if (new_factor.table.size() == 1) {
                        break;
                    }
                    factors.sort(Table::compareTo);
                    break;
                }
            }
        }
        /*
        now we will preform join and elimination on the query node until we will have only 1 factor that contains him.
         */
        while (true) {
            Table f1 = null;
            Table f2 = null;
            for (int j = 0; j < factors.size(); j++) {
                Table curr_factor = factors.get(j);
                if (curr_factor.nodes_order.contains(q)) {
                    if (f1 == null) {
                        f1 = curr_factor;
                    } else {
                        f2 = curr_factor;
                        break;
                    }
                }
            }
            if (f1 != null && f2 != null) {
                factors.remove(f1);
                factors.remove(f2);
                Table new_factor = join(f1, f2, hs_names_nodes, mul_counter);
                factors.add(new_factor);
                factors.sort(Table::compareTo);
            } else {
                break;
            }
        }
        /*
        Finally, to get the answer to the query we need to normalize the values we got so far.
        to do that we will sum the values of each outcome and divide the probability of the wanted
        outcome in the sum.
         */
        Table f = new Table();
        for (int i = 0; i < factors.size(); i++) {
            Table curr_f = factors.get(i);
            if (curr_f.nodes_order.contains(q)) {
                f = curr_f;
                break;
            }
        }
        ArrayList<Double> x = new ArrayList<>(f.table.values());
        double sum = x.get(0);
        for (int i = 1; i < x.size(); i++) {
            sum += x.get(i);
            add_counter.addAndGet(1);
        }
        String wanted_value = names_values.get(q);
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add(wanted_value);
        double y = f.table.get(tmp);
        double prob = y / sum;
        String ans = String.format("%.5f", prob) + "," + add_counter + "," + mul_counter;
        return ans;
    }

    /**
     * After splitting the query string we got, this function take apart each substring and
     * extracting from it the name and the value of the query and the name and value of each evidence
     * and then adding it to a Hashtable.
     *
     * @param x
     * @param names_values
     */

    private static void getNamesAndValues(String[] x, Hashtable<String, String> names_values) {
        String[] e = x[1].split(",");
        for (int i = 0; i < e.length; i++) {
            String[] curr_e = e[i].split("=");
            names_values.put(curr_e[0], curr_e[1]);
        }
    }

    /**
     * this function check if hidden node is ancestor of the query or evidences nodes.
     *
     * @param q_e_nodes - list of the query and evidences nodes.
     * @param hidden    - the name of the hidden node we want to check.
     * @return
     */

    private boolean isAncestor(ArrayList<MyNode> q_e_nodes, String hidden) {
        for (int i = 0; i < q_e_nodes.size(); i++) {
            MyNode curr = q_e_nodes.get(i);
            Queue<MyNode> q = new LinkedList<>();
            q.add(curr);
            while (!q.isEmpty()) {
                MyNode curr_nd = q.remove();
                for (int j = 0; j < curr_nd.parents.size(); j++) {
                    MyNode parent = curr_nd.parents.get(j);
                    if (parent.name.equals(hidden)) {
                        return true;
                    }
                    q.add(parent);
                }
            }
        }
        return false;
    }

    /**
     * this function gets a CPT table that contains some evidence e in it, and remove all the irrelevant
     * rows. that means it will remove all the row in which e != value.
     *
     * @param f     - factor, the CPT table we want to reduce.
     * @param e     - the evidence we need to remove from this table.
     * @param value - the only value of e we want to stay in this table.
     */
    private void evidenceReduce(Table f, String e, String value) {
        int index = f.nodes_order.indexOf(e);
        Set<ArrayList<String>> keys = new HashSet<>(f.table.keySet());
        for (ArrayList<String> key : keys) {
            if (!key.get(index).equals(value)) {
                f.table.remove(key);
            } else {
                double prob = f.table.get(key);
                f.table.remove(key);
                key.remove(index);
                ArrayList<String> new_key = new ArrayList<>(key);
                f.table.put(new_key, prob);
            }
        }
        f.nodes_order.remove(e);
    }

    /**
     * this function create a new CPT table by joining 2 given CPT tables.
     * the function join the tables by multiplying the row that contains the same value for the common variables.
     *
     * @param f1          - factor, first given table.
     * @param f2          - factor, second given table.
     * @param names_nodes - the Hashtable of the Network.
     * @return - new factor, new CPT table.
     */
    private static Table join(Table f1, Table f2, Hashtable<String, MyNode> names_nodes, AtomicInteger mul_counter) {
        Table ans = new Table();
        ArrayList<String> common_nodes = new ArrayList();
        ArrayList<MyNode> nd_list = new ArrayList<>();
        for (int i = 0; i < f1.nodes_order.size(); i++) {
            String curr_str = f1.nodes_order.get(i);
            ans.nodes_order.add(curr_str);
            nd_list.add(names_nodes.get(curr_str));
        }
        for (int i = 0; i < f2.nodes_order.size(); i++) {
            String curr_str = f2.nodes_order.get(i);
            if (!ans.nodes_order.contains(curr_str)) {
                ans.nodes_order.add(curr_str);
                nd_list.add(names_nodes.get(curr_str));
            } else {
                common_nodes.add(curr_str);
            }
        }
        ArrayList<ArrayList<String>> values_table = new ArrayList<>();
        Table.Combinations(nd_list, values_table);
        double[] probs = new double[values_table.size()];

        for (int i = 0; i < values_table.size(); i++) {
            ArrayList<String> curr_row = values_table.get(i);
            ArrayList<String> f1_row = new ArrayList<>();
            for (int j = 0; j < f1.nodes_order.size(); j++) {
                String curr_node_name = f1.nodes_order.get(j);//E
                int index = ans.nodes_order.indexOf(curr_node_name);// index of E in ans
                String value = curr_row.get(index);
                f1_row.add(value);
            }
            ArrayList<String> f2_row = new ArrayList<>();
            for (int j = 0; j < f2.nodes_order.size(); j++) {
                String curr_node_name = f2.nodes_order.get(j);//E
                int index = ans.nodes_order.indexOf(curr_node_name);// index of E in ans
                String value = curr_row.get(index);
                f2_row.add(value);
            }
            double prob1 = f1.table.get(f1_row);
            double prob2 = f2.table.get(f2_row);
            probs[i] = prob1 * prob2;
            mul_counter.addAndGet(1);
        }

        for (int i = 0; i < probs.length; i++) {
            ArrayList<String> curr_str = values_table.get(i);
            double curr_prob = probs[i];
            ans.table.put(curr_str, curr_prob);
        }
        return ans;
    }

    /**
     * this function gets a CPT table and remove the variable "name" of it by summing the rows
     * that contains the same value for the rest of the variables.
     *
     * @param f    - factor, the given CPT table.
     * @param name - the name of the node in the Network we want to eliminate from this table.
     * @return - new factor, new CPT table.
     */
    private static Table elimination(Table f, String name, AtomicInteger add_counter) {
        Table ans = new Table();
        for (int i = 0; i < f.nodes_order.size(); i++) {
            String curr_name = f.nodes_order.get(i);
            if (!curr_name.equals(name)) {
                ans.nodes_order.add(curr_name);
            }
        }
        int index = f.nodes_order.indexOf(name);
        Set<ArrayList<String>> curr_keys = new HashSet<>(f.table.keySet());
        for (ArrayList<String> key : curr_keys) {
            ArrayList<String> new_key = new ArrayList<>(key);
            new_key.remove(index);
            Double prob = f.table.get(key);
            if (ans.table.containsKey(new_key)) {
                double new_prob = prob + ans.table.get(new_key);
                ans.table.put(new_key, new_prob);
                add_counter.addAndGet(1);
            } else {
                ans.table.put(new_key, prob);
            }
        }
        return ans;
    }


    @Override
    public String toString() {
        return "Network{\n" +
                hs_names_nodes +
                '}';
    }
}
