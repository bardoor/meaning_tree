package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.*;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        var language = new JavaLanguage();
        language.setConfig(List.of(LanguageTranslator.getPredefinedCommonConfig()));
        var viewer = new JavaViewer();

        var code = "while (a < b) { variable++; }";
        var mt = language.getMeaningTree(code);

        var auglet = AugletsRefactorProblemsGenerator.generate(
                mt,
                AugletsRefactorProblemsType.WRAP_WHILE_LOOP_AND_REPLACE_IT_WITH_DO_WHILE,
                true,
                Map.of()
        );

        var convertedCode = viewer.toString(auglet.problemMeaningTree());
        System.out.println(convertedCode);
    }
}