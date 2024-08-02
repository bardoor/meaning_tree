package org.vstu.meaningtree;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SingleTestCode {
    public final String code;
    public String language;
    public TestCodeType type;


    public SingleTestCode(String code) {
        parseName(code);
        this.code = parseCode(code);
    }

    private String parseName(String testCode) {
        Pattern langNamePattern = Pattern.compile("^\\s+((main|alt)\\s+)?([^\\s]+):\\s*$", Pattern.MULTILINE);
        Matcher langNameMatcher = langNamePattern.matcher(testCode);

        if (!langNameMatcher.find()) {
            throw new IllegalArgumentException("Название языка в тест-кейсе не найдено!");
        }
        language = langNameMatcher.group(3);

        String langPrefix = langNameMatcher.group(2);
        type = switch (langPrefix) {
            case "alt" -> TestCodeType.ALTERNATIVE;
            case "main" -> TestCodeType.MAIN;
            case null, default -> TestCodeType.DEFAULT;
        };
        return language;
    }

    private String parseCode(String testCode) {
        // Удалить пустые строки
        List<String> lines = Arrays.stream(testCode.split("\\R"))
                                        .filter(Predicate.not(String::isBlank))
                                        .collect(Collectors.toList());

        // Удалить строку с названием языка (надеюсь что она первая)
        lines.removeFirst();
        String indent = lines.getFirst().replace(lines.getFirst().strip(), "");

        if (lines.isEmpty()) {
            throw new RuntimeException("Нет кода: " + testCode);
        }

        // Удалить комментарии с конца
        while (lines.getLast().strip().startsWith("#") && !lines.getLast().replace(lines.getLast().strip(), "").equals(indent)) {
            lines.removeLast();
        }

        return lines.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    public String getLanguage() { return language; }

    public String getCode() { return code; }

    public String getFormattedCode(CodeFormatter formatter) {
        return formatter.format(code);
    }

    public TestCodeType getType() {
        return type;
    }
}
