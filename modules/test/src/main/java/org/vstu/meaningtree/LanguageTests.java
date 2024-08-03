package org.vstu.meaningtree;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.*;
import org.vstu.meaningtree.languages.JavaTranslator;
import org.vstu.meaningtree.languages.PythonTranslator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
        List<ImmutablePair<TestCodeGroup, TestCodeGroup>> codePermutations = TestCombinator.getPairs(testCase);

        for (ImmutablePair<TestCodeGroup, TestCodeGroup> codePair : codePermutations) {
            TestCodeGroup source = codePair.left;
            TestCodeGroup dest = codePair.right;

            // Взять трансляторы из конфига по названию языков
            TestLanguageConfig sourceLangConfig = _config.getByName(source.getLanguage());
            TestLanguageConfig destLangConfig = _config.getByName(dest.getLanguage());
            // Если нет конфига хотя бы для одного языка из пары или не выполняются условия тестирования по схеме
            boolean skipCheck = (sourceLangConfig == null || destLangConfig == null)
                    || (codePair.left.size() > 1 && codePair.right.size() > 1)
                    || (codePair.right.size() > 1 && codePair.left.size() == 1 && !codePair.left.getFirst().getType().equals(TestCodeType.MAIN));

            if (skipCheck) {
                continue;  // Пропустить акт тестирования
            }
            CodeFormatter sourceCodeFormatter = new CodeFormatter(sourceLangConfig.indentSensitive());
            CodeFormatter destCodeFormatter = new CodeFormatter(destLangConfig.indentSensitive());

            // Добавить в контейнер динамических тестов проверку эквивалентности исходного кода и переведённого
            tests.add(DynamicTest.dynamicTest(
                    String.format("%s from %s to %s", testCase.getName(), source.getLanguage(), dest.getLanguage()),
                    () -> {
                        if (source.getFirst().getType().equals(TestCodeType.MAIN)) {
                            // Выполняем проверку на равенство кода по следующей схеме: (source -> dest) == dest

                            // Отформатировать код с учётом чувствительности к индетации
                            String formatedSourceCode = source.getFirst().getFormattedCode(sourceCodeFormatter);

                            // Преобразуем код первого языка в MT, а затем MT - в код второго языка
                            String destSourceCode = destCodeFormatter.format(destLangConfig.translator().getCode(
                                    sourceLangConfig.translator().getMeaningTree(formatedSourceCode)
                            ));

                            boolean anyMatch = dest.stream().map((SingleTestCode code) -> code.getFormattedCode(destCodeFormatter))
                                    .anyMatch((String alternative) -> destCodeFormatter.equals(destSourceCode, alternative));

                            assertTrue(anyMatch,
                                    String.format(
                                            "\nПроверка (%s->%s) == %s\nИсходный код на %s:\n%s\nИсходный код, переведенный в %s:\n%s\nПроверяемый код на %s\n%s\n",
                                            source.getLanguage(), dest.getLanguage(), dest.getLanguage(),
                                            source.getLanguage(), formatedSourceCode,
                                            dest.getLanguage(), destSourceCode,
                                            dest.getLanguage(), dest.getFirst().getFormattedCode(destCodeFormatter)
                                            ));

                        } else {
                            // Выполняем проверку на равенство кода по следующей схеме: source == (dest -> source)

                            // Отформатировать код с учётом чувствительности к индетации
                            List<String> formattedSourceCode = source.stream().map((SingleTestCode code) -> code.getFormattedCode(sourceCodeFormatter)).toList();

                            // Перегнать код на втором языке в MT, затем превратить в код на первом языке
                            String destSourceCode = sourceLangConfig.translator().getCode(
                                    destLangConfig.translator().getMeaningTree(dest.getFirst().getFormattedCode(destCodeFormatter))
                            );

                            boolean anyMatch = formattedSourceCode.stream().anyMatch((String sourceCodeAlt) -> sourceCodeFormatter.equals(sourceCodeAlt, destSourceCode));

                            assertTrue(anyMatch,
                                    String.format(
                                            "\nПроверка %s==(%s->%s)\nИсходный код на %s:\n%s\nПроверяемый код на %s:\n%s\nПроверяемый код, переведенный на %s:\n%s\n",
                                            source.getLanguage(), dest.getLanguage(), source.getLanguage(),
                                            source.getLanguage(), formattedSourceCode.getFirst(),
                                            dest.getLanguage(), dest.getFirst().getFormattedCode(destCodeFormatter),
                                            source.getLanguage(), destSourceCode));
                        }
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
