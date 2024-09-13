package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.LanguageTranslator;

public record TestLanguageConfig(LanguageTranslator translator, String languageName, boolean indentSensitive)  {

}
