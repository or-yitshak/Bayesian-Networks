import java.util.ArrayList;

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

    @Override
    public String toString() {
        return "Network{\n" +
                nodes +
                '}';
    }
}
