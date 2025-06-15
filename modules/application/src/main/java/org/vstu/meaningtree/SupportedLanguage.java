package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public enum SupportedLanguage {
    JAVA("java", JavaTranslator.ID),
    PYTHON("python", PythonTranslator.ID),
    CPP("c++", CppTranslator.ID);

    private final String stringValue;
    private final int id;

    SupportedLanguage(String value, int id) {
        this.stringValue = value;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SupportedLanguage from(int id) {
        for (SupportedLanguage language : translators.keySet()) {
            if (language.id == id) {
                return language;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    private static Map<SupportedLanguage, Class<? extends LanguageTranslator>> translators = new HashMap<>();

    static {
        translators.put(JAVA, JavaTranslator.class);
        translators.put(CPP, CppTranslator.class);
        translators.put(PYTHON, PythonTranslator.class);
    }

    public static Map<SupportedLanguage, Class<? extends LanguageTranslator>> getMap() {
        return translators;
    }

    public static SupportedLanguage fromString(String s) {
        for (SupportedLanguage lang : translators.keySet()) {
            if (lang.toString().equals(s.toLowerCase())) {
                return lang;
            }
        }
        return null;
    }

    public static Map<String, Class<? extends LanguageTranslator>> getStringMap() {
        HashMap<String, Class<? extends LanguageTranslator>> map = new HashMap<>();
        for (SupportedLanguage lang : translators.keySet()) {
            map.put(lang.toString(), translators.get(lang));
        }
        return map;
    }

    public LanguageTranslator createTranslator() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return translators.get(this).getConstructor().newInstance();
    }

    public LanguageTranslator createTranslator(Map<String, String> config) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return translators.get(this).getConstructor(Map.class).newInstance(config);
    }

    public Class<? extends LanguageTranslator> getTranslatorClass() {
        return translators.get(this);
    }
}
