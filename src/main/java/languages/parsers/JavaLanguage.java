package languages.parsers;
import jdk.jshell.spi.ExecutionControl;
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
        return switch (node.getType()) {
            case "program" -> fromProgramTSNode(node);
            case null, default -> throw new RuntimeException("Not implemented yet!");
        };
    }

    private Node fromProgramTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

}
