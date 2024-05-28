package org.vstu.meaningtree.languages.parsers;

import org.vstu.meaningtree.MeaningTree;
import org.treesitter.TSNode;

import java.nio.charset.StandardCharsets;

abstract public class Language {
    protected String _code = "";

    public abstract MeaningTree getMeaningTree(String code);

    protected String getCodePiece(TSNode node) {
        byte[] code = _code.getBytes(StandardCharsets.UTF_8);
        int start = node.getStartByte();
        int end = node.getEndByte();
        return new String(code, start, end - start).trim();
    }
}
