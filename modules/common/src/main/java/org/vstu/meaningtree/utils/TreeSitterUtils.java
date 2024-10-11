package org.vstu.meaningtree.utils;

import org.treesitter.TSNode;

import java.nio.charset.StandardCharsets;

public class TreeSitterUtils {
    public static String getCodePiece(String sourceCode, TSNode node) {
        byte[] code = sourceCode.getBytes(StandardCharsets.UTF_8);
        int start = node.getStartByte();
        int end = node.getEndByte();
        return new String(code, start, end - start);
    }
}
