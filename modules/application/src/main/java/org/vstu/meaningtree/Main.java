package org.vstu.meaningtree;
import org.vstu.meaningtree.languages.parsers.PythonLanguage;
import org.vstu.meaningtree.languages.viewers.JavaViewer;
import org.vstu.meaningtree.utils.Visualizer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "false");
        PythonLanguage pythonLanguage = new PythonLanguage();
        MeaningTree mt = pythonLanguage.getMeaningTree("if a > b:\n\ta = av + 1");
        Visualizer visualizer = new Visualizer(mt);
        visualizer.visualize();
    }
}