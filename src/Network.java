import java.util.*;

public class Network {
    //    public LinkedList<String>[] edges;
    public ArrayList<String> nodes_names;
    public ArrayList<MyNode> nodes;
    Hashtable<String,MyNode> hs = new Hashtable<>();

    public Network() {
        nodes = new ArrayList<>();
        nodes_names = new ArrayList<>();
        hs = new Hashtable<>();

    }

    public MyNode getNode(String name) {
        int nd_index = this.nodes_names.indexOf(name);
        MyNode nd = this.nodes.get(nd_index);
        return nd;
    }

    public boolean bayes_ball(String query) {
        String[] x = query.split("\\|");
        String[] vertices = x[0].split("-");// an array of the 2 vertices that we want to check.
        ArrayList<String> givens = new ArrayList<>();
        if (x.length == 2) {
            givens = getGivens((x[1]));// if there are given evidence we will save them in ArrayList.
        }
        MyNode nd1 = getNode(vertices[0]);
        MyNode nd2 = getNode(vertices[1]);
        Queue<MyNode> q = new LinkedList<>();// a queue that will hold the nodes we need to visit.
        Queue<Boolean> came_from_son_q = new LinkedList<>();// a parallel queue to the one above that hold true if we got to this node from his children and false otherwise.
        boolean[][] visited = new boolean[this.nodes.size()][2];// 2D array the first column says if we already got to this node from his children and the 2 column says if we already got to this node from his parent.
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
        int j = this.nodes.indexOf(nd1);
        visited[j][0] = true;
        visited[j][1] = true;
        while (!q.isEmpty()) {//traveling in the network in BFS style combined with bayes ball rules.
            MyNode curr_nd = q.poll();
            boolean came_from_son = came_from_son_q.poll();
            boolean is_given = givens.contains(curr_nd.name);
            j = this.nodes.indexOf(curr_nd);
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

    private static ArrayList<String> getGivens(String query) {
        ArrayList<String> ans = new ArrayList<>();
        String given = "";
        int i = 0;
        while (i < query.length()) {
            if (query.charAt(i) == '=') {
                ans.add(given);
                i += 3;
                given = "";
                if (i >= query.length()) {
                    break;
                }
            }
            given += query.charAt(i);
            i++;
        }
        return ans;
    }

    public String variableElimination(String query) {
        String[] querys_subs = query.split(" ");
        String[] hidden = querys_subs[1].split("-");//[A, E]
        String str = querys_subs[0].substring(2, querys_subs[0].length() - 1);//B=T|J=T,M=T
        querys_subs = str.split("\\|");//[B=T, J=T,M=T] 2 strings
        Hashtable<String, String> names_values = new Hashtable<>();//{J=T, M=T, B=T}
        getNamesAndValues(querys_subs,names_values);
        String q = querys_subs[0].substring(0,1);
        ArrayList<String> e = getGivens(querys_subs[1]);
        /*
        at first, we will check if the answer to this query is already given in one of the cpt_tables.
        to check it we will go to the query node and check if the evidence given are equal to his parents.
        if all his parents are given then the answer will appear in his cpt_table.
         */
        boolean in_table = true;
        MyNode q_nd = hs.get(q);
        for (int i = 0; i < e.size(); i++) {
            MyNode curr_nd = hs.get(e.get(i));
            if(!q_nd.parents.contains(curr_nd)){
                in_table = false;
            }
        }
        if(in_table){
            Table t = q_nd.cpt_table;
            if(t.nodes_order.size()-1 == e.size()){
                String s="";
                for (int j = 0; j < t.nodes_order.size(); j++) {
                    s += names_values.get(t.nodes_order.get(j));
                }
                double prob = t.table.get(s);
                String ans = prob+",0,0";
                System.out.println(ans);
                return ans;
            }
        }
        /*
        next we will go over the hidden nodes and check if they are conditionally independent in the query node with the
        given evidence. we will do it by creating new query(in the style that given in the input.txt) and use
        bayes_ball function on it. if the answer will be true we will omit the curr variable because he is
        not relevant to our calculation.
         */
        ArrayList<String> relevant_hidden = new ArrayList<>();
        for (int i = 0; i < hidden.length; i++) {
            String new_query = q+"-"+hidden[i]+"|"+querys_subs[1];
            System.out.println(new_query);
            System.out.println(this.bayes_ball(new_query));
            if(!this.bayes_ball(new_query)){
                relevant_hidden.add(hidden[i]);
            }
        }

        Table t = join(hs.get("B").cpt_table,hs.get("A").cpt_table,hs);
        elimination(t,"B");

//        System.out.println(Arrays.toString(hiddens) + "\n" + str + "\n" + Arrays.toString(x) + "\n" + names_values + "\n" + names_roles);


        return "";
    }


    private static void getNamesAndValues(String[] x, Hashtable<String, String> names_values) {
        String[] q = x[0].split("=");
        names_values.put(q[0], q[1]);
        String[] e = x[1].split(",");
        for (int i = 0; i < e.length; i++) {
            String[] curr_e = e[i].split("=");
            names_values.put(curr_e[0], curr_e[1]);
        }
    }

    private static Table join(Table f1, Table f2, Hashtable<String,MyNode> names_nodes) {
        Table ans =  new Table();
        ArrayList<String> common_nodes = new ArrayList();
        ArrayList<MyNode> nd_list = new ArrayList<>();
        for (int i = 0; i < f1.nodes_order.size(); i++) {
            String curr_str = f1.nodes_order.get(i);
            ans.nodes_order.add(curr_str);
            nd_list.add(names_nodes.get(curr_str));
        }
        for (int i = 0; i < f2.nodes_order.size(); i++) {
            String curr_str = f2.nodes_order.get(i);
            if(!ans.nodes_order.contains(curr_str)){
                ans.nodes_order.add(curr_str);
                nd_list.add(names_nodes.get(curr_str));
            }
            else {
                common_nodes.add(curr_str);
            }
        }
        ArrayList<String> values_table = new ArrayList<>();
        Table.Combinations(nd_list, values_table);
        System.out.println(values_table);
        double[] probs = new double[values_table.size()];

        for (int i = 0; i < values_table.size(); i++) {
            String curr_str = values_table.get(i);
            String f1_str = "";
            for (int j = 0; j < f1.nodes_order.size(); j++) {
                String curr_node_name = f1.nodes_order.get(j);//E
                int index = ans.nodes_order.indexOf(curr_node_name);// index of E in ans
                String value = curr_str.charAt(index)+"";
                MyNode curr_node = names_nodes.get(curr_node_name);
                if(!curr_node.outcomes.contains(value)){
                    value+=curr_str.charAt(index+1);
                }
                f1_str += value;
            }
            String f2_str = "";
            for (int j = 0; j < f2.nodes_order.size(); j++) {
                String curr_node_name = f2.nodes_order.get(j);//E
                int index = ans.nodes_order.indexOf(curr_node_name);// index of E in ans
                String value = curr_str.charAt(index)+"";
                MyNode curr_node = names_nodes.get(curr_node_name);
                if(!curr_node.outcomes.contains(value)){
                    value+=curr_str.charAt(index+1);
                }
                f2_str += value;
            }
            double prob1 = f1.table.get(f1_str);
            double prob2 = f2.table.get(f2_str);
            probs[i] = prob1*prob2;
        }
        System.out.println(ans.nodes_order);
        System.out.println(Arrays.toString(probs));
        for (int i = 0; i < probs.length; i++) {
            String curr_str = values_table.get(i);
            double curr_prob = probs[i];
            ans.table.put(curr_str,curr_prob);
        }
        System.out.println(ans);
        return ans;
    }

    private static Table elimination(Table f, String name) {
        Table ans = new Table();
        for (int i = 0; i < f.nodes_order.size(); i++) {
            String curr_name = f.nodes_order.get(i);
            if(!curr_name.equals(name)){
                ans.nodes_order.add(curr_name);
            }
        }
        int index = f.nodes_order.indexOf(name);
        Set<String> curr_keys = new HashSet<>(f.table.keySet());
        for (String key : curr_keys){
            String new_key = key.substring(0,index);
            if(index+1 != key.length()){
                new_key += key.substring(index+1);
            }
            Double prob = f.table.get(key);
            if (ans.table.containsKey(new_key)){
                double new_prob = prob + ans.table.get(new_key);
                ans.table.put(new_key,new_prob);
            }
            else{
                ans.table.put(new_key,prob);
            }
        }
        System.out.println(ans);
        return ans;
    }


        @Override
        public String toString () {
            return "Network{\n" +
                    nodes +
                    '}';
        }

        public static void main (String[]args){

        }
    }
