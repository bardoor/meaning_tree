package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.parsers.PythonLanguage;
import org.vstu.meaningtree.languages.viewers.PythonViewer;

public class PythonTranslator extends Translator {
    public PythonTranslator() {
        super(new PythonLanguage(), new PythonViewer());
    }
}
