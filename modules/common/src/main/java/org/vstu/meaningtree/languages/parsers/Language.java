package org.vstu.meaningtree.languages.parsers;

import org.vstu.meaningtree.MeaningTree;
import org.treesitter.TSNode;

abstract public class Language {
    protected String _code = "";

    public abstract MeaningTree getMeaningTree(String code);

    protected String getCodePiece(TSNode node) {
        int start = node.getStartByte();
        int end = node.getEndByte();
        return _code.substring(start, end).trim();
    }
}
