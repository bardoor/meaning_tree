package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.CppTranslator;
import org.vstu.meaningtree.languages.JavaTranslator;
import org.vstu.meaningtree.languages.LanguageTranslator;
import org.vstu.meaningtree.languages.PythonTranslator;

import java.util.HashMap;
import java.util.Map;

public class SupportedLanguages {
    private static Map<String, Class<? extends LanguageTranslator>> translators = new HashMap<>();

    static {
        translators.put("java", JavaTranslator.class);
        translators.put("cpp", CppTranslator.class);
        translators.put("python", PythonTranslator.class);
    }

    public static Map<String, Class<? extends LanguageTranslator>> getMap() {
        return translators;
    }
}
