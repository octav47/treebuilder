import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

public class NewickTreeBuilder {

    private static PrintWriter pwn = null;
    
    private static LinkedHashMap<String, ArrayList<String>> pro2pepList = new LinkedHashMap<String, ArrayList<String>>();
    
    private static LinkedHashMap<String, ArrayList<String>> rawTreeListOfNodes = new LinkedHashMap<String, ArrayList<String>>();
    
    public static void main(String[] args) throws FileNotFoundException {
        String fileName = "proteins_tree.csv";
        long before = System.currentTimeMillis();
        BufferedReader bf;
        PrintWriter pw;

        Tree<String> tree = new Tree<String>("root");
        TreeSet<String> rooted = new TreeSet<String>();

        try {
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String bfCurLine = bf.readLine();
            while ((bfCurLine = bf.readLine()) != null) {
                String[] ch = bfCurLine.split("\";\"");
                for (int i = 0; i < ch.length; i++) {
                    ch[i] = ch[i].replace('(', '[');
                    ch[i] = ch[i].replace(')', ']');
                    ch[i] = ch[i].replace(' ', '_');
                    ch[i] = ch[i].replace("\'", "");
                    ch[i] = ch[i].replace("\"", "");
                    ch[i] = ch[i].replace(":", "_");
                }
                int lastIndexToAdd = ch.length - 1;
                for (int i = 2; i < ch.length; i++) {
                    String curLeaf = ch[i];
                    if (curLeaf.equalsIgnoreCase("")) {
                        lastIndexToAdd = i - 1;
                        break;
                    }
                    String curRoot = ch[i - 1];
                    if (!rooted.contains(curLeaf)) {
                        tree.addLeaf(curRoot, curLeaf);
                        rooted.add(curLeaf);
                    }
                }
                if (!rawTreeListOfNodes.containsKey(ch[lastIndexToAdd])) {
                    ArrayList<String> a = new ArrayList<String>();
                    a.add(ch[0]);
                    rawTreeListOfNodes.put(ch[lastIndexToAdd], a);
                } else {
                    ArrayList<String> a = rawTreeListOfNodes.get(ch[lastIndexToAdd]);
                    a.add(ch[0]);
                    rawTreeListOfNodes.put(ch[lastIndexToAdd], a);
                }
            }
            bf.close();
            rooted.clear();
            rooted = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String curLine;
            bf = new BufferedReader(new InputStreamReader(new FileInputStream("parseProtPept.txt")));
            while ((curLine = bf.readLine()) != null) {
                String[] ch = curLine.split(";");
                ArrayList<String> a = new ArrayList<String>();
                a.addAll(Arrays.asList(ch).subList(1, ch.length));
                pro2pepList.put(ch[0], a);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            newick(prefix + "output.txt", tree, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        rawTreeListOfNodes.clear();
        PhyShow.main(new String[]{});
    }

    private static void newick(String fileName, Tree<String> tree, boolean isIntensity) throws FileNotFoundException {
        TreeSet<String> used = new TreeSet<String>();
        pwn = new PrintWriter(fileName);
        dfs(tree, used);
        pwn.print(";");
        pwn.close();
        pwn = null;
        used.clear();
        used = null;
    }

    private static TreeMap<String, Double> weights = new TreeMap<String, Double>();

    private static void dfs(Tree<String> tree, TreeSet<String> used) {
        int tl = tree.getSubTrees().size();
        if (tl > 0) pwn.print("(");
        String w = tree.getHead();
        used.add(w);
        Collection<Tree<String>> st = tree.getSubTrees();
        int i = 0;
        for (Tree<String> ct : st) {
            if (!used.contains(ct.getHead())) {
                dfs(ct, used);
            }
            if (i != st.size() - 1) pwn.print(",");
            i++;
        }
        if (tl > 0) {
            pwn.print(")");
        }
        pwn.print(w);
        double curw = countWeight(w);
        pwn.print(":" + Double.toString(curw));
    }

    private static double countIntensity(double weight, double max_weight) {
        return (weight * 100) / max_weight;
    }

    private static double countWeight(String node) {
        double tmp = 0.0;
        if (!rawTreeListOfNodes.containsKey(node)) return 0;
        ArrayList<String> a = rawTreeListOfNodes.get(node);
        for (String s : a) {
            if (pro2pepList.containsKey(s)) {
                ArrayList<String> asd = pro2pepList.get(s);
                if (asd.size() == 1) {
                    tmp++;
                }
            }
        }
        return tmp;
    }
}
