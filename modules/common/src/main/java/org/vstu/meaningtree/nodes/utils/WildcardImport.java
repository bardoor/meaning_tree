package org.vstu.meaningtree.nodes.utils;

import org.vstu.meaningtree.nodes.Identifier;

public class WildcardImport extends Identifier {
    public WildcardImport() {
        super("*");
    }

    /**
     * Empty node, used in imports
     */

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
