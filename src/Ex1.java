import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Scanner;

public class Ex1 {

    public static void main(String[] args) {
        Network net = new Network();

        readTxtFile("input.txt", net);
        System.out.println(net);


    }

    public static void readTxtFile(String file, Network net) {
        try {
            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            String xml_name = myReader.nextLine();
            myReadXmlFile(xml_name, net);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                /*
                add what you want to do with the given line.create an array of queries.
                 */
                System.out.println(data);
            }
            myReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void myReadXmlFile(String xml_name, Network net) {
        try {
            File myObj = new File(xml_name);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                /*
                add what you want to do with the given line.
                 */
                if (data.equals("") || data.equals("<VARIABLE>") || data.equals("</VARIABLE>") || data.equals("<NETWORK>") || data.equals("<DEFINITION>") || data.equals("</DEFINITION>")) {
                    continue;
                }
//                if(data.contains("\t")){
//                    data = data.substring(1);
//                }
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
                    double[] table = toDoubletArray(str_table);
                    curr_nd.cpt_table = table;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getData(String data) {
//        char[] chars = data.toCharArray();

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

    private static double[] toDoubletArray(String[] strs) {
        double[] arr = new double[strs.length];
        for (int i = 0; i < strs.length; i += 2) {
            arr[i] = Double.parseDouble(strs[i]);
        }
        return arr;
    }


    private static void readXmlFile(String data) {

        try {
//creating a constructor of file class and parsing an XML file
            File file = new File(data);
//an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("student");
// nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) node;
//                    System.out.println("Student id: "+ eElement.getElementsByTagName("id").item(0).getTextContent());
//                    System.out.println("First Name: "+ eElement.getElementsByTagName("firstname").item(0).getTextContent());
//                    System.out.println("Last Name: "+ eElement.getElementsByTagName("lastname").item(0).getTextContent());
//                    System.out.println("Subject: "+ eElement.getElementsByTagName("subject").item(0).getTextContent());
//                    System.out.println("Marks: "+ eElement.getElementsByTagName("marks").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        // Instantiate the Factory
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//
//        try {
//
//            // optional, but recommended
//            // process XML securely, avoid attacks like XML External Entities (XXE)
//            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
//
//            // parse XML file
//            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            Document doc = db.parse(new File(data));
//
//            // optional, but recommended
//            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
//            doc.getDocumentElement().normalize();
//
//            System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
//            System.out.println("------");
//
//            // get <staff>
//            NodeList list = doc.getElementsByTagName("staff");
//
//            for (int temp = 0; temp < list.getLength(); temp++) {
//
//                Node node = list.item(temp);
//
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//
//                    Element element = (Element) node;
//
//                    // get staff's attribute
//                    String id = element.getAttribute("id");
//
//                    // get text
//                    String firstname = element.getElementsByTagName("firstname").item(0).getTextContent();
//                    String lastname = element.getElementsByTagName("lastname").item(0).getTextContent();
//                    String nickname = element.getElementsByTagName("nickname").item(0).getTextContent();
//
//                    NodeList salaryNodeList = element.getElementsByTagName("salary");
//                    String salary = salaryNodeList.item(0).getTextContent();
//
//                    // get salary's attribute
//                    String currency = salaryNodeList.item(0).getAttributes().getNamedItem("currency").getTextContent();
//
//                    System.out.println("Current Element :" + node.getNodeName());
//                    System.out.println("Staff Id : " + id);
//                    System.out.println("First Name : " + firstname);
//                    System.out.println("Last Name : " + lastname);
//                    System.out.println("Nick Name : " + nickname);
//                    System.out.printf("Salary [Currency] : %,.2f [%s]%n%n", Float.parseFloat(salary), currency);
//
//                }
//            }
//
//        } catch (ParserConfigurationException | SAXException | IOException e) {
//            e.printStackTrace();
//        }
    }


}
