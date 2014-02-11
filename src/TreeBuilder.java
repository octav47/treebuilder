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
            newick(prefix + "output.txt", tree);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        rawTreeListOfNodes.clear();
        PhyShow.main(new String[]{});
    }

    private static void newick(String fileName, Tree<String> tree) throws FileNotFoundException {
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
    }

}
