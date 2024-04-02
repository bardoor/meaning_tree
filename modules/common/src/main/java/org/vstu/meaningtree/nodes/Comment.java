package org.vstu.meaningtree.nodes;

public class Comment extends Node {
    protected final String _content;

    public Comment(String content) {
        _content = content;
    }

    public String getContent() {
        return _content;
    }

    public boolean isMultiline() {
        return getContent().contains("\n");
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
