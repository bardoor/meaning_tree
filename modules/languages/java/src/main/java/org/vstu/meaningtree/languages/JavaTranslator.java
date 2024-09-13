package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.parsers.JavaLanguage;
import org.vstu.meaningtree.languages.viewers.JavaViewer;

public class JavaTranslator extends LanguageTranslator {
    public JavaTranslator() {
        super(new JavaLanguage(), new JavaViewer());
    }
}
