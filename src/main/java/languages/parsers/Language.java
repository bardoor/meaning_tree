package languages.parsers;

import meaning_tree.MeaningTree;
import org.treesitter.TSNode;

abstract public class Language {
    protected final String _code = "";

    public abstract MeaningTree getMeaningTree(String code);

    protected String getCodePiece(TSNode node) {
        int start = node.getStartByte();
        int end = node.getEndByte();
        return _code.substring(start, end).trim();
    }
}
