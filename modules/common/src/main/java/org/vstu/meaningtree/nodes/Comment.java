package org.vstu.meaningtree.nodes;

import org.apache.commons.text.StringEscapeUtils;

public class Comment extends Node {
    protected final String _content;
    // NOTE: Комментарии хранятся аналогично строковым литералам в неэкранированном виде

    private Comment(String content) {
        _content = content;
    }

    public static Comment fromUnescaped(String codePiece) {
        return new Comment(codePiece);
    }

    public static Comment fromEscaped(String codePiece) {
        codePiece = StringEscapeUtils.unescapeJava(codePiece);
        return new Comment(codePiece);
    }

    public String getUnescapedContent() {
        return _content;
    }

    public String getEscapedContent() {
        return StringEscapeUtils.escapeJava(_content);
    }

    public boolean isMultiline() {
        return getContent().contains("\n");
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
