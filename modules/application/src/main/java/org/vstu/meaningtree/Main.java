package org.vstu.meaningtree;
import org.vstu.meaningtree.languages.parsers.JavaLanguage;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "false");
        JavaLanguage javaLanguage = new JavaLanguage();
        MeaningTree mt = javaLanguage.getMeaningTree("if (5 + 10) {12 * 55; if (12 - 6) {12; 13; 15;}}");
        mt.show();
        System.out.println(mt.generateDot());
    }
}