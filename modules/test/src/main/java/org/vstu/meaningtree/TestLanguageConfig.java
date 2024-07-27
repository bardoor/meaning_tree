package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.Translator;

public record TestLanguageConfig(Translator translator, String languageName, boolean indentSensitive)  {

}
