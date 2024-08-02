package org.vstu.meaningtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TestCodeGroup extends ArrayList<SingleTestCode> {
    private final String language;

    public TestCodeGroup(String codesLanguage, SingleTestCode ... codes) {
        language = codesLanguage;
        if (codes.length > 0 &&
                !Arrays.stream(codes).map(SingleTestCode::getLanguage).allMatch(language::equals)) {
            throw new RuntimeException("Группа тестов должна содержать только один язык");
        }
        Collections.addAll(this, codes);

    }

    public String getLanguage() {
        return language;
    }
}
