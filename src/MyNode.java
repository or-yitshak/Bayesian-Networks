import java.util.ArrayList;

public class MyNode {
    public String name;
    public ArrayList<String> outcomes;
    public ArrayList<MyNode> parents;
    public ArrayList<MyNode> children;
    public double[] cpt_table;//should be a class.

    public MyNode(String name) {
        this.name = name;
        outcomes = new ArrayList<>();
        parents = new ArrayList<>();
        children = new ArrayList<>();
    }

    @Override
    public String toString() {
        String str = "MyNode{" + "name='" + name + ", parents= [";
        if (parents.size() == 0) {
            str += "]";
        } else {
            for (int i = 0; i < parents.size(); i++) {
                if (i == parents.size() - 1) {
                    str += parents.get(i).name + "]";
                    break;
                }
                str += parents.get(i).name + ", ";
            }
        }
        str += ", children= [";
        if (children.size() == 0) {
            str += "]";
        } else {
            for (int i = 0; i < children.size(); i++) {
                if (i == children.size() - 1) {
                    str += children.get(i).name + "]";
                    break;
                }
                str += children.get(i).name + ", ";
            }
        }
        str += "}\n";


        return str;
    }
}
