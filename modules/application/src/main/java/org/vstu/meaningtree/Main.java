package org.vstu.meaningtree;
import org.vstu.meaningtree.languages.parsers.JavaLanguage;

public class Main {
    public static void main(String[] args) {
        JavaLanguage javaLanguage = new JavaLanguage();
        MeaningTree mt = javaLanguage.getMeaningTree("if (5 + 10) {}");
        System.out.println(mt);
    }
}