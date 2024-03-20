package languages.parsers;
import meaning_tree.*;
import org.treesitter.*;

public class JavaLanguage {
    private final String _code = "";

    public MeaningTree getMeaningTree(String code) {
        TSParser parser = new TSParser();
        TSLanguage javaLanguage = new TreeSitterJava();

        parser.setLanguage(javaLanguage);
        TSTree tree = parser.parseString(null, code);

        return new MeaningTree(fromTSNode(tree.getRootNode()));
    }

    private Node fromTSNode(TSNode node) {
        return null;
    }



}
