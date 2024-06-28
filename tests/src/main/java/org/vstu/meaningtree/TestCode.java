package org.vstu.meaningtree;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCode {
    private final String _code;
    private final String _language;

    public TestCode(String code) {
        _language = parseName(code);
        _code = parseCode(code);
    }

    private String parseName(String testCode) {
        Pattern langNamePattern = Pattern.compile("^\\s+(\\w+):\\s*$");
        Matcher langNameMatcher = langNamePattern.matcher(testCode);

        if (!langNameMatcher.find()) {
            throw new IllegalArgumentException("Название языка в тест-кейсе не найдено!");
        }
        return langNameMatcher.group();
    }

    private String parseCode(String testCode) {
        return "";  // TODO
    }

    public String getLanguage() { return _language; }

    public String getCode() { return _code; }
}
