package org.example;
import languages.parsers.JavaLanguage;
import meaning_tree.MeaningTree;
import org.treesitter.*;

public class Main {
    public static void main(String[] args) {
        JavaLanguage javaLanguage = new JavaLanguage();
        MeaningTree mt = javaLanguage.getMeaningTree("if (5 + 10) {}");
    }
}