package org.vstu.meaningtree;
import org.vstu.meaningtree.languages.parsers.JavaLanguage;
import org.vstu.meaningtree.languages.viewers.JavaViewer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "false");
        JavaLanguage javaLanguage = new JavaLanguage();
        MeaningTree mt = javaLanguage.getMeaningTree("{10 + 231; {124 + 143;} 31 + 341;}");
        //mt.show();
        JavaViewer javaViewer = new JavaViewer();
        System.out.println(javaViewer.toString(mt.getRootNode()));
    }
}