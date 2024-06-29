package org.vstu.meaningtree;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CodeFormatter {
    protected final boolean _indentSensitive;
    protected final int TAB_SIZE = 4;

    public CodeFormatter(boolean indentSensitive) {
        _indentSensitive = indentSensitive;
    }

    public String format(String code) {
        return removeMeaninglessIndents(code);
    }

    public boolean equals(String codeA, String codeB) {
        return removeMeaninglessIndents(codeA).equals(removeMeaninglessIndents(codeB));
    }

    /**
     * Удаляет незначащую индетацию, заменяет табуляцию на {@value TAB_SIZE} пробела
     * <pre>
     * Например:
     * for (auto elem in elements) {
     *     printf(elem);
     * }
     * Меняется на:
     * for (auto elem in elements) {
     * printf(elem);
     * }
     * </pre>
     * @param code исходный код
     * @return код без незначащей индетации
     */
    private String removeMeaninglessIndents(String code) {
        // Заменить все табы пробелами и разбить на строки
        String[] lines = code.replaceAll("\\t", " ".repeat(TAB_SIZE))
                             .split(System.lineSeparator());

        // Если язык не чувствителен к индетации, вырезать пробелы из начала и конца строки
        if (!_indentSensitive) {
            return Arrays.stream(lines)
                            .map(String::strip)
                            .collect(Collectors.joining(System.lineSeparator()));
        }

        // Иначе считать индетацию первой строки - базовой
        int baseIndent = Pattern.compile("^(\\s+)\\w").matcher(lines[0]).group().length();
        // Удалить индетацию, равную базовой у каждой строки
        return Arrays.stream(lines)
                        .map(line -> line.substring(baseIndent))
                        .collect(Collectors.joining(System.lineSeparator()));
    }

}