import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.Configuration;
import org.forester.archaeopteryx.Options;
import org.forester.archaeopteryx.TreeColorSet;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.BranchColor;
import org.forester.phylogeny.data.NodeVisualization;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import support.SubFuntions;

import java.awt.*;
import java.io.File;

public class PhyShow {

    private static String fileInput = "output.txt";

    public static void main(String[] args) {
        try {
            gradient = SubFuntions.getGradient();
            File treeFile = null;
            treeFile = new File(fileInput);

            final PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(treeFile, true);
            final Phylogeny phy = PhylogenyMethods.readPhylogenies(parser, treeFile)[0];
            Archaeopteryx.createApplication(phy);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}