package org.vstu.meaningtree;
import org.vstu.meaningtree.languages.parsers.PythonLanguage;

public class Main {
    public static void main(String[] args) {
        PythonLanguage pyLanguage = new PythonLanguage();
        MeaningTree mt = pyLanguage.getMeaningTree("if a:\n\ta * b\nelif b:\n\tt + 1\nelse:\n\tpass");
    }
}