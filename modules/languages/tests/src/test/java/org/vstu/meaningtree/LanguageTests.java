package org.vstu.meaningtree;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.vstu.meaningtree.languages.JavaTranslator;
import org.vstu.meaningtree.languages.PythonTranslator;
import org.vstu.meaningtree.languages.Translator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTests {
    private static TestGroup[] _tests;
    private static final String _testsFilePath = "sample.test";
    private static final TestConfig _config = new TestConfig();

    static String readTestsFile(String filePath) throws IOException {
        FileReader reader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(reader);

        StringBuilder tests = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            tests.append(line).append(System.lineSeparator());
        }
        return tests.toString();
    }

    @BeforeAll
    static void setUp() throws IOException {
        _config.addLanguageConfig(new TestLanguageConfig(new JavaTranslator(), "java", false));
        _config.addLanguageConfig(new TestLanguageConfig(new PythonTranslator(), "python", true));
        _tests = TestsParser.parse(readTestsFile(_testsFilePath));
    }

    @TestFactory
    Stream<DynamicContainer> testAllLanguages() {
        // Тут можно посмеяться и поплакать, я называю это "бур"
        return Arrays.stream(_tests)
                .map(group -> DynamicContainer.dynamicContainer(
                        group.getName(),
                        Arrays.stream(group.getCases())
                                .map(testCase -> DynamicTest.dynamicTest(
                                        testCase.getName(),
                                        () -> Assertions.assertAll(
                                                Combinator.getPermutations(testCase.getCodes()).stream().map(pair ->
                                                         () -> assertTrue(new CodeMatcher(
                                                                _config.getByName(pair.left.language).indentSensitive()).equals(
                                                                    pair.left.code,
                                                                    _config.getByName(pair.left.language).translator().getCode(
                                                                            _config.getByName(pair.right.language)
                                                                                    .translator().getMeaningTree(pair.right.code)
                                                                    )
                                                                )
                                                        )
                                                )
                                        )
                                ))
                        )
                );
    }
}