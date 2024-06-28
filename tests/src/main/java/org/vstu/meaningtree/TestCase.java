package org.vstu.meaningtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCase {
    private final String _name;
    private final ArrayList<TestCode> _codes;  // Язык -> Код

    public TestCase(String testCase) {
        _name = parseName(testCase);
        _codes = parseCodes(testCase);
    }

    private String parseName(String testCase) {
        Pattern namePattern = Pattern.compile("case:\\s+(\\w*)$");
        Matcher nameMatcher = namePattern.matcher(testCase);

        if (!nameMatcher.find()) {
            throw new IllegalArgumentException("Имя тест-кейса не найдено!");
        }
        return nameMatcher.group();
    }

    private ArrayList<TestCode> parseCodes(String testCase) {
        Pattern codePattern = Pattern.compile("\\s+\\w+:\\s+\\w+\\s+(?:(?!^\\s*\\w+:).)*", Pattern.DOTALL);
        Matcher codeMatcher = codePattern.matcher(testCase);

        ArrayList<TestCode> codes = new ArrayList<>();
        while (codeMatcher.find()) {
            codes.add(new TestCode(codeMatcher.group()));
        }
        return codes;
    }

    public String getName() { return _name; }

    public TestCode[] getCodes() { return _codes.toArray(TestCode[]::new); }

    public String[] getLanguages() { return _codes.stream().map(TestCode::getLanguage).toArray(String[]::new); }
}
