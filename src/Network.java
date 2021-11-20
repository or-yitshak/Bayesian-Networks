import java.util.*;

public class Network {
    //    public LinkedList<String>[] edges;
    public ArrayList<String> nodes_names;
    public ArrayList<MyNode> nodes;

    public Network() {
        nodes = new ArrayList<>();
        nodes_names = new ArrayList<>();
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
        String[] x = query.split(" ");
        String[] hiddens = x[1].split("-");//[A, E]
        String str = x[0].substring(2, x[0].length() - 1);//B=T|J=T,M=T
        x = str.split("\\|");//[B=T, J=T,M=T] 2 strings
        Hashtable<String, String> names_values = new Hashtable<>();//{J=T, M=T, B=T}
        Hashtable<String, String> names_roles = new Hashtable<>();//{J=E, M=E, B=Q}
        getNamesAndValues(x,names_values,names_roles);



        System.out.println(Arrays.toString(hiddens) + "\n" + str + "\n" + Arrays.toString(x) + "\n" + names_values + "\n" + names_roles);


        return "";
    }


    private static void getNamesAndValues(String[] x, Hashtable<String, String> names_values, Hashtable<String, String> names_roles) {
        String[] q = x[0].split("=");
        names_values.put(q[0], q[1]);
        names_roles.put(q[0],"Q");
        String[] e = x[1].split(",");
        for (int i = 0; i < e.length; i++) {
            String[] curr_e = e[i].split("=");
            names_values.put(curr_e[0], curr_e[1]);
            names_roles.put(curr_e[0],"E");
        }
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
