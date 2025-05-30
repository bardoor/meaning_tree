package org.vstu.meaningtree;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.vstu.meaningtree.languages.*;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Main {

    @Parameters(commandDescription = "Translate code between programming languages")
    public static class TranslateCommand {
        @Parameter(names = "--from", description = "Source language", required = true)
        private String fromLanguage;

        @Parameter(names = "--to", description = "Target language", required = true)
        private String toLanguage;

        @Parameter(description = "<input_file> [output_file]", required = true, arity = 1)
        private java.util.List<String> positionalParams;

        public String getFromLanguage() {
            return fromLanguage;
        }

        public String getToLanguage() {
            return toLanguage;
        }

        public String getInputFile() {
            return positionalParams.getFirst();
        }

        public String getOutputFile() {
            return positionalParams.size() > 1 ? positionalParams.get(1) : "-";
        }
    }

    @Parameters(commandDescription = "List all supported languages")
    public static class ListLangsCommand {}

    public static Map<String, Class<? extends LanguageTranslator>> translators = SupportedLanguage.getStringMap();

    public static void main(String[] args) throws Exception {
        var p1 = new PythonLanguage();
        var mt = p1.getMeaningTree("a = 10\na = a + 0.5");

        var v1 = new CppViewer();
        var code = v1.toString(mt);
        System.out.println(code);

        var v2 = new JavaViewer();
        var code2 = v2.toString(mt);
        System.out.println(code2);
    }

    private static void listSupportedLanguages() {
        System.out.println("Supported languages: " + String.join(", ", translators.keySet()));
    }

    private static void runTranslation(TranslateCommand cmd) throws Exception {
        String fromLanguage = cmd.getFromLanguage();
        String toLanguage = cmd.getToLanguage();
        String inputFilePath = cmd.getInputFile();
        String outputFilePath = cmd.getOutputFile();

        if (!translators.containsKey(fromLanguage.toLowerCase()) || !translators.containsKey(toLanguage.toLowerCase())) {
            System.err.println("Unsupported language. Supported languages: " + translators.keySet());
            return;
        }

        String code = readCode(inputFilePath);
        LanguageTranslator fromTranslator = translators.get(fromLanguage.toLowerCase()).getDeclaredConstructor().newInstance();
        LanguageTranslator toTranslator = translators.get(toLanguage.toLowerCase()).getDeclaredConstructor().newInstance();

        MeaningTree mt = fromTranslator.getMeaningTree(code);
        String translatedCode = toTranslator.getCode(mt);

        if ("-".equals(outputFilePath)) {
            System.out.println(translatedCode);
        } else {
            try (PrintWriter out = new PrintWriter(new FileWriter(outputFilePath))) {
                out.print(translatedCode);
            }
        }
    }

    private static String readCode(String filePath) throws IOException {
        if ("-".equals(filePath)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int c;
            while ((c = System.in.read()) != -1) {
                buffer.write(c);
            }
            return buffer.toString(StandardCharsets.UTF_8);
        } else {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
        }
    }
}