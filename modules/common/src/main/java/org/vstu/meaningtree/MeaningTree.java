package org.vstu.meaningtree;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vstu.meaningtree.nodes.Node;

import javax.swing.*;

public class MeaningTree {
    private final Node _rootNode;

    public MeaningTree(Node rootNode) {
        _rootNode = rootNode;
    }

    public Node getRootNode() {
        return _rootNode;
    }

    public String generateDot() {
        return normalizeDot("graph MeaningTree {\ndpi=200;\n" + _rootNode.generateDot() + "}");
    }

    public void show() throws IOException {
        String dotString = generateDot();

        MutableGraph tree = new Parser().read(dotString);

        File outputImage = new File("tree.png");
        Graphviz.fromGraph(tree).render(Format.PNG).toFile(outputImage);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel(new ImageIcon(outputImage.getAbsolutePath()));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private static String normalizeDot(String dot) {
        String[] lines = dot.split("\n");

        StringBuilder connections = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            if (line.contains("--") || line.equals("}")) {
                connections.append(line).append("\n");
            }
            else {
                result.append(line).append("\n");
            }
        }

        result.append(connections);

        return result.toString();
    }

}

