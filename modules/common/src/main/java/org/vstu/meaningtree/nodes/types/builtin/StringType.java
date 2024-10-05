package org.vstu.meaningtree.nodes.types.builtin;

import org.vstu.meaningtree.nodes.Type;

public class StringType extends Type {
    public final int charSize;

    public StringType() {
        charSize = 16;
    }

    public StringType(int charSize) {
        this.charSize = Math.min(Math.max(charSize, 8), 32);
    }

    public boolean isUnicode() {
        return charSize >= 16;
    }

    public int getCharSize() {
        return charSize;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
