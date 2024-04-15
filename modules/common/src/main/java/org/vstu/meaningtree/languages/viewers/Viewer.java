package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Node;

abstract public class Viewer {
    public abstract String toString(Node node);

    public String toString(MeaningTree mt) {
        return toString(mt.getRootNode());
    }
}
