import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Kir on 17.02.14.
 */
public class UnWeightedUniFracCompare {

    public static void main(String[] args) throws IOException {
        String a = args[0];
        String b = args[1];
        double u = 0.0;
        double L = 0.0;
        final File treefile1 = new File("treesNewicks/" + a + ".txt");
        final File treefile2 = new File("treesNewicks/" + b + ".txt");
        PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(treefile1, true);
        final Phylogeny phy1 = PhylogenyMethods.readPhylogenies(parser, treefile1)[0];
        parser = ParserUtils.createParserDependingOnFileType(treefile2, true);
        final Phylogeny phy2 = PhylogenyMethods.readPhylogenies(parser, treefile2)[0];
        String compareResultFileName = "resultCompare/" + a + "_comp_" + b + ".txt";
        if (isNullTree(phy1) || isNullTree(phy2)) {
            System.out.println(a + " or " + b + " are null-trees");
            result = -1;
        } else {
            int s = 0;
            List<PhylogenyNode> leafs = phy1.getExternalNodes();
            double maxDepth = 0;
            for (PhylogenyNode node : leafs) {
                double curDepth = node.calculateDepth();
                if (curDepth > maxDepth) {
                    maxDepth = curDepth;
                }
            }
            System.out.println(maxDepth);
            for (PhylogenyNodeIterator it = phy1.iteratorPostorder(); it.hasNext(); ) {
                PhylogenyNode n1 = it.next();
                PhylogenyNode n2 = phy2.getNode(n1.toString());
                double n1Dist = (n1.getDistanceToParent() > EPS) ? 1 : 0;
                double n2Dist = (n2.getDistanceToParent() > EPS) ? 1 : 0;
                int depth = n1.calculateDepth() + 1;
                u += depth * Math.abs(n1Dist - n2Dist);
                L += depth * Math.max(n1Dist, n2Dist);

            }
            u /= L;
            System.out.println("u = " + u);
        }
        printResult(compareResultFileName, u);
    }

    private static void printResult(String fileName, double u) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(fileName);
        pw.print(u);
        pw.close();
    }

    private static boolean isNullTree(Phylogeny phy) {
        for (PhylogenyNodeIterator it = phy.iteratorPostorder(); it.hasNext(); ) {
            if (it.next().getDistanceToParent() > EPS) {
                return false;
            }
        }
        return true;
    }
}
