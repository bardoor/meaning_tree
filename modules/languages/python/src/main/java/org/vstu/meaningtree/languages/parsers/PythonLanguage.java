package org.vstu.meaningtree.languages.parsers;

import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Node;

import java.io.File;
import java.io.IOException;

public class PythonLanguage extends Language {
    @Override
    public MeaningTree getMeaningTree(String code) {
        _code = code;
        TSParser parser = new TSParser();
        TSLanguage javaLanguage = new TreeSitterPython();

        parser.setLanguage(javaLanguage);
        TSTree tree = parser.parseString(null, code);
        try {
            tree.printDotGraphs(new File("TSTree.dot"));
        } catch (IOException e) { }

        return new MeaningTree(fromTSNode(tree.getRootNode()));
    }

    private Node fromTSNode(TSNode rootNode) {
        return null;
    }
}
