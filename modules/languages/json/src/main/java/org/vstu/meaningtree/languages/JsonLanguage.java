package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.treesitter.TSTree;
import org.vstu.meaningtree.MeaningTree;

public class JsonLanguage extends LanguageParser{
    @Override
    public TSTree getTSTree() {
        return null;
    }

    @Override
    public MeaningTree getMeaningTree(String code) {
        return null;
    }

    @Override
    public MeaningTree getMeaningTree(TSNode node, String code) {
        return null;
    }
}
