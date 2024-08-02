package org.vstu.meaningtree;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.*;
import org.vstu.meaningtree.languages.JavaTranslator;
import org.vstu.meaningtree.languages.PythonTranslator;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTests {
    private static final Map<String, TestGroup[]> _tests = new HashMap<>();
    private static final File _resourcesDirectory = new File("src/main/resources");
    private static final TestConfig _config = new TestConfig();

    static boolean checkExtension(File file, String extension) {
        int i = file.getName().lastIndexOf(".") + 1;
        return file.getName().substring(i).equals(extension);
    }

    static void parseTestsFiles() throws IOException {
        File[] files = _resourcesDirectory.listFiles();
        if (files == null) {
            throw new FileNotFoundException("В директории тестовых ресурсов не найдены тестировочные файлы!");
        }

        for (File file : files) {
            if (file.isDirectory() || !checkExtension(file, "test")) {
                continue;
            }

            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder testBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                testBuilder.append(line).append("\n");
            }

            _tests.put(file.getName(), TestsParser.parse(testBuilder.toString()));
        }
    }

    @BeforeAll
    static void setUp() throws IOException {
        _config.addLanguageConfig(new TestLanguageConfig(new JavaTranslator(), "java", false));
        _config.addLanguageConfig(new TestLanguageConfig(new PythonTranslator(), "python", true));
        parseTestsFiles();
    }

    List<DynamicTest> createTests(TestCase testCase) {
        List<DynamicTest> tests = new ArrayList<>();
        List<ImmutablePair<SingleTestCode, TestCodeGroup>> codePermutations = TestCombinator.getPairs(testCase);

        for (ImmutablePair<SingleTestCode, TestCodeGroup> codePair : codePermutations) {
            SingleTestCode source = codePair.left;
            TestCodeGroup alternatives = codePair.right;

            // Взять трансляторы из конфига по названию языков
            TestLanguageConfig sourceLangConfig = _config.getByName(source.language);
            TestLanguageConfig translatedLangConfig = _config.getByName(alternatives.getLanguage());
            // Если нет конфига хотя бы для одного языка из пары
            if (sourceLangConfig == null || translatedLangConfig == null) {
                continue;  // Пропустить акт тестирования
            }
            CodeFormatter sourceCodeFormatter = new CodeFormatter(sourceLangConfig.indentSensitive());
            CodeFormatter translatedCodeFormatter = new CodeFormatter(translatedLangConfig.indentSensitive());

            // Добавить в контейнер динамических тестов проверку эквивалентности исходного кода и переведённого
            tests.add(DynamicTest.dynamicTest(
                    String.format("%s from %s to %s", testCase.getName(), source.language, alternatives.getLanguage()),
                    () -> {
                        // Отформатировать код с учётом чувствительности к индетации
                        String formatedSourceCode = source.getFormattedCode(sourceCodeFormatter);

                        // Перегнать код на втором языке в MT, затем превратить в код на первом языке
                        String[] translatedCodeAlternatives = new String[alternatives.size()];
                        for (int i = 0; i < translatedCodeAlternatives.length; i++) {
                            translatedCodeAlternatives[i] = sourceLangConfig.translator().getCode(
                                    translatedLangConfig.translator().getMeaningTree(alternatives.get(i).getFormattedCode(translatedCodeFormatter))
                            );
                            translatedCodeAlternatives[i] = sourceCodeFormatter.format(translatedCodeAlternatives[i]);
                        }

                        boolean anyMatch = Arrays.stream(translatedCodeAlternatives).anyMatch((String translatedSourceCode) -> sourceCodeFormatter.equals(formatedSourceCode, translatedSourceCode));

                        assertTrue(anyMatch,
                                String.format(
                                        "\nПроверка %s->%s\nИсходный код на %s:\n%s\nИсходный код на %s:\n%s\nКод на %s, переведенный с %s:\n%s\n",
                                        source.language, alternatives.getLanguage(),
                                        source.language, formatedSourceCode,
                                        alternatives.getLanguage(), alternatives.getFirst().getFormattedCode(translatedCodeFormatter),
                                        source.language, alternatives.getLanguage(), translatedCodeAlternatives[0]));
                    }
            ));
        }
        return tests;
    }

    @TestFactory
    List<DynamicContainer> testAllLanguages() {
        List<DynamicContainer> allTests = new ArrayList<>();
        for (Map.Entry<String, TestGroup[]> entry : _tests.entrySet()) {
            String fileName = entry.getKey();
            TestGroup[] groups = entry.getValue();

            List<DynamicContainer> testsInFile = new ArrayList<>();

            for (TestGroup group : groups) {
                List<DynamicContainer> testGroup = new ArrayList<>();

                for (TestCase testCase : group.getCases()) {
                    testGroup.add(DynamicContainer.dynamicContainer(testCase.getName(), createTests(testCase)));
                }

                testsInFile.add(DynamicContainer.dynamicContainer(group.getName(), testGroup));
            }
            allTests.add(DynamicContainer.dynamicContainer(fileName, testsInFile));
        }

        return allTests;
    }
}
