import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * this is the main class of this program. it reads txt file named "input.txt" and create a Network object from the
 * XML file that appears in the first line in "input.txt". then it reads the queries and answer them by using the
 * Network functions. At the end it writes the answers into txt file named "output.txt"
 */

public class Ex1 {

    public static void main(String[] args) {
        Network net = new Network();
        ArrayList<String> queries = new ArrayList<>();
        readTxtFile("input.txt", net, queries);
//        System.out.println(net);
//        System.out.println(queries);
        String[] answers = new String[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String query = queries.get(i);
            if (query.charAt(0) == 'P') {
                answers[i] = net.variableElimination(query);
            } else {
                if (net.bayes_ball(query)) {
                    answers[i] = "yes";
                } else {
                    answers[i] = "no";
                }
//                System.out.println(net.bayes_ball(query));
            }
        }
        for (int i = 0; i < net.nodes.size(); i++) {
//            System.out.println(net.nodes.get(i).cpt_table);
        }
        try {
            FileWriter myWriter = new FileWriter("output.txt");
            for (int i = 0; i < answers.length; i++) {
                if (i < answers.length - 1) {
                    myWriter.write(answers[i] + "\n");
                } else {
                    myWriter.write(answers[i]);
                }

            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * this function go over the "input.txt" file extract the information it contains.
     *
     * @param file
     * @param net
     * @param queries
     */

    public static void readTxtFile(String file, Network net, ArrayList<String> queries) {
        try {
            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            String xml_name = myReader.nextLine();
            myReadXmlFile(xml_name, net);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                queries.add(data);
            }
            myReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this function go over the aml file given in "input.txt" file, extracting the data it contains
     * and initialize the Network object with this data.
     *
     * @param xml_name
     * @param net
     */

    private static void myReadXmlFile(String xml_name, Network net) {
        try {
            File myObj = new File(xml_name);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.equals("") || data.equals("<VARIABLE>") || data.equals("</VARIABLE>") || data.equals("<NETWORK>") || data.equals("<DEFINITION>") || data.equals("</DEFINITION>")) {
                    continue;
                }
                if (data.contains("<NAME>")) {
                    String name = getData(data);
                    MyNode nd = new MyNode(name);
                    data = myReader.nextLine();
                    while (!data.equals("</VARIABLE>")) {
                        String outcome = getData(data);
                        nd.outcomes.add(outcome);
                        data = myReader.nextLine();
                    }
                    net.nodes.add(nd);
                    net.nodes_names.add(name);
                    net.hs_names_nodes.put(name, nd);
                }
                if (data.contains("<FOR>")) {
                    String name = getData(data);
                    MyNode curr_nd = net.getNode(name);
                    data = myReader.nextLine();
                    while (data.contains("<GIVEN>")) {
                        String parent_name = getData(data);
                        MyNode parent = net.getNode(parent_name);
                        curr_nd.parents.add(parent);
                        parent.children.add(curr_nd);
                        data = myReader.nextLine();
                    }
                    String str = getData(data);
                    String[] str_table = str.split(" ");
                    Table t = new Table(str_table, curr_nd);
                    curr_nd.cpt_table = t;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this function extract the data given in some line in the xml file.
     *
     * @param data - string that contains data in the form "<TYPE>data</TYPE>"
     * @return
     */

    private static String getData(String data) {
        int start, end;
        int i = 0;
        while (data.charAt(i) != '>') {
            i++;
        }
        start = ++i;
        while (data.charAt(i) != '<') {
            i++;
        }
        end = i;
        String ans = data.substring(start, end);
        return ans;
    }
}
