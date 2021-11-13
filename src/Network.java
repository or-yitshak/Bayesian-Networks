import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
        String[] vertices = x[0].split("-");
        ArrayList<String> givens = new ArrayList<>();
        if (x.length==2){
            givens = getGivens((x[1]));
        }
        MyNode nd1 = getNode(vertices[0]);
        MyNode nd2 = getNode(vertices[1]);
        Queue<MyNode> q = new LinkedList<>();
        Queue<Boolean> came_from_son_q =new LinkedList<>();
        for (int i = 0; i < nd1.parents.size() ; i++) {
            MyNode parent = nd1.parents.get(i);
            if(parent.equals(nd2)){
                return false;
            }
            q.offer(parent);
            came_from_son_q.offer(true);
        }
        for (int i = 0; i < nd1.children.size() ; i++) {
            MyNode child = nd1.children.get(i);
            if(child.equals(nd2)){
                return false;
            }
            q.offer(child);
            came_from_son_q.offer(false);
        }
        while(!q.isEmpty()){
            MyNode curr_nd = q.poll();
            boolean came_from_son = came_from_son_q.poll();
            boolean is_given = givens.contains(curr_nd.name);
            /*
            if this node is not given, and we got to him from one of his parents
            we can now go only to his children.
             */
            if(!is_given && !came_from_son){
                for (int i = 0; i < curr_nd.children.size(); i++) {
                    MyNode child = curr_nd.children.get(i);
                    if(child.equals(nd2)){
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
            else if(!is_given && came_from_son){
                for (int i = 0; i < curr_nd.parents.size(); i++) {
                    MyNode parent = curr_nd.parents.get(i);
                    if(parent.equals(nd2)){
                        return false;
                    }
                    q.offer(parent);
                    came_from_son_q.offer(true);
                }
                for (int i = 0; i < curr_nd.children.size(); i++) {
                    MyNode child = curr_nd.children.get(i);
                    if(child.equals(nd2)){
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
            else if(is_given && !came_from_son){
                for (int i = 0; i < curr_nd.parents.size(); i++) {
                    MyNode parent = curr_nd.parents.get(i);
                    if(parent.equals(nd2)){
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
            else if(is_given && came_from_son){
                continue;
            }
        }
        return true;
    }

    private static ArrayList<String> getGivens(String query) {
        ArrayList<String> ans = new ArrayList<>();
        String given = "";
        int i = 0;
        while (i<query.length()) {
            if(query.charAt(i)=='='){
                ans.add(given);
                i+=3;
                given = "";
                if(i>=query.length()){break;}
            }
            given += query.charAt(i);
            i++;
        }
        return ans;
    }


    @Override
    public String toString() {
        return "Network{\n" +
                nodes +
                '}';
    }

    public static void main(String[] args) {

    }
}
