package org.vstu.meaningtree;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.*;
import org.vstu.meaningtree.languages.JavaTranslator;
import org.vstu.meaningtree.languages.PythonTranslator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTests {
    private static TestGroup[] _tests;
    private static final String _testsFilePath = "src/main/resources/common.test";
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
        List<ImmutablePair<TestCode, TestCode>> codePermutations;
        List<DynamicContainer> allTests = new ArrayList<>();

        for (TestGroup group : _tests) {
            List<DynamicContainer> testGroup = new ArrayList<>();

            for (TestCase testCase : group.getCases()) {
                codePermutations = Combinator.getPermutations(testCase.getCodes());
                List<DynamicTest> dynamicTests = new ArrayList<>();

                for (ImmutablePair<TestCode, TestCode> codePair : codePermutations) {
                    TestCode source = codePair.left;
                    TestCode translated = codePair.right;

                    // Взять трансляторы из конфига по названию языков
                    TestLanguageConfig sourceLangConfig = _config.getByName(source.language);
                    TestLanguageConfig translatedLangConfig = _config.getByName(translated.language);
                    if (sourceLangConfig == null || translatedLangConfig == null) {
                        continue;
                    }

                    CodeFormatter codeFormatter = new CodeFormatter(_config.getByName(source.language).indentSensitive());

                    // Добавить в контейнер динамических тестов проверку эквивалентности исходного кода и переведённого
                    dynamicTests.add(DynamicTest.dynamicTest(
                            String.format("%s from %s to %s", testCase.getName(), source.language, translated.language),
                            () -> {
                                // Перегнать код на втором языке в MT, затем превратить в код на первом языке
                                String translatedCode = sourceLangConfig.translator().getCode(
                                        translatedLangConfig.translator().getMeaningTree(translated.code)
                                );

                                // Отформатировать код с учётом чувствительности к индетации
                                String formatedSourceCode = codeFormatter.format(source.code);
                                String translatedSourceCode = codeFormatter.format(translatedCode);

                                assertTrue(codeFormatter.equals(formatedSourceCode, translatedSourceCode),
                                           String.format("\nИсходный код на %s:\n%s\nПереведённый код на %s:\n%s\n",
                                                   source.language, formatedSourceCode, translated.language, translatedSourceCode));
                            }
                    ));
                }
                testGroup.add(DynamicContainer.dynamicContainer(
                        testCase.getName(),
                        dynamicTests
                ));
            }
            allTests.add(DynamicContainer.dynamicContainer(
                    group.getName(),
                    testGroup
            ));
        }

        return allTests.stream();
    }
}
