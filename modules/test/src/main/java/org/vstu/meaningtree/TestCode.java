package org.vstu.meaningtree;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestCode {
    public final String code;
    public final String language;

    public TestCode(String code) {
        language = parseName(code);
        this.code = parseCode(code);
    }

    private String parseName(String testCode) {
        Pattern langNamePattern = Pattern.compile("^\\s+(\\w+):\\s*", Pattern.DOTALL | Pattern.MULTILINE);
        Matcher langNameMatcher = langNamePattern.matcher(testCode);

        if (!langNameMatcher.find()) {
            throw new IllegalArgumentException("Название языка в тест-кейсе не найдено!");
        }
        return langNameMatcher.group(1);
    }

    private String parseCode(String testCode) {
        // Удалить пустые строки
        List<String> lines = Arrays.stream(testCode.split("\\R"))
                                        .filter(Predicate.not(String::isBlank))
                                        .collect(Collectors.toList());
        // Удалить строку с названием языка (надеюсь что она первая)
        lines.removeFirst();

        // Удалить комментарии с конца
        while (lines.getLast().strip().startsWith("#")) {
            lines.removeLast();
        }

        return lines.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    public String getLanguage() { return language; }

    public String getCode() { return code; }
}
