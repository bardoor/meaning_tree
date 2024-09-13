package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.languages.parsers.LanguageParser;
import org.vstu.meaningtree.languages.viewers.LanguageViewer;

public abstract class LanguageTranslator {
    protected LanguageParser _language;
    protected LanguageViewer _viewer;

    protected LanguageTranslator(LanguageParser language, LanguageViewer viewer) {
        _language = language;
        _viewer = viewer;
    }

    public MeaningTree getMeaningTree(String code) {
        return _language.getMeaningTree(code);
    }

    public String getCode(MeaningTree mt) {
        return _viewer.toString(mt);
    }
}
