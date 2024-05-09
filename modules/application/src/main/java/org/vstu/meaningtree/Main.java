package org.vstu.meaningtree;
import org.vstu.meaningtree.languages.parsers.JavaLanguage;
import org.vstu.meaningtree.languages.parsers.PythonLanguage;
import org.vstu.meaningtree.languages.viewers.JavaViewer;
import org.vstu.meaningtree.utils.Visualizer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "false");
        PythonLanguage pythonLanguage = new PythonLanguage();
        /*
        String pythonCode =
                "max_number = a\n" +
                "if b > max_number:\n" +
                "\tmax_number = b\n" +
                "elif c > max_number:\n" +
                "\tmax_number = c\n" +
                "elif d > max_number:\n" +
                "\tmax_number = d\n" +
                "else:\n" +
                "\tmax_number = max_number + 41";
         */
        JavaLanguage jl = new JavaLanguage();
        MeaningTree mt = jl.getMeaningTree("int a, b = 10, c;");
        JavaViewer jv = new JavaViewer();
        System.out.println(jv.toString(mt.getRootNode()));
        //Visualizer visualizer = new Visualizer(mt);
        //visualizer.visualize();
    }
}