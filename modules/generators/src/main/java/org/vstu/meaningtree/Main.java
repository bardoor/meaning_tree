package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.*;

public class Main {

    public static void main(String[] args) {
        var language = new JavaLanguage();
        var viewer = new JavaViewer();

        var code = "while (a < b) { variable++; }";
        var mt = language.getMeaningTree(code);

        var modifedMt = AugletsRefactorProblemsGenerator.generate(
                mt,
                AugletsRefactorProblemsType.WRAP_WHILE_LOOP_AND_REPLACE_IT_WITH_DO_WHILE,
                true
        );

        modifedMt = AugletsRefactorProblemsGenerator.generate(
                modifedMt,
                AugletsRefactorProblemsType.ADD_DANGLING_ELSE,
                true
        );

        var convertedCode = viewer.toString(modifedMt);
        System.out.println(convertedCode);
    }
}