package org.vstu.meaningtree;
import org.vstu.meaningtree.languages.parsers.JavaLanguage;
import org.vstu.meaningtree.languages.viewers.JavaViewer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "false");
        JavaLanguage javaLanguage = new JavaLanguage();
        MeaningTree mt = javaLanguage.getMeaningTree("for (int i = 0; i < 10; i = i + 1) { int b = 10 + 4; for (int i = 0; i < 10; i = i + 1) { int b = 10 + 4; }}");
        //mt.show();
        JavaViewer jv = new JavaViewer();
        System.out.println(jv.toString(mt.getRootNode()));
    }
}