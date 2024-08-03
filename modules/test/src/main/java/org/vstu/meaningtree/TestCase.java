package org.vstu.meaningtree;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestCase {
    private final String _name;
    private List<TestCodeGroup> _codeGroups; // Группы, состоящие из альтернатив кода на одном языке
    private SingleTestCode _mainCode; // Основной язык, если установлен, то все преобразования производятся от него

    public TestCase(String testCase) {
        _name = parseName(testCase);
        parseCodes(testCase);
    }

    private String parseName(String testCase) {
        Pattern namePattern = Pattern.compile("case:\\s+(\\w*)");
        Matcher nameMatcher = namePattern.matcher(testCase);

        if (!nameMatcher.find()) {
            throw new IllegalArgumentException("Имя тест-кейса не найдено!");
        }
        return nameMatcher.group(1);
    }

    private void parseCodes(String testCase) {
        Pattern langNamePattern = Pattern.compile("^([ \\t\\f\\r]+)((main|alt|static)\\s+)?[^\\s]+:\\s*$", Pattern.MULTILINE);
        Matcher matcher = langNamePattern.matcher(testCase);

        // Найти строки с названиями языков
        ArrayList<MatchResult> results = matcher.results().collect(Collectors.toCollection(ArrayList::new));
        // Определить отступ
        String langNameIndent = results.getFirst().group().replace(results.getFirst().group().strip(), "");
        ArrayList<Integer> codesStarts = results.stream()
                .filter(match -> match.group().replace(match.group().strip(), "").equals(langNameIndent))
                .map(MatchResult::start)
                .collect(Collectors.toCollection(ArrayList::new));

        codesStarts.add(testCase.length());

        // Вычленить всё что начинается названием языка включительно
        // и кончается названием другого языка не включительно
        List<SingleTestCode> codes = new ArrayList<>();
        HashMap<String, TestCodeGroup> alternatives = new HashMap<>();
        SingleTestCode mainCode = null;

        for (int i = 0; i < codesStarts.size() - 1; i++) {
            String code = testCase.substring(codesStarts.get(i), codesStarts.get(i + 1));
            SingleTestCode testCode = new SingleTestCode(code);
            if (mainCode != null && testCode.getType().equals(TestCodeType.MAIN)) {
                throw new RuntimeException("В тест кейсе несколько главных кодов:\n" + testCase);
            }
            if (testCode.getType().equals(TestCodeType.MAIN)) {
                mainCode = testCode;
            } else if (testCode.getType().equals(TestCodeType.ALTERNATIVE)) {
                if (!alternatives.containsKey(testCode.getLanguage())) {
                    alternatives.put(testCode.getLanguage(), new TestCodeGroup(testCode.getLanguage()));
                }
                alternatives.get(testCode.getLanguage()).add(testCode);
            } else {
                if (alternatives.containsKey(testCode.getLanguage())) {
                    throw new RuntimeException("В тест кейсе язык " + testCode.getLanguage() + " должен состоять только из альтернатив");
                }
                codes.add(testCode);
            }
        }
        _codeGroups = new ArrayList<>();
        _codeGroups.addAll(alternatives.values());
        for (SingleTestCode code : codes) {
            _codeGroups.add(new TestCodeGroup(code.getLanguage(), code));
        }
        _mainCode = mainCode;
    }

    public String getName() { return _name; }

    public List<TestCodeGroup> getCodeGroups() {
        return new ArrayList<>(_codeGroups);
    }

    public SingleTestCode getMainCode() {
        return _mainCode;
    }

    public boolean hasMainCode() {
        return _mainCode != null;
    }

    public String[] getLanguages() {
        return _codeGroups.stream().flatMap(Collection::stream).map(SingleTestCode::getLanguage).distinct().toArray(String[]::new);
    }
}
