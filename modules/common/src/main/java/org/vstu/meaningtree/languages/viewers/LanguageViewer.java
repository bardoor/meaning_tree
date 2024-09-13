package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Node;

abstract public class LanguageViewer {
    public abstract String toString(Node node);

    public String toString(MeaningTree mt) {
        return toString(mt.getRootNode());
    }
}
