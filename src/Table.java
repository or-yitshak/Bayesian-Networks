import java.util.ArrayList;

public class Table {
    String[] nodes_in;
    String[][] table; //row as the length of the double list and cols as the number of nodes_in
    double[] probs;

    public Table(String[] nums ,MyNode nd){
        nodes_in = new String[nd.parents.size()+1];
        for (int i = 0; i <nd.parents.size(); i++) {
            nodes_in[i] = nd.parents.get(i).name;
        }
        nodes_in[nodes_in.length-1] = nd.name;

        table = new String[nums.length][nodes_in.length];
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nodes_in.length; j++) {
                if(j==0 && i<=nums.length/2){table[0][0]="t";}
            }

        }




        probs = new double[nums.length];
        for (int i = 0; i < nums.length; i ++) {
            probs[i] = Double.parseDouble(nums[i]);
        }

    }


}
