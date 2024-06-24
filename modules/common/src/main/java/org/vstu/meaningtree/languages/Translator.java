package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.languages.parsers.Language;
import org.vstu.meaningtree.languages.viewers.Viewer;

public abstract class Translator {
    protected Language _language;
    protected Viewer _viewer;

    protected Translator(Language language, Viewer viewer) {
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
