package org.vstu.meaningtree.nodes.utils;

import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

public class WildcardImport extends SimpleIdentifier {
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
