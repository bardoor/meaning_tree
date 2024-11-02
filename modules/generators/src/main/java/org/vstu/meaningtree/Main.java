package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.*;

public class Main {

    public static void main(String[] args) {
        var language = new JavaLanguage();
        var viewer = new JavaViewer();

        var code = "if (a < b) { max = b; } else { max = a; }";
        var mt = language.getMeaningTree(code);

        var modifedMt = AugletsRefactorProblemsGenerator.generate(
                mt,
                AugletsRefactorProblemsType.ADD_USELESS_CONDITION_CHECKING_IN_ELSE,
                true
        );

        var convertedCode = viewer.toString(modifedMt);
        System.out.println(convertedCode);
    }
}