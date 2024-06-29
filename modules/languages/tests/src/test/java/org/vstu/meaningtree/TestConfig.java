package org.vstu.meaningtree;

import java.util.ArrayList;
import java.util.List;

public class TestConfig {
    private final List<TestLanguageConfig> configList = new ArrayList<>();

    public void addLanguageConfig(TestLanguageConfig ... languageConfig) {
        configList.addAll(List.of(languageConfig));
    }

    public TestLanguageConfig getByName(String name) {
        for (TestLanguageConfig config : configList) {
            if (config.languageName().equals(name)) {
                return config;
            }
        }
        return null;
    }
}
