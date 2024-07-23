package org.vstu.meaningtree;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestCase {
    private final String _name;
    private final List<TestCode> _codes;  // Язык -> Код

    public TestCase(String testCase) {
        _name = parseName(testCase);
        _codes = parseCodes(testCase);
    }

    private String parseName(String testCase) {
        Pattern namePattern = Pattern.compile("case:\\s+(\\w*)");
        Matcher nameMatcher = namePattern.matcher(testCase);

        if (!nameMatcher.find()) {
            throw new IllegalArgumentException("Имя тест-кейса не найдено!");
        }
        return nameMatcher.group(1);
    }

    private List<TestCode> parseCodes(String testCase) {
        Pattern langNamePattern = Pattern.compile("^([ \\t\\f\\r]+)\\w+:\\s*$", Pattern.MULTILINE);
        Matcher matcher = langNamePattern.matcher(testCase);

        if (!matcher.find()) {
            throw new RuntimeException("В следующем тест кейсе не найдены языки:\n" + testCase);
        }

        // Найти строки с названиями языков
        String langNameIndent = matcher.group();
        ArrayList<Integer> codesStarts = matcher.results()
                .filter(match -> match.group().equals(langNameIndent))
                .map(MatchResult::start)
                .collect(Collectors.toCollection(ArrayList::new));

        // Вычленить всё что начинается названием языка включительно
        // и кончается названием другого языка не включительно
        ArrayList<TestCode> codes = new ArrayList<>();
        for (int i = 0; i < codesStarts.size() - 1; i++) {
            String code = testCase.substring(codesStarts.get(i), codesStarts.get(i + 1));
            codes.add(new TestCode(code));
        }

        return codes;
    }

    public String getName() { return _name; }

    public TestCode getCode(String language) {
        return _codes.stream()
                        .filter(code -> code.getLanguage().equals(language))
                        .findFirst()
                        .orElse(null);
    }

    public TestCode[] getCodes() { return _codes.toArray(TestCode[]::new); }

    public String[] getLanguages() { return _codes.stream().map(TestCode::getLanguage).toArray(String[]::new); }
}
