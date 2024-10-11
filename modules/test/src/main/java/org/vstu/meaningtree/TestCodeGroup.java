package org.vstu.meaningtree;

import org.vstu.meaningtree.exceptions.MeaningTreeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

public class TestCodeGroup extends ArrayList<SingleTestCode> {
    private final String language;

    public TestCodeGroup(String codesLanguage, SingleTestCode ... codes) {
        language = codesLanguage;

        if (codes.length == 0) {
            throw new MeaningTreeException("Список тестовых кодов не может быть пустым");
        }

        if(Arrays.stream(codes).map(SingleTestCode::getLanguage).anyMatch(Predicate.not(language::equals))) {
            throw new MeaningTreeException("Группа тестов должна содержать только один язык");
        }

        Collections.addAll(this, codes);
    }

    public String getLanguage() {
        return language;
    }
}
