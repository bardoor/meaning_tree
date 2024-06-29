package org.vstu.meaningtree;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        // Удалить пустые строки
        List<String> lines = Arrays.stream(testCode.split("\\R"))
                                        .filter(String::isBlank)
                                        .collect(Collectors.toList());
        // Удалить строку с названием языка (надеюсь что она первая)
        lines.removeFirst();

        // Удалить комментарии с конца
        while (lines.getLast().strip().startsWith("#")) {
            lines.removeLast();
        }

        return lines.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    public String getLanguage() { return _language; }

    public String getCode() { return _code; }
}
