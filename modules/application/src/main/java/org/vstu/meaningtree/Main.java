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
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class Main {

    @Parameters(commandDescription = "Translate code between programming languages (optional: generate an Auglet refactor problem first)")
    public static class TranslateCommand {
        @Parameter(names = "--from", description = "Source language", required = true)
        private String fromLanguage;

        @Parameter(names = "--to", description = "Target language", required = true)
        private String toLanguage;

        @Parameter(names = "--task",
                description = "Optional Auglet refactor problem to generate before translation. " +
                        "Possible values: add_dangling_else, add_useless_condition_checking_in_else, " +
                        "wrap_while_loop_and_replace_it_with_do_while, convert_redundant_condition_checks, " +
                        "add_duplicated_case_bodies, add_redundant_condition_check_after_loop")
        private String task;

        @Parameter(description = "<input_file> [output_file]", required = true, arity = 1)
        private java.util.List<String> positionalParams;

        public String getFromLanguage() {
            return fromLanguage;
        }

        public String getToLanguage() {
            return toLanguage;
        }

        public String getTask() {
            return task;
        }

        public String getInputFile() {
            return positionalParams.get(0);
        }

        public String getOutputFile() {
            return positionalParams.size() > 1 ? positionalParams.get(1) : "-";
        }
    }

    @Parameters(commandDescription = "List all supported languages")
    public static class ListLangsCommand {}

    public static Map<String, Class<? extends LanguageTranslator>> translators =
            SupportedLanguage.getStringMap();

    public static void main(String[] args) throws Exception {
        TranslateCommand translateCommand = new TranslateCommand();
        ListLangsCommand listLangsCommand = new ListLangsCommand();

        JCommander jc = JCommander.newBuilder()
                .addCommand("translate", translateCommand)
                .addCommand("list-langs", listLangsCommand)
                .build();

        jc.parse(args);

        String parsed = jc.getParsedCommand();
        if ("list-langs".equals(parsed)) {
            listSupportedLanguages();
        } else if ("translate".equals(parsed)) {
            runTranslation(translateCommand);
        } else {
            jc.usage();
        }
    }

    private static void listSupportedLanguages() {
        System.out.println("Supported languages: " + String.join(", ", translators.keySet()));
    }

    private static void runTranslation(TranslateCommand cmd) throws Exception {
        String fromLanguage = cmd.getFromLanguage().toLowerCase();
        String toLanguage = cmd.getToLanguage().toLowerCase();
        String inputFilePath = cmd.getInputFile();
        String outputFilePath = cmd.getOutputFile();
        String taskString = cmd.getTask();

        if (!translators.containsKey(fromLanguage) || !translators.containsKey(toLanguage)) {
            System.err.println("Unsupported language. Supported languages: " + translators.keySet());
            return;
        }

        // Read source code (from file or stdin)
        String code = readCode(inputFilePath);


        // Instantiate source-language translator
        LanguageTranslator fromTranslator =
                translators.get(fromLanguage).getDeclaredConstructor().newInstance();
        //fromTranslator.setConfig(new Config(new SkipErrors(true)));
        var mt = fromTranslator.getMeaningTree(code);

        // If a task is specified, convert the string to the enum and generate the refactor problem
        if (taskString != null) {
            AugletsRefactorProblemsType taskEnum = parseTask(taskString);
            if (taskEnum == null) {
                // parseTask already printed an error message
                return;
            }
            var problem = AugletsRefactorProblemsGenerator.generate(mt, taskEnum, true, Collections.emptyMap());
            mt = problem.problemMeaningTree();
        }

        // Instantiate target-language translator
        LanguageTranslator toTranslator =
                translators.get(toLanguage).getDeclaredConstructor().newInstance();
        //toTranslator.setConfig(new Config(new SkipErrors(true)));

        // Generate the final code
        String translatedCode = toTranslator.getCode(mt);

        if ("-".equals(outputFilePath)) {
            System.out.println(translatedCode);
        } else {
            try (PrintWriter out = new PrintWriter(new FileWriter(outputFilePath))) {
                out.print(translatedCode);
            }
        }
    }

    /**
     * Convert a lowercase-underscore task string into the corresponding enum constant.
     * For example, "add_dangling_else" -> AugletsRefactorProblemsType.ADD_DANGLING_ELSE.
     * If the conversion fails, prints an error and returns null.
     */
    private static AugletsRefactorProblemsType parseTask(String taskString) {
        if (taskString == null) {
            return null;
        }
        // Convert to uppercase, matching the enum's naming
        String normalized = taskString.trim().toUpperCase(Locale.ROOT);
        try {
            return AugletsRefactorProblemsType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid task: '" + taskString + "'. Valid tasks are:");
            for (AugletsRefactorProblemsType t : AugletsRefactorProblemsType.values()) {
                System.err.println("  - " + t.name().toLowerCase(Locale.ROOT));
            }
            return null;
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
