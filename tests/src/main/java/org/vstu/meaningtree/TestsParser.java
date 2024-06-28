package org.vstu.meaningtree;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestsParser {
    public static TestGroup[] parse(String tests) {
        Pattern groupPattern = Pattern.compile("group:\\s+\\w+\\s+(?:(?!^\\s*group:).)*", Pattern.DOTALL);
        Matcher groupMatcher = groupPattern.matcher(tests);
        ArrayList<TestGroup> testGroups = new ArrayList<>();

        while (groupMatcher.find()) {
            // Добавить каждую найденную группу в список
            testGroups.add(new TestGroup(groupMatcher.group()));
        }

        return testGroups.toArray(TestGroup[]::new);
    }
}
