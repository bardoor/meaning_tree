package org.vstu.meaningtree;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestGroup {
    private final String _name;
    private final ArrayList<TestCase> _testCases;

    public TestGroup(String testGroup) {
        _name = parseName(testGroup);
        _testCases = parseCases(testGroup);
    }

    private String parseName(String testGroup) {
        Pattern namePattern = Pattern.compile("group:\\s+(\\w*)");
        Matcher nameMatcher = namePattern.matcher(testGroup);

        if (!nameMatcher.find()) {
            throw new IllegalArgumentException("Имя группы тестов не найдено!");
        }
        return nameMatcher.group(1);
    }

    private ArrayList<TestCase> parseCases(String testGroup) {
        Pattern casePattern = Pattern.compile("case:\\s+\\w+\\s+(?:(?!\\s*case:).)*",Pattern.DOTALL | Pattern.MULTILINE);
        Matcher caseMatcher = casePattern.matcher(testGroup);

        ArrayList<TestCase> cases = new ArrayList<>();
        while(caseMatcher.find()) {
            cases.add(new TestCase(caseMatcher.group()));
        }
        return cases;
    }


    public String getName() { return _name; }

    public TestCase[] getCases() { return _testCases.toArray(TestCase[]::new); }
}
